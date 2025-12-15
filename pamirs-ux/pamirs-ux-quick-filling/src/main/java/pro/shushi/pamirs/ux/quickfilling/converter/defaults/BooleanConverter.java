package pro.shushi.pamirs.ux.quickfilling.converter.defaults;

import pro.shushi.pamirs.ux.quickfilling.converter.AbstractBasicQuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingColumn;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingRow;
import pro.shushi.pamirs.ux.quickfilling.enumeration.QuickFillingExpEnumerate;

/**
 * 布尔类型转换
 *
 * @author Adamancy Zhang at 17:07 on 2025-11-27
 */
public class BooleanConverter extends AbstractBasicQuickFillingConverter implements QuickFillingConverter {

    public BooleanConverter(QuickFillingColumn column) {
        super(column);
    }

    @Override
    protected Object singleValueConvert(QuickFillingRow row, String value) {
        if ("true".equals(value) || "TRUE".equals(value) || "1".equals(value) || "是".equals(value) || "Y".equalsIgnoreCase(value)) {
            return Boolean.TRUE;
        } else if ("false".equals(value) || "FALSE".equals(value) || "0".equals(value) || "否".equals(value) || "N".equalsIgnoreCase(value)) {
            return Boolean.FALSE;
        }
        validateError(row, QuickFillingExpEnumerate.NON_BOOLEAN_ERROR.msg());
        return null;
    }
}
