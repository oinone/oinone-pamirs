package pro.shushi.pamirs.eip.api.type.converter;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.type.ExcelTTypeDescriptor;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

/**
 * @author Gesi at 16:00 on 2025/7/18
 */
@Component
@Slf4j
public class ExcelTTypeFloatConverter extends ExcelTTypeMoneyConverter {

    @Override
    public boolean canConvert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        return TtypeEnum.FLOAT.value().equals(excelTTypeDescriptor.getTargetType());
    }

    @Override
    public String convert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        String value = excelTTypeDescriptor.getValue();
        try {
            if ("float".equals(excelTTypeDescriptor.getOriginType())) {
                return Double.parseDouble(value) + "";
            } else if ("binary".equals(excelTTypeDescriptor.getOriginType())) {
                if (value != null && value.length() == 32) {
                    int intBits = (int) Long.parseLong(value, 2);
                    return Float.intBitsToFloat(intBits) + "";
                } else if (value != null && value.length() == 64) {
                    long intBits = Long.parseLong(value, 2);
                    return Double.longBitsToDouble(intBits) + "";
                }
            }
            return super.convert(excelTTypeDescriptor);
        } catch (Exception e) {
            log.debug("can not convert {} to float, use default value", value, e);
            return defaultValue(excelTTypeDescriptor);
        }
    }
}
