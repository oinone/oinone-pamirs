package pro.shushi.pamirs.eip.api.type.converter;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.type.ExcelTTypeDescriptor;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Gesi at 16:00 on 2025/7/18
 */
@Component
public class ExcelTTypeStringConverter implements ExcelTTypeConverter {

    private static final Set<String> STRING_TTYPE_SET =
            Sets.newHashSet(TtypeEnum.STRING, TtypeEnum.TEXT, TtypeEnum.HTML, TtypeEnum.PHONE, TtypeEnum.EMAIL)
                    .stream().map(TtypeEnum::value).collect(Collectors.toSet());

    @Override
    public boolean canConvert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        return STRING_TTYPE_SET.contains(excelTTypeDescriptor.getTargetType());
    }

    @Override
    public String convert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        return excelTTypeDescriptor.getValue();
    }

}
