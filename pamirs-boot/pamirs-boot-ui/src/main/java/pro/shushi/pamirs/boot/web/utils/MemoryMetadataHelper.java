package pro.shushi.pamirs.boot.web.utils;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.model.UeModel;
import pro.shushi.pamirs.boot.web.manager.UiIoManager;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLHelper;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfo;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlSearchOperation;
import pro.shushi.pamirs.framework.gateways.rsql.connector.RSQLNodeConnector;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionFillOwnSignApi;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.spi.Holder;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 内存元数据帮助类
 *
 * @author Adamancy Zhang at 17:15 on 2024-07-22
 */
public class MemoryMetadataHelper {

    public static <T extends ModuleDefinition> List<ModuleDefinition> fetchMemoryModules(List<T> moduleDefinitions) {
        return fetchMemoryModules(moduleDefinitions, PageLoadHelper::isCurrentClientApplication);
    }

    public static <T extends ModuleDefinition> List<ModuleDefinition> fetchMemoryModules(List<T> moduleDefinitions, Predicate<ModuleDefinition> filter) {
        if (!Spider.getDefaultExtension(SessionFillOwnSignApi.class).handleOwnSign()) {
            return new ArrayList<>();
        }
        Set<String> moduleKeySet = PamirsSession.getContext().getModuleCache().keySet();
        moduleKeySet = Sets.difference(moduleKeySet, moduleDefinitions.stream().map(ModuleDefinition::getModule).collect(Collectors.toSet()));
        List<ModuleDefinition> memoryModules = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(moduleKeySet)) {
            for (String moduleKey : moduleKeySet) {
                ModuleDefinition moduleDefinition = PamirsSession.getContext().getModule(moduleKey);
                if (moduleDefinition != null && filter.evaluate(moduleDefinition)) {
                    memoryModules.add(BeanDefinitionUtils.getBean(UiIoManager.class).cloneData(moduleDefinition));
                }
            }
        }
        return memoryModules;
    }

    public static ModuleDefinition fetchMemoryModule(String module) {
        ModuleDefinition moduleDefinition = PamirsSession.getContext().getModule(module);
        if (moduleDefinition != null) {
            return BeanDefinitionUtils.getBean(UiIoManager.class).cloneData(moduleDefinition);
        }
        return null;
    }

    public static ModelDefinition fetchMemoryModel(String rsql) {
        if (StringUtils.isBlank(rsql) || !Spider.getDefaultExtension(SessionFillOwnSignApi.class).handleOwnSign()) {
            return null;
        }
        TreeNode<RSQLNodeInfo> root = RSQLHelper.parse(UeModel.MODEL_MODEL, rsql);
        String modelFieldKey = LambdaUtil.fetchFieldName(UeModel::getModel);
        Holder<String> modelValueHolder = new Holder<>();
        if (root != null) {
            RSQLHelper.toTargetString(root, new RSQLNodeConnector() {
                @Override
                public String comparisonConnector(RSQLNodeInfo nodeInfo) {
                    String field = nodeInfo.getField();
                    if (modelFieldKey.equals(field)) {
                        if (RsqlSearchOperation.LIKE.getOperator().equals(nodeInfo.getOperator()) || RsqlSearchOperation.EQUAL.getOperator().equals(nodeInfo.getOperator())) {
                            modelValueHolder.set(nodeInfo.getArguments().get(0));
                        } else if (RsqlSearchOperation.IN.getOperator().equals(nodeInfo.getOperator())) {
                            modelValueHolder.set(nodeInfo.getArguments().get(0));
                        }
                    }
                    return super.comparisonConnector(nodeInfo);
                }
            });
        }
        String modelValue = modelValueHolder.get();
        if (StringUtils.isNotBlank(modelValue)) {
            if (modelValue.startsWith(CharacterConstants.PERCENT)) {
                modelValue = modelValue.substring(1);
            }
            if (modelValue.endsWith(CharacterConstants.PERCENT)) {
                modelValue = modelValue.substring(0, modelValue.length() - 1);
            }
            ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(modelValue);
            if (modelConfig != null) {
                return BeanDefinitionUtils.getBean(UiIoManager.class).cloneData(modelConfig.getModelDefinition());
            }
        }
        return null;
    }

    public static ModelDefinition fetchMemoryModelByModel(String model) {
        ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(model);
        if (modelConfig != null) {
            return BeanDefinitionUtils.getBean(UiIoManager.class).cloneData(modelConfig.getModelDefinition());
        }
        return null;
    }
}
