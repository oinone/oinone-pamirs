package pro.shushi.pamirs.framework.compute.process.definition.field;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.definition.ValueComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.domain.model.ModelField;

/**
 * 字段默认值计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class DefaultFieldComputer<T> implements FieldComputer<Meta, T> {

    @Override
    public Result<Void> compute(ComputeContext context, Meta meta, ModelField field, T data) {
        Result<Void> result = new Result<>();

        // 计算默认值
        ModelFieldConfig modelFieldConfig = Models.compute().convert(field);
        if (StringUtils.isNotBlank(modelFieldConfig.getDefaultValue())) {
            ValueComputer.get().compute(modelFieldConfig, data);
        }

        return result;
    }

}
