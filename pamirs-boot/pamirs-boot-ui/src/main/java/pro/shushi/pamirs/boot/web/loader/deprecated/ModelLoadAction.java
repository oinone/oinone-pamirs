package pro.shushi.pamirs.boot.web.loader.deprecated;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.boot.web.manager.UiIoManager;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.faas.utils.ArgUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用于前端的元数据加载
 *
 * @author shier
 * date  2020/11/30 11:30 上午
 */
@Base
@Component
@Slf4j
@Fun(UeModel.MODEL_MODEL)
public class ModelLoadAction extends AbstractLoadAction {

    @Resource
    private MetaCacheManager metaCacheManager;

    @Resource
    private UiIoManager uiIoManager;

    @Base
    @Function(summary = "提供给前端加载model的元数据", openLevel = FunctionOpenEnum.API)
    @Function.Advanced(displayName = "获取模型元数据", type = FunctionTypeEnum.QUERY)
    public UeModel load(UeModel modelDefinition) {
        modelDefinition = loadModel(modelDefinition);
        loadModelField0(modelDefinition);
        loadView0(modelDefinition);
        loadAction0(modelDefinition);
        return modelDefinition;
    }

    @Base
    @Function(summary = "提供给前端加载model的动作", openLevel = FunctionOpenEnum.API)
    @Function.Advanced(displayName = "获取模型动作元数据", type = FunctionTypeEnum.QUERY)
    public UeModel loadAction(UeModel modelDefinition) {
        modelDefinition = loadModel(modelDefinition);
        loadView0(modelDefinition);
        loadAction0(modelDefinition);
        return modelDefinition;
    }

    @Function(summary = "提供给前端加载model的字段元数据", openLevel = FunctionOpenEnum.API)
    @Function.Advanced(displayName = "获取模型字段元数据", type = FunctionTypeEnum.QUERY)
    public UeModel loadModelField(UeModel modelDefinition) {
        modelDefinition = loadModel(modelDefinition);
        loadModelField0(modelDefinition);
        return modelDefinition;
    }

    @NotNull
    private UeModel loadModel(UeModel modelDefinition) {
        // 约定: 前端只允许使用模型编码查询模型元数据
        String model = modelDefinition.getModel();
        if (StringUtils.isBlank(model)) {
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_LOAD_MODEL_MODEL_IS_NULL_ERROR).errThrow();
        }
        ModelDefinition cacheModel = Optional.ofNullable(PamirsSession.getContext().getModelConfig(model))
                .map(ModelConfig::getModelDefinition)
                .orElse(null);
        if (cacheModel == null) {
            cacheModel = new UeModel().queryOneByWrapper(Pops.<UeModel>lambdaQuery()
                    .from(UeModel.MODEL_MODEL)
                    .eq(UeModel::getModel, model));
            if (cacheModel == null) {
                log.error("Invalid model. model = {}", model);
                throw PamirsException.construct(BootUxdExpEnumerate.BASE_LOAD_MODEL_META_MODEL_DATA_ERROR).errThrow();
            }
            log.warn("model cache is loss. model = {}", model);
        }
        ModelDefinition cloneModel = BeanDefinitionUtils.getBean(UiIoManager.class).cloneData(cacheModel);
        cloneModel.unsetModelFields();
        return ArgUtils.convert(ModelDefinition.MODEL_MODEL, ModelDefinition.UE_MODEL_MODEL, cloneModel);
    }

    private void loadModelField0(UeModel modelDefinition) {
        String model = modelDefinition.getModel();
        List<ModelField> results = new ArrayList<>();
        UiIoManager manager = BeanDefinitionUtils.getBean(UiIoManager.class);
        List<ModelFieldConfig> modelFieldConfigList = Optional.ofNullable(PamirsSession.getContext().getModelConfig(model))
                .map(ModelConfig::getModelFieldConfigList)
                .orElse(null);
        if (CollectionUtils.isEmpty(modelFieldConfigList)) {
            modelDefinition.setModelFields(results);
            return;
        }
        for (ModelFieldConfig modelFieldConfig : modelFieldConfigList) {
            ModelField cloneField = manager.cloneData(modelFieldConfig.getModelField());
            cloneField.unsetModelDefinition();
            results.add(cloneField);
        }
        modelDefinition.setModelFields(results.stream().sorted(ModelFieldComparator.INSTANCE).collect(Collectors.toList()));
    }

    private void loadView0(UeModel modelDefinition) {
        String model = modelDefinition.getModel();
        List<View> views = new View().setModel(model).setActive(ActiveEnum.ACTIVE).queryList();
        modelDefinition.setViewList(views);
    }

    private void loadAction0(UeModel modelDefinition) {
        String model = modelDefinition.getModel();
        List<Action> actions = metaCacheManager.fetchActions(model);
        if (CollectionUtils.isEmpty(actions)) {
            return;
        }
        List<ViewAction> viewActions = new ArrayList<>();
        List<ServerAction> serverActions = new ArrayList<>();
        List<UrlAction> urlActions = new ArrayList<>();
        List<ClientAction> clientActions = new ArrayList<>();
        for (Action action : actions) {
            if (action instanceof ViewAction) {
                viewActions.add(uiIoManager.cloneData((ViewAction) action));
            } else if (action instanceof ServerAction) {
                serverActions.add(uiIoManager.cloneData((ServerAction) action));
            } else if (action instanceof UrlAction) {
                urlActions.add(uiIoManager.cloneData((UrlAction) action));
            } else if (action instanceof ClientAction) {
                clientActions.add(uiIoManager.cloneData((ClientAction) action));
            } else {
                log.error("Invalid action cache. model = {}, actionName = {}", model, action.getName());
            }
        }
        modelDefinition.setViewActionList(viewActions);
        modelDefinition.setServerActionList(serverActions);
        modelDefinition.setUrlActionList(urlActions);
        modelDefinition.setClientActionList(clientActions);
    }

    private static class ModelFieldComparator implements Comparator<ModelField> {

        private static final Comparator<ModelField> INSTANCE = new ModelFieldComparator();

        @Override
        public int compare(ModelField o1, ModelField o2) {
            if (o1.getPriority() != null && o2.getPriority() != null) {
                return (int) (o1.getPriority() - o2.getPriority());
            }
            return 0;
        }
    }
}
