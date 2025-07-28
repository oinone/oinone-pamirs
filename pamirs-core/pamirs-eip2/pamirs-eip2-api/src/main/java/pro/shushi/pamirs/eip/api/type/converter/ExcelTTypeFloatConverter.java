package pro.shushi.pamirs.eip.api.type.converter;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.type.ExcelTTypeDescriptor;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

/**
 * @author Gesi at 16:00 on 2025/7/18
 */
@Component
public class ExcelTTypeFloatConverter extends ExcelTTypeMoneyConverter {

    @Override
    public boolean canConvert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        return TtypeEnum.FLOAT.value().equals(excelTTypeDescriptor.getTargetType());
    }

    @Override
    public String convert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        try {
            if ("float".equals(excelTTypeDescriptor.getOriginType())) {
                return Double.parseDouble(excelTTypeDescriptor.getValue()) + "";
            }
            return super.convert(excelTTypeDescriptor);
        } catch (Exception e) {
            return defaultValue();
        }
    }

    @Override
    public String defaultValue() {
        return "0.0";
    }
}
