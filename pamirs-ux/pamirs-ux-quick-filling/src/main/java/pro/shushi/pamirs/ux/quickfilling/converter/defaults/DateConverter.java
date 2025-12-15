package pro.shushi.pamirs.ux.quickfilling.converter.defaults;

import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingColumn;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingRow;
import pro.shushi.pamirs.ux.quickfilling.enumeration.QuickFillingExpEnumerate;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 日期类型转换
 *
 * @author Adamancy Zhang at 20:55 on 2025-11-27
 */
public class DateConverter extends AbstractDateConverter implements QuickFillingConverter {

    public DateConverter(QuickFillingColumn column) {
        super(column);
    }

    @Override
    protected Object singleValueConvert(QuickFillingRow row, String value) {
        for (String datePattern : DATE_PATTERNS) {
            try {
                return getDateFillingValue(new SimpleDateFormat(datePattern).parse(value));
            } catch (ParseException ignored) {
            }
        }
        validateError(row, QuickFillingExpEnumerate.NON_DATE_ERROR.msg());
        return null;
    }
}
