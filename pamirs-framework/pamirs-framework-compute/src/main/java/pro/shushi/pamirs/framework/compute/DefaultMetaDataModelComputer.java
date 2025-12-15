package pro.shushi.pamirs.framework.compute;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.compute.exception.MetaDataComputeException;
import pro.shushi.pamirs.framework.configure.MetaConfiguration;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.CrossingInheritedComputer;
import pro.shushi.pamirs.meta.api.core.compute.MetaDataModelComputer;
import pro.shushi.pamirs.meta.api.core.compute.ModelDefinitionComputer;
import pro.shushi.pamirs.meta.api.core.compute.PamirsDataComputer;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.MetaDataExtendComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.base.AbstractModel;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import javax.annotation.Resource;
import java.util.*;

/**
 * 元数据定义计算器
 *
 * @author Adamancy Zhang at 19:04 on 2025-02-21
 */
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultMetaDataModelComputer implements MetaDataModelComputer {

    @Resource
    protected MetaConfiguration metaConfiguration;

    @SuppressWarnings("rawtypes")
    @Override
    public void compute(ComputeContext context, List<Meta> metaList, Set<String> completedModuleSet) {
        List<MetaDataExtendComputer> extendComputers = BeanDefinitionUtils.getBeansOfTypeByOrdered(MetaDataExtendComputer.class);
        ModelDefinitionComputer modelDefinitionComputer = Spider.getDefaultExtension(ModelDefinitionComputer.class);
        final Set<String> extendComputeModuleSet = new HashSet<>();
        final Set<String> crossingComputeModuleSet = new HashSet<>();
        compute0(context, metaList, modelDefinitionComputer, extendComputers, completedModuleSet, extendComputeModuleSet, crossingComputeModuleSet);
    }

    @SuppressWarnings("rawtypes")
    private void compute0(ComputeContext context, List<Meta> metaList,
                          ModelDefinitionComputer modelDefinitionComputer,
                          List<MetaDataExtendComputer> extendComputers,
                          Set<String> completedModuleSet,
                          Set<String> extendComputeModuleSet,
                          Set<String> crossingComputeModuleSet) {
        for (Meta meta : metaList) {
            // 元数据计算
            Result<Void> result = modelDefinitionComputer.compute(context, meta, completedModuleSet);
            // 元数据扩展计算
            long start = System.currentTimeMillis();
            Result<Void> extendComputeResult = extendCompute(extendComputers, extendComputeModuleSet, context, meta);
            log.info("元数据扩展计算 {}ms", System.currentTimeMillis() - start);
            result.fill(extendComputeResult);
            result.logMessages(metaConfiguration.getLogLevel());
            if (!result.isSuccess()) {
                throw new MetaDataComputeException();
            }
        }
        for (Meta meta : metaList) {
            // 元数据跨模型继承计算
            long start = System.currentTimeMillis();
            crossingCompute(crossingComputeModuleSet, meta);
            log.info("元数据跨模型继承计算完成 {}ms, module: {}", System.currentTimeMillis() - start, meta.getModule());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Result<Void> extendCompute(List<MetaDataExtendComputer> extendComputers,
                                         Set<String> extendComputeModuleSet,
                                         ComputeContext context, Meta meta) {
        Result<Void> result = new Result<>();
        for (String module : meta.getData().keySet()) {
            if (extendComputeModuleSet.contains(module)) {
                continue;
            }
            extendComputeModuleSet.add(module);
            MetaData metaData = meta.getData().get(module);
            Map<String/*meta model*/, Map<String/*model sign*/, MetaBaseModel>> data = metaData.getData();
            if (null != data) {
                // 模型、字典、扩展点
                for (String model : data.keySet()) {
                    // 非元模型数据计算
                    if (ModelDefinition.MODEL_MODEL.equals(model)
                            || ModelDefinition.UE_MODEL_MODEL.equals(model)
                            || ModelField.MODEL_MODEL.equals(model)
                            || ModelField.UE_MODEL_MODEL.equals(model)
                            || ModuleDefinition.MODEL_MODEL.equals(model)
                            || ModuleDefinition.UE_MODEL_MODEL.equals(model)) {
                        continue;
                    }
                    Map<String/*model sign*/, MetaBaseModel> dataMap = data.get(model);

                    // 计算默认值、模型约束、引用字段、关系字段、字段约束
                    result.fill(computeMetaData(context, model, dataMap));

                    for (MetaDataExtendComputer computer : extendComputers) {
                        if (computer.canCompute(model)) {
                            for (AbstractModel abstractModel : dataMap.values()) {
                                if (Models.modelDirective().isMetaCompleted(abstractModel)) {
                                    continue;
                                }
                                computer.compute(meta, model, abstractModel);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    protected <T> Result<Void> computeMetaData(ComputeContext context, String model, Map<String, T> dataMap) {
        List<T> dataList = ListUtils.toList(dataMap.values());
        Models.orm().modeling(model, dataList);
        // 数据计算
        return CommonApiFactory.getApi(PamirsDataComputer.class).computeModelInLifecycle(context, model, dataList);
    }

    protected void crossingCompute(Set<String> crossingComputeModuleSet, Meta meta) {
        for (String module : meta.getData().keySet()) {
            if (crossingComputeModuleSet.contains(module)) {
                continue;
            }
            crossingComputeModuleSet.add(module);
            MetaData metaData = meta.getData().get(module);
            List<ModelDefinition> modelList = metaData.getModelList();
            if (!CollectionUtils.isEmpty(modelList)) {
                for (ModelDefinition modelDefinition : modelList) {
                    if (modelDefinition.isMetaCompleted()) {
                        continue;
                    }
                    InheritedComputeTemplate.compute(meta, modelDefinition, new HashMap<>(),
                            null, null,
                            true, (currentModel, superModel) -> currentModel.getModule().equals(superModel.getModule()),
                            (currentModel, superModel) -> dealCrossingInheritedField(meta, currentModel, superModel,
                                    SystemSourceEnum.ABSTRACT_INHERITED),
                            null,
                            (currentModel, superModel) -> dealCrossingInheritedField(meta, currentModel, superModel,
                                    SystemSourceEnum.MULTI_TABLE_INHERITED),
                            (currentModel, superModel) -> dealCrossingInheritedField(meta, currentModel, superModel,
                                    SystemSourceEnum.PROXY_INHERITED),
                            (currentModel, superModel) -> dealCrossingInheritedField(meta, currentModel, superModel,
                                    SystemSourceEnum.EXTEND_INHERITED),
                            null);
                }
            }
        }
    }

    protected void dealCrossingInheritedField(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel, SystemSourceEnum systemSourceEnum) {
        List<CrossingInheritedComputer> computers = Spider.getLoader(CrossingInheritedComputer.class).getOrderedExtensions();
        for (CrossingInheritedComputer computer : computers) {
            computer.compute(meta, modelDefinition, superModel, systemSourceEnum);
        }
    }
}
