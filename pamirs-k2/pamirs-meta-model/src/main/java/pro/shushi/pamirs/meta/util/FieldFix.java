package pro.shushi.pamirs.meta.util;

import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.Optional;

/**
 * 字段配置fix
 * 2020/12/23 3:18 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class FieldFix {

    public static String fixFormat(ModelField modelField) {
        DateFormatEnum format = modelField.getFormat();
        TtypeEnum ttypeEnum = modelField.getTtype();
        if (TtypeEnum.YEAR.equals(ttypeEnum)) {
            format = Optional.ofNullable(format).orElse(DateFormatEnum.YEAR);
        } else if (TtypeEnum.DATE.equals(ttypeEnum)) {
            format = Optional.ofNullable(format).orElse(DateFormatEnum.DATE);
        } else if (TtypeEnum.TIME.equals(ttypeEnum)) {
            format = Optional.ofNullable(format).orElse(DateFormatEnum.TIME);
        } else if (TtypeEnum.DATETIME.equals(ttypeEnum)) {
            format = Optional.ofNullable(format).orElse(DateFormatEnum.DATETIME);
        }
        if (null == format) {
            return null;
        }
        return format.value();
    }

}
