package pro.shushi.pamirs.framework.compute.process.data.field;

import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.data.FieldComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

/**
 * 序列生成计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class SequenceFieldComputer<T> implements FieldComputer<T> {

    @Override
    public Result<Void> compute(ComputeContext context, ModelFieldConfig field, T data) {
        return new pro.shushi.pamirs.framework.compute.process.definition.field.SequenceFieldComputer<T>()
                .compute(context, null, field.getModelField(), data);
    }

}
