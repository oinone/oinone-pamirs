package pro.shushi.pamirs.eip.api.type.converter;

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
            if ("integer".equals(excelTTypeDescriptor.getOriginType()) || "long".equals(excelTTypeDescriptor.getOriginType()) || "uid".equals(excelTTypeDescriptor.getOriginType())) {
                return Long.parseLong(value) + "";
            } else if ("binary".equals(excelTTypeDescriptor.getOriginType())) {
                return Long.parseLong(value, 2) + "";
            }
            return new BigDecimal(super.convert(excelTTypeDescriptor)).longValue() + "";
        } catch (Exception e) {
            log.debug("can not convert {} to integer, use default value", value, e);
            return defaultValue();
        }
    }

    @Override
    public String defaultValue() {
        return "0";
    }
}
