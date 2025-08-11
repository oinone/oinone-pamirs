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
public class ExcelTTypeTimeConverter extends ExcelTTypeDateTimeConverter {

    @Override
    public boolean canConvert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        return TtypeEnum.TIME.value().equals(excelTTypeDescriptor.getTargetType());
    }

    @Override
    public String convert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        return convertOrDefault(excelTTypeDescriptor, DateFormatEnum.TIME.value());
    }

}
