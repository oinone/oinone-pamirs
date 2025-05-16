package pro.shushi.pamirs.framework.compare.core;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.common.spi.CrossingFunctionExtendApi;
import pro.shushi.pamirs.framework.compare.CrossingExtendService;
import pro.shushi.pamirs.framework.compare.utils.CrossingComputer;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.fun.ExtPoint;
import pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.ArrayList;
import java.util.List;

/**
 * 跨模块增量扩展实现
 * 2021/2/4 11:02 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
public class DefaultCrossingExtendService implements CrossingExtendService {

    @Override
    public void diffCompute(List<Meta> metaList) {
        for (Meta meta : metaList) {
            // 跨模块字段挂载处理
            List<ModelField> standAloneFieldList = meta.getCurrentModuleData().getStandAloneFieldList();
            if (!CollectionUtils.isEmpty(standAloneFieldList)) {
                for (ModelField modelField : standAloneFieldList) {
                    ModelDefinition modelOfField = meta.getModel(modelField.getModel());
                    if (null != modelOfField) {
                        if (null == modelOfField.getModelFields()) {
                            modelOfField.setModelFields(new ArrayList<>());
                        } else {
                            // 本模块字段优先级高于跨模块多对一追加关系字段和继承依赖模块模型字段，所以丢弃跨模块元数据
                            if (modelOfField.getModelFields().stream().anyMatch(v -> modelField.getField().equals(v.getField()))) {
                                continue;
                            }
                        }
                        modelOfField.getModelFields().add(modelField);
                        // 记录跨模块加载的字段
                        meta.getData().get(modelOfField.getModule())
                                .addCrossingExtendData(ModelField.MODEL_MODEL, modelField.getSign(), meta.getModule());
                    }
                }
            }
            // 跨模块方法挂载处理
            List<FunctionDefinition> standAloneFunctionList = meta.getCurrentModuleData().getStandAloneFunctionList();
            if (!CollectionUtils.isEmpty(standAloneFunctionList)) {
                for (FunctionDefinition functionDefinition : standAloneFunctionList) {
                    ModelDefinition modelOfFunction = meta.getModel(functionDefinition.getNamespace());
                    if (null != modelOfFunction) {
                        MetaData metaDataOfModel = meta.getData().get(modelOfFunction.getModule());
                        if (functionDefinition.isMetaCompleted()) {
                            // 移除跨模块元数据注册表差异记录
                            metaDataOfModel.removeDiffModelData(FunctionDefinition.MODEL_MODEL, functionDefinition.getSign());
                            continue;
                        }
                        int existIndex = -1;
                        if (null == modelOfFunction.getFunctions()) {
                            modelOfFunction.setFunctions(new ArrayList<>());
                        } else {
                            int functionIndex = 0;
                            for (FunctionDefinition existFunctionDefinition : modelOfFunction.getFunctions()) {
                                if (functionDefinition.getFun().equals(existFunctionDefinition.getFun())) {
                                    existIndex = functionIndex;
                                    break;
                                }
                                functionIndex++;
                            }
                        }
                        if (-1 == existIndex) {
                            modelOfFunction.getFunctions().add(functionDefinition);
                        } else {
                            modelOfFunction.getFunctions().remove(existIndex);
                            modelOfFunction.getFunctions().add(existIndex, functionDefinition);
                        }
                        // 移除跨模块元数据注册表差异记录
                        metaDataOfModel.removeDiffModelData(FunctionDefinition.MODEL_MODEL, functionDefinition.getSign());
                        // 记录跨模块加载的函数
                        metaDataOfModel.addCrossingExtendData(FunctionDefinition.MODEL_MODEL, functionDefinition.getSign(), functionDefinition.getModule());
                    }
                }
            }

            // 扩展逻辑
            Spider.getDefaultExtension(CrossingFunctionExtendApi.class).extend(meta);

            // 跨模块扩展点挂载处理
            CrossingComputer.crossingFunCompute(meta, ExtPoint.MODEL_MODEL, ExtPoint::getNamespace, ExtPoint::getNamespace, ExtPoint::getName);

            // 跨模块扩展点实现挂载处理
            CrossingComputer.crossingFunCompute(meta, ExtPointImplementation.MODEL_MODEL, ExtPointImplementation::getNamespace,
                    ExtPointImplementation::getExecuteNamespace, ExtPointImplementation::getExecuteFun);
        }
    }

}
