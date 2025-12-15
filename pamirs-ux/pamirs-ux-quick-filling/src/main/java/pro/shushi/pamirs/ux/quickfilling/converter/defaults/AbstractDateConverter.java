package pro.shushi.pamirs.ux.quickfilling.converter.defaults;

import pro.shushi.pamirs.ux.quickfilling.converter.AbstractBasicQuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingColumn;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverter;

/**
 * 日期类型转换
 *
 * @author Adamancy Zhang at 20:55 on 2025-11-27
 */
public abstract class AbstractDateConverter extends AbstractBasicQuickFillingConverter implements QuickFillingConverter {

    protected static final String YEAR_PATTERN = "yyyy";

    protected static final String[] DATE_PATTERNS = {
            "yyyy/MM/dd",
            "yyyy-MM-dd",
            "yyyy.MM.dd"
    };

    protected static final String[] TIME_PATTERNS = {
            "HH:mm:ss"
    };

    public AbstractDateConverter(QuickFillingColumn column) {
        super(column);
    }

}
