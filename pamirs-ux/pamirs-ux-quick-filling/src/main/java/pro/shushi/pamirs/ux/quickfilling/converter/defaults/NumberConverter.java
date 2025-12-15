package pro.shushi.pamirs.ux.quickfilling.converter.defaults;

import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.ux.common.utils.NumberHelper;
import pro.shushi.pamirs.ux.quickfilling.converter.AbstractBasicQuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingColumn;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingRow;
import pro.shushi.pamirs.ux.quickfilling.enumeration.QuickFillingExpEnumerate;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 数字类型转换
 *
 * @author Adamancy Zhang at 16:34 on 2025-11-27
 */
public class NumberConverter extends AbstractBasicQuickFillingConverter implements QuickFillingConverter {

    public NumberConverter(QuickFillingColumn column) {
        super(column);
    }

    @Override
    protected Object singleValueConvert(QuickFillingRow row, String value) {
        if (NumberHelper.isNumber(value)) {
            return getNumberFillingValue(new BigDecimal(value));
        }
        validateError(row, QuickFillingExpEnumerate.NON_NUMBER_ERROR.msg());
        return null;
    }

    protected String getNumberFillingValue(BigDecimal number) {
        String ttype = getColumn().getTtype();
        if (TtypeEnum.INTEGER.value().equals(ttype)) {
            return number.setScale(0, RoundingMode.HALF_UP).toPlainString();
        }
        return number.toPlainString();
    }
}
