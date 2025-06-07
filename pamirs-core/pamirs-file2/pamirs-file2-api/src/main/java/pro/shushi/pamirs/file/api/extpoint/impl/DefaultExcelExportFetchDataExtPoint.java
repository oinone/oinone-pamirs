package pro.shushi.pamirs.file.api.extpoint.impl;

import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelExportFetchDataExtPointImpl;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;

import java.util.List;

@Ext(ExcelExportTask.class)
public class DefaultExcelExportFetchDataExtPoint extends AbstractExcelExportFetchDataExtPointImpl {

    @ExtPoint.Implement(priority = 999)
    @Override
    public List<Object> fetchExportData(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        return super.fetchExportData(exportTask, context);
    }
}
