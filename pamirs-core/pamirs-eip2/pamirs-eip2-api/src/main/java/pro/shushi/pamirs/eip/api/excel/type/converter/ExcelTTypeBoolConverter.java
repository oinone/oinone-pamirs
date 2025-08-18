package pro.shushi.pamirs.eip.api.excel.type.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.excel.type.ExcelTTypeDescriptor;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.math.BigDecimal;

/**
 * @author Gesi at 16:00 on 2025/7/18
 */
@Component
@Slf4j
public class ExcelTTypeBoolConverter implements ExcelTTypeConverter {

    @Autowired
    private ExcelTTypeMoneyConverter excelTTypeMoneyConverter;

    public static boolean originIsBool(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        if ("TRUE".equalsIgnoreCase(value) || "1".equals(value) || "是".equals(value) || "Y".equalsIgnoreCase(value)) {
            return true;
        } else if ("FALSE".equalsIgnoreCase(value) || "0".equals(value) || "否".equals(value) || "N".equalsIgnoreCase(value)) {
            return true;
        }
        return false;
    }

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
                case "binary":
                case "integer":
                case "long":
                case "float":
                case "money":
                case "uid": {
                    if (StringUtils.isBlank(value)) {
                        return null;
                    }
                    String numberValue = excelTTypeMoneyConverter.convert(ExcelTTypeDescriptor.valueOf(value, TtypeEnum.STRING.value(), TtypeEnum.FLOAT.value()));
                    return (new BigDecimal(numberValue).compareTo(BigDecimal.ZERO) != 0) + "";
                }
                case "year":
                case "datetime":
                case "date":
                case "time":
                    log.debug("can not convert {} type {} to a number", excelTTypeDescriptor.getOriginType(), value);
                    return defaultValue(excelTTypeDescriptor);
                default:
                    if (StringUtils.isBlank(value)) {
                        return null;
                    }
                    String boolValue = caseStringToBoolValue(value);
                    if (boolValue != null) {
                        return boolValue;
                    }
                    log.debug("can not convert {} to boolean", value);
                    return defaultValue(excelTTypeDescriptor);
            }
        } catch (Exception e) {
            log.debug("can not convert {} to boolean", value);
            return defaultValue(excelTTypeDescriptor);
        }
    }

    private String caseStringToBoolValue(String value) {
        if ("TRUE".equalsIgnoreCase(value) || "1".equals(value) || "是".equals(value) || "Y".equalsIgnoreCase(value)) {
            return "true";
        } else if ("FALSE".equalsIgnoreCase(value) || "0".equals(value) || "否".equals(value) || "N".equalsIgnoreCase(value)) {
            return "false";
        }
        return null;
    }
}
