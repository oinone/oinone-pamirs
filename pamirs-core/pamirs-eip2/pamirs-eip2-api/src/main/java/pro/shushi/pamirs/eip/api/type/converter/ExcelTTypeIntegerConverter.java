package pro.shushi.pamirs.eip.api.type.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ExcelTTypeIntegerConverter extends ExcelTTypeMoneyConverter {

    @Autowired
    private ExcelTTypeBoolConverter excelTTypeBoolConverter;

    @Override
    public boolean canConvert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        return TtypeEnum.INTEGER.value().equals(excelTTypeDescriptor.getTargetType())
                || "long".equals(excelTTypeDescriptor.getTargetType())
                || TtypeEnum.UID.value().equals(excelTTypeDescriptor.getTargetType());
    }

    @Override
    public String convert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        String value = excelTTypeDescriptor.getValue();
        try {
            switch (excelTTypeDescriptor.getOriginType()) {
                case "datetime":
                case "year":
                case "date":
                case "time": {
                    log.debug("can not convert {} type {} to a number", excelTTypeDescriptor.getOriginType(), value);
                    return defaultValue(excelTTypeDescriptor);
                }
                case "bool": {
                    if (StringUtils.isBlank(value)) {
                        return null;
                    }
                    String boolValue = excelTTypeBoolConverter.convert(ExcelTTypeDescriptor.valueOf(value, TtypeEnum.STRING.value(), TtypeEnum.BOOLEAN.value()));
                    if (boolValue == null) {
                        return defaultValue(excelTTypeDescriptor);
                    }
                    return Boolean.TRUE.toString().equals(boolValue) ? "1" : "0";
                }
                default: {
                    if (StringUtils.isBlank(value)) {
                        return null;
                    }
                    String numberValue = extractAmountString(value);
                    if (StringUtils.isBlank(numberValue)) {
                        log.debug("can not convert {} to a number", value);
                        return defaultValue(excelTTypeDescriptor);
                    }
                    return new BigDecimal(numberValue).longValue() + "";
                }
            }
        } catch (Exception e) {
            log.debug("can not convert {} to a number", value, e);
            return defaultValue(excelTTypeDescriptor);
        }
    }
}
