package pro.shushi.pamirs.framework.compute.process.data.field;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.data.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.definition.ValueComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.domain.model.ModelField;

/**
 * 字段默认值计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class DefaultFieldComputer<T> implements FieldComputer<T> {

    @Override
    public Result<Void> compute(ComputeContext context, ModelFieldConfig field, T data) {
        Result<Void> result = new Result<>();
        ModelField modelField = field.getModelField();

        // 计算默认值
        ModelFieldConfig modelFieldConfig = Models.compute().convert(modelField);
        if (StringUtils.isNotBlank(modelFieldConfig.getDefaultValue())) {
            ValueComputer.get().compute(modelFieldConfig, data);
        }

        return result;
    }

}
