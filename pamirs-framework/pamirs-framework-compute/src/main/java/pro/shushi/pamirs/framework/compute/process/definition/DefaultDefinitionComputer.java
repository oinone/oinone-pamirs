package pro.shushi.pamirs.framework.compute.process.definition;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.compute.retry.RetryManager;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.definition.FieldExtendComputer;
import pro.shushi.pamirs.meta.api.core.compute.definition.ModelComputer;
import pro.shushi.pamirs.meta.api.core.compute.definition.ModelExtendComputer;
import pro.shushi.pamirs.meta.api.core.compute.template.DefinitionComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模型计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Order
@Component
@SPI.Service
public class DefaultDefinitionComputer implements DefinitionComputer {

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Void> compute(ComputeContext context, Meta meta, boolean extendCompute, List<ModelDefinition> definitionList, ModelComputer modelComputer, FieldComputer... fieldComputers) {
        List<ModelExtendComputer> modelExtendComputers = Spider.getLoader(ModelExtendComputer.class).getOrderedExtensions();
        List<FieldExtendComputer> fieldExtendComputers = Spider.getLoader(FieldExtendComputer.class).getOrderedExtensions();
        Result<Void> result = new Result<>();
        List<ModelDefinition> dataList = new ArrayList<>(definitionList);
        if (null != modelComputer) {
            Map<String, Object> computeContext = new HashMap<>();
            try {
                RetryManager.init();
                for (ModelDefinition item : dataList) {
                    if (Models.modelDirective().isMetaDiffing(item)
                            && Models.modelDirective().isMetaCompleted(item)/*元数据定义未发生差量*/) {
                        continue;
                    }
                    computeUnit(context, meta, modelComputer, extendCompute, modelExtendComputers, computeContext, result, item);
                }
            } finally {
                // 解决循环计算问题
                RetryManager.fire(v -> computeUnit(context, meta, modelComputer, extendCompute, modelExtendComputers, computeContext, result, v));
                RetryManager.clear();
            }
        }

        if (null != fieldComputers) {
            for (ModelDefinition item : dataList) {
                List<ModelField> modelFieldList = meta.getModel(item.getModel()).getModelFields();
                if (CollectionUtils.isEmpty(modelFieldList)) {
                    continue;
                }
                List<String> fieldList = new ArrayList<>();
                for (ModelField modelField : modelFieldList) {
                    fieldList.add(modelField.getField());
                }
                for (String field : fieldList) {
                    for (FieldComputer fieldComputer : fieldComputers) {
                        ModelField modelField = meta.getModelField(item.getModel(), field);
                        if (Models.modelDirective().isMetaCompleted(item)
                                || (Models.modelDirective().isMetaDiffing(modelField)
                                && Models.modelDirective().isMetaCompleted(modelField)/*元数据定义未发生差量*/)) {
                            continue;
                        }
                        Result computeResult = fieldComputer.compute(context, meta, modelField, item);
                        result.fill(computeResult);
                        if (!result.isSuccess()) {
                            break;
                        }
                        if (extendCompute) {
                            for (FieldExtendComputer computer : fieldExtendComputers) {
                                computeResult = computer.compute(meta, item.getModel(), field, item);
                                result.fill(computeResult);
                            }
                        }
                        if (!result.isSuccess()) {
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void computeUnit(ComputeContext context, Meta meta, ModelComputer modelComputer,
                               boolean extendCompute, List<ModelExtendComputer> modelExtendComputers,
                               Map<String, Object> computeContext,
                               Result<Void> result, ModelDefinition item) {
        Result computeResult = modelComputer.compute(context, meta, item.getModel(), item, computeContext);
        result.fill(computeResult);
        if (extendCompute) {
            for (ModelExtendComputer computer : modelExtendComputers) {
                computeResult = computer.compute(meta, item.getModel(), item);
                result.fill(computeResult);
            }
        }
    }

}
