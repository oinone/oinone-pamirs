package pro.shushi.pamirs.file.api.extpoint.impl;

import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelImportO2mDataExtPointImpl;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;

import java.util.List;

/**
 * @author Adamancy Zhang at 20:22 on 2021-04-14
 */
@Ext(ExcelImportTask.class)
public class DefaultExcelImportO2mDataExtPoint extends AbstractExcelImportO2mDataExtPointImpl<Object> {

    @ExtPoint.Implement(priority = 999)
    @Override
    public List<Object> importO2mData(ExcelImportContext importContext, List<Object> dataList) {
        return super.importO2mData(importContext, dataList);
    }
}
