package pro.shushi.pamirs.ux.quickfilling.converter.defaults;

import pro.shushi.pamirs.ux.quickfilling.converter.AbstractBasicQuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingColumn;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingRow;

/**
 * 文本类型转换
 *
 * @author Adamancy Zhang at 15:59 on 2025-11-27
 */
public class StringConverter extends AbstractBasicQuickFillingConverter implements QuickFillingConverter {

    public StringConverter(QuickFillingColumn column) {
        super(column);
    }

    @Override
    protected Object singleValueConvert(QuickFillingRow row, String value) {
        return value;
    }
}
