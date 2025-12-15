package pro.shushi.pamirs.ux.quickfilling.converter.defaults;

import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.ux.quickfilling.converter.AbstractQuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingColumn;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingRow;
import pro.shushi.pamirs.ux.quickfilling.enumeration.QuickFillingExpEnumerate;

/**
 * 键值对类型转换
 *
 * @author Adamancy Zhang at 09:55 on 2025-11-28
 */
public class MapConverter extends AbstractQuickFillingConverter implements QuickFillingConverter {

    private final boolean isMulti;

    public MapConverter(QuickFillingColumn column) {
        super(column);
        this.isMulti = column.isMulti();
    }

    @Override
    public void convert(QuickFillingRow row, String value) {
        if (isSkip(row, value)) {
            return;
        }
        boolean isValid = false;
        Object target = null;
        if (isMulti) {
            if (JsonUtils.isJSONArray(value)) {
                target = JsonUtils.parseObjectList(value);
                isValid = true;
            }
        } else {
            if (JsonUtils.isJSONObject(value)) {
                target = JsonUtils.parseObject(value);
                isValid = true;
            }
        }
        if (isValid) {
            setValue(row, target);
        } else {
            validateError(row, QuickFillingExpEnumerate.NON_MAP_ERROR.msg());
        }
    }

    @Override
    public void collect(QuickFillingRow row, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill() {
        throw new UnsupportedOperationException();
    }
}
