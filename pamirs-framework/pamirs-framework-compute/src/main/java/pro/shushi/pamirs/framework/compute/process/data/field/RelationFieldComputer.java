package pro.shushi.pamirs.framework.compute.process.data.field;

import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.data.FieldComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.List;

import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_RELATION_FIELD_CONFIG_IS_NOT_EXISTS_ERROR;

/**
 * 关联关系字段计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class RelationFieldComputer<T> implements FieldComputer<T> {

    @Override
    public Result<Void> compute(ComputeContext context, ModelFieldConfig field, T data) {
        Result<Void> result = new Result<>();
        // 计算关联关系字段
        if (field.getRelationStore()) {
            if (TtypeEnum.M2O.value().equals(field.getTtype())
                    || TtypeEnum.O2O.value().equals(field.getTtype())) {
                List<String> relationFields = field.getRelationFields();
                if (null == relationFields) {
                    throw PamirsException.construct(BASE_RELATION_FIELD_CONFIG_IS_NOT_EXISTS_ERROR)
                            .appendMsg("model:" + field.getModel() + ", field:" + field).errThrow();
                }
                Object fieldValue = FieldUtils.getFieldValue(data, field.getLname());
                if (null == fieldValue) {
                    return result;
                }
                int fieldIndex = 0;
                for (String relationField : relationFields) {
                    if (FieldUtils.isConstantRelationFieldValue(relationField)) {
                        fieldIndex++;
                        continue;
                    }

                    ModelFieldConfig selfField = PamirsSession.getContext().getModelField(field.getModel(), relationField);
                    Object referenceValue = FieldUtils
                            .getReferenceFieldValue(fieldValue, field.getReferences(), field.getReferenceFields().get(fieldIndex));
                    if (null != referenceValue) {
                        FieldUtils.setFieldValue(data, selfField.getLname(), referenceValue);
                    }
                    fieldIndex++;
                }
            }
        }
        return result;
    }

}
