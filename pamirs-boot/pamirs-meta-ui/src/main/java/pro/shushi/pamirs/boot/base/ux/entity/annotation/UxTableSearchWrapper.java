package pro.shushi.pamirs.boot.base.ux.entity.annotation;

import pro.shushi.pamirs.boot.base.ux.annotation.view.UxTableSearch;
import pro.shushi.pamirs.boot.base.ux.constants.GridConstants;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * {@link UxTableSearch} annotation wrapper class.
 *
 * @author Adamancy Zhang at 15:14 on 2025-06-16
 */
@Data
public class UxTableSearchWrapper implements UxTableSearch, Serializable {

    private static final long serialVersionUID = -181106824232221295L;

    private int grid = GridConstants.defaultTableSearchGrid;

    @Override
    public int grid() {
        return grid;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return UxTableSearch.class;
    }

    public static UxTableSearchWrapper wrap(UxTableSearch tableSearch) {
        UxTableSearchWrapper uxTableSearch = new UxTableSearchWrapper();
        uxTableSearch.grid = tableSearch.grid();
        return uxTableSearch;
    }
}
