package pro.shushi.pamirs.framework.compute.process.definition.field;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.FieldComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.util.FieldUtils;

/**
 * 序列生成计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class SequenceFieldComputer<T> implements FieldComputer<Meta, T> {

    @Override
    public Result<Void> compute(ComputeContext context, Meta meta, ModelField field, T data) {
        Result<Void> result = new Result<>();
        ModelFieldConfig modelField = PamirsSession.getContext().getModelField(field.getModel(), field.getField());
        // 计算序列
        SequenceConfig sequenceConfig = modelField.getSequenceConfig();
        if (null != sequenceConfig
                && StringUtils.isNotBlank(sequenceConfig.getSequence())) {
            Object fieldValue = FieldUtils.getFieldValue(data, modelField.getLname());
            if (null == fieldValue) {
                Object sequence = CommonApiFactory.getSequenceGenerator().generate(sequenceConfig.getSequence(), modelField.getModel() + modelField.getField());
                FieldUtils.setFieldValue(data, modelField.getLname(), sequence);
            }
        }
        return result;
    }

}
