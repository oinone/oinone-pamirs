package pro.shushi.pamirs.ux.quickfilling.converter.defaults;

import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingColumn;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverter;

/**
 * 多对多类型转换
 *
 * @author Adamancy Zhang at 09:53 on 2025-11-28
 */
public class M2MConverter extends M2OConverter implements QuickFillingConverter {

    public M2MConverter(QuickFillingColumn column) {
        super(column);
    }
}
