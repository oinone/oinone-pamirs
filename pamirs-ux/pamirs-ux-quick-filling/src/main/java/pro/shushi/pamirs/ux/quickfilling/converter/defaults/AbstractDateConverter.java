package pro.shushi.pamirs.ux.quickfilling.converter.defaults;

import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.util.FieldFix;
import pro.shushi.pamirs.ux.quickfilling.converter.AbstractBasicQuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingColumn;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverter;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    protected String getDateFillingValue(Date date) {
        QuickFillingColumn column = getColumn();
        String model = column.getModel();
        String field = column.getField();
        String format = FieldFix.fixFormat(PamirsSession.getContext().getModelField(model, field).getModelField());
        assert format != null;
        return new SimpleDateFormat(format).format(date);
    }
}
