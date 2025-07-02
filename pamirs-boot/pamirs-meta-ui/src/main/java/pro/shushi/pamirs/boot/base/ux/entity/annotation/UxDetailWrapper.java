package pro.shushi.pamirs.boot.base.ux.entity.annotation;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxDetail;
import pro.shushi.pamirs.boot.base.ux.constants.GridConstants;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * {@link UxDetail} annotation wrapper class.
 *
 * @author Adamancy Zhang at 15:14 on 2025-06-16
 */
@Data
public class UxDetailWrapper implements UxDetail, Serializable {

    private static final long serialVersionUID = -1412060095166959996L;

    private int grid = GridConstants.defaultViewGrid;

    private String group;

    private boolean tabsTable = true;

    @Override
    public int grid() {
        return grid;
    }

    @Override
    public String group() {
        return group;
    }

    @Override
    public boolean tabsTable() {
        return tabsTable;
    }

    @JSONField(serialize = false)
    @Override
    public Class<? extends Annotation> annotationType() {
        return UxDetail.class;
    }

    public static UxDetailWrapper wrap(UxDetail detail) {
        UxDetailWrapper uxDetail = new UxDetailWrapper();
        uxDetail.grid = detail.grid();
        uxDetail.group = detail.group();
        uxDetail.tabsTable = detail.tabsTable();
        return uxDetail;
    }
}
