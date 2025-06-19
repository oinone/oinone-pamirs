package pro.shushi.pamirs.boot.base.ux.entity.annotation;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxForm;
import pro.shushi.pamirs.boot.base.ux.constants.GridConstants;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * {@link UxForm} annotation wrapper class.
 *
 * @author Adamancy Zhang at 15:14 on 2025-06-16
 */
@Data
public class UxFormWrapper implements UxForm, Serializable {

    private static final long serialVersionUID = 3456234029701974224L;

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
        return UxForm.class;
    }

    public static UxFormWrapper wrap(UxForm form) {
        UxFormWrapper uxForm = new UxFormWrapper();
        uxForm.grid = form.grid();
        uxForm.group = form.group();
        uxForm.tabsTable = form.tabsTable();
        return uxForm;
    }
}
