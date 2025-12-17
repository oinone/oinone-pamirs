package pro.shushi.pamirs.ux.quickfilling.converter.defaults;

import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingColumn;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingRow;
import pro.shushi.pamirs.ux.quickfilling.enumeration.QuickFillingExpEnumerate;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 日期时间类型转换
 *
 * @author Adamancy Zhang at 21:08 on 2025-11-27
 */
public class DatetimeConverter extends AbstractDateConverter implements QuickFillingConverter {

    public DatetimeConverter(QuickFillingColumn column) {
        super(column);
    }

    @Override
    protected Object singleValueConvert(QuickFillingRow row, String value) {
        for (String datePattern : DATE_PATTERNS) {
            for (String timePattern : TIME_PATTERNS) {
                try {
                    return new SimpleDateFormat(datePattern + " " + timePattern).parse(value);
                } catch (ParseException ignored) {
                }
            }
        }
        validateError(row, QuickFillingExpEnumerate.NON_DATETIME_ERROR.msg());
        return null;
    }
}
