package pro.shushi.pamirs.ux.quickfilling.converter.defaults;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingColumn;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingRow;
import pro.shushi.pamirs.ux.quickfilling.enumeration.QuickFillingExpEnumerate;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 年份类型转换
 *
 * @author Adamancy Zhang at 21:05 on 2025-11-27
 */
@Slf4j
public class YearConverter extends AbstractDateConverter implements QuickFillingConverter {

    public YearConverter(QuickFillingColumn column) {
        super(column);
    }

    @Override
    protected Object singleValueConvert(QuickFillingRow row, String value) {
        try {
            return new SimpleDateFormat(YEAR_PATTERN).parse(value);
        } catch (ParseException ignored) {
        }
        validateError(row, QuickFillingExpEnumerate.NON_YEAR_ERROR.msg());
        return null;
    }
}
