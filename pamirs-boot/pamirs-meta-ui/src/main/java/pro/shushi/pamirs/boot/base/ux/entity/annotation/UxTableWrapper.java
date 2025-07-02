package pro.shushi.pamirs.boot.base.ux.entity.annotation;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxTable;
import pro.shushi.pamirs.boot.base.ux.constants.GridConstants;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * {@link UxTable} annotation wrapper class.
 *
 * @author Adamancy Zhang at 15:12 on 2025-06-16
 */
@Data
public class UxTableWrapper implements UxTable, Serializable {

    private static final long serialVersionUID = -2534897561511151255L;

    private int grid = GridConstants.defaultViewGrid;

    private boolean enableSearch = true;

    private boolean enableSequence = false;

    @Override
    public int grid() {
        return grid;
    }

    @Override
    public boolean enableSearch() {
        return enableSearch;
    }

    @Override
    public boolean enableSequence() {
        return enableSequence;
    }

    @JSONField(serialize = false)
    @Override
    public Class<? extends Annotation> annotationType() {
        return UxTable.class;
    }

    public static UxTableWrapper wrap(UxTable table) {
        UxTableWrapper uxTable = new UxTableWrapper();
        uxTable.grid = table.grid();
        uxTable.enableSearch = table.enableSearch();
        uxTable.enableSequence = table.enableSequence();
        return uxTable;
    }
}
