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
        if (StringUtils.isBlank(value)) {
            return value;
        } else if ("TRUE".equalsIgnoreCase(value) || "1".equals(value)) {
            return "true";
        } else if ("FALSE".equalsIgnoreCase(value) || "0".equals(value)) {
            return "false";
        } else {
            log.debug("can not convert {} to boolean", value);
            return defaultValue();
        }
    }

    @Override
    public String defaultValue() {
        return "false";
    }
}
