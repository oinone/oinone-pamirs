package pro.shushi.pamirs.file.api.extpoint.impl;

import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelImportDataExtPointImpl;
import pro.shushi.pamirs.file.api.extpoint.ExcelImportDataExtPoint;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;

/**
 * @author Adamancy Zhang at 20:22 on 2021-04-14
 */
@Ext(ExcelImportTask.class)
public class DefaultExcelImportDataExtPoint extends AbstractExcelImportDataExtPointImpl<Object> implements ExcelImportDataExtPoint<Object> {

    @ExtPoint.Implement(priority = 999)
    @Override
    public Boolean importData(ExcelImportContext importContext, Object data) {
        return super.importData(importContext, data);
    }
}
