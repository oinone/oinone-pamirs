package pro.shushi.pamirs.framework.compute.process.definition;

import org.apache.commons.collections4.MapUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.definition.ModelComputer;
import pro.shushi.pamirs.meta.api.core.compute.template.LifeDataComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.model.Relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_MODEL_CONFIG_IS_NOT_EXISTS_ERROR;

/**
 * 元数据计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Order
@Component
@SPI.Service
public class DefaultLifeDataComputer<T> implements LifeDataComputer<Meta, List<T>> {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Result<Void> compute(String model, ComputeContext context, Meta meta, List<T> data, ModelComputer modelComputer, FieldComputer... fieldComputers) {
        Result<Void> result = new Result<>();
        List<T> dataList = new ArrayList<>(data);

        ModelDefinition modelDefinition = meta.getModel(model);
        if (null == modelDefinition) {
            throw PamirsException.construct(BASE_MODEL_CONFIG_IS_NOT_EXISTS_ERROR)
                    .appendMsg("model：" + model).errThrow();
        }

        if (null != fieldComputers) {
            List<ModelField> modelFieldList = modelDefinition.getModelFields();
            if (!CollectionUtils.isEmpty(modelFieldList)) {
                Map<String, ModelField> computeModelFieldMap = new HashMap<>();
                dealComputeFields(modelFieldList, computeModelFieldMap);
                if (ModelField.MODEL_MODEL.equals(model)) {
                    List<ModelField> relationModelFieldList = meta.getModel(Relation.MODEL_MODEL).getModelFields();
                    dealComputeFields(relationModelFieldList, computeModelFieldMap);
                }
                if (MapUtils.isEmpty(computeModelFieldMap)) {
                    return result;
                }
                for (T item : dataList) {
                    if (Models.modelDirective().isMetaCompleted(item)) {
                        continue;
                    }
                    for (ModelField computeField : computeModelFieldMap.values()) {
                        for (FieldComputer fieldComputer : fieldComputers) {
                            if (Models.modelDirective().isMetaCompleted(item)
                                    || (Models.modelDirective().isMetaDiffing(computeField)
                                    && Models.modelDirective().isMetaCompleted(computeField)/*元数据定义未发生差量*/)) {
                                continue;
                            }
                            Result computeResult = fieldComputer.compute(context, meta, computeField, item);
                            result.fill(computeResult);
                            if (!result.isSuccess()) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (null != modelComputer) {
            for (T item : dataList) {
                if (Models.modelDirective().isMetaCompleted(item)
                        || (Models.modelDirective().isMetaDiffing(modelDefinition)
                        && Models.modelDirective().isMetaCompleted(modelDefinition)/*元数据定义未发生差量*/)) {
                    continue;
                }
                Result computeResult = modelComputer.compute(context, meta, model, item, new HashMap<>());
                result.fill(computeResult);
            }
        }

        return result;
    }

    private void dealComputeFields(List<ModelField> modelFieldList, Map<String, ModelField> computeModelFieldMap) {
        for (ModelField modelField : modelFieldList) {
            if (computeModelFieldMap.containsKey(modelField.getField())) {
                continue;
            }
            computeModelFieldMap.put(modelField.getField(), modelField);
        }
    }

}
