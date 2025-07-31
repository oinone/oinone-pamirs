package pro.shushi.pamirs.eip.api.type.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.type.ExcelTTypeDescriptor;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.math.BigDecimal;

/**
 * @author Gesi at 16:00 on 2025/7/18
 */
@Component
@Slf4j
public class ExcelTTypeBoolConverter implements ExcelTTypeConverter {

    @Override
    public boolean canConvert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        return TtypeEnum.BOOLEAN.value().equals(excelTTypeDescriptor.getTargetType())
                || "boolean".equals(excelTTypeDescriptor.getTargetType());
    }

    @Override
    public String convert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        String value = excelTTypeDescriptor.getValue();
        try {
            switch (excelTTypeDescriptor.getOriginType()) {
                case "bool":
                case "boolean":
                    return Boolean.parseBoolean(value) + "";
                case "binary":
                case "integer":
                case "uid":
                case "float":
                case "money": {
                    BigDecimal bigDecimal = new BigDecimal(value);
                    return (bigDecimal.compareTo(BigDecimal.ZERO) != 0) + "";
                }
                case "string":
                case "text":
                case "phone":
                case "email":
                case "html":
                    return StringUtils.isNotBlank(value) + "";
                default:
                    throw new IllegalArgumentException(value + "can not convert to boolean");
            }
        } catch (Exception e) {
            log.debug("can not convert {} to boolean", value, e);
            return defaultValue();
        }
    }

    @Override
    public String defaultValue() {
        return "false";
    }
}
