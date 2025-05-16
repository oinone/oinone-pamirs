package pro.shushi.pamirs.framework.common.core;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.Exp;
import pro.shushi.pamirs.meta.api.core.compute.definition.ValueComputer;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 字段默认值表达式计算
 *
 * @author Adamancy Zhang at 11:49 on 2025-04-09
 */
@SPI.Service(NamespaceConstants.expression)
@Component
public class ExpressionDefaultValueComputer implements ValueComputer {

    @Override
    public <T> void compute(ModelFieldConfig modelField, T data) {
        Object fieldValue = FieldUtils.getFieldValue(data, modelField.getLname());
        if (null == fieldValue) {
            String lname = modelField.getLname();
            String defaultValue = modelField.getDefaultValue();
            if (defaultValue.startsWith("${") && defaultValue.endsWith("}")) {
                FieldUtils.setFieldValue(data, lname, Exp.fastRun(defaultValue.substring(2, defaultValue.length() - 1), generatorComputeContext(data)));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> generatorComputeContext(Object data) {
        if (data instanceof D) {
            return ((D) data).get_d();
        } else if (data instanceof Map) {
            return (Map<String, Object>) data;
        }
        return new HashMap<>();
    }
}
