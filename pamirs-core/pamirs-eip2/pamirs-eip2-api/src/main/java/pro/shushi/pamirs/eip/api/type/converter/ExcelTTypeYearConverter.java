package pro.shushi.pamirs.eip.api.type.converter;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.type.ExcelTTypeDescriptor;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.DateUtils;

import java.util.Date;

/**
 * @author Gesi at 16:00 on 2025/7/18
 */
@Component
@Slf4j
public class ExcelTTypeYearConverter extends ExcelTTypeDateTimeConverter {

    @Override
    public boolean canConvert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        return TtypeEnum.YEAR.value().equals(excelTTypeDescriptor.getTargetType());
    }

    @Override
    public String convert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        String value = excelTTypeDescriptor.getValue();
        try {
            Date date = getDateByString(value, excelTTypeDescriptor.getFormat());
            return DateUtils.formatDate(date, DateFormatEnum.YEAR.value());
        } catch (Exception e) {
            log.debug("can not convert {} to year, use default value", value, e);
            return defaultValue();
        }
    }

    @Override
    public String defaultValue() {
        return "2025";
    }
}
