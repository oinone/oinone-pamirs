package pro.shushi.pamirs.file.api.extpoint.impl;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.entity.ExcelExportFetchDataContext;
import pro.shushi.pamirs.file.api.executor.ExcelExportExecutor;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelBlockFetchDataExtPoint;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.sys.Base;

/**
 * 默认Excel区块获取数据扩展点
 *
 * @author Adamancy Zhang at 16:43 on 2024-03-28
 */
@Base
@Component
@Ext(ExcelExportExecutor.class)
public class DefaultExcelBlockFetchDataExtPoint extends AbstractExcelBlockFetchDataExtPoint<Object> {

    @ExtPoint.Implement(priority = 999)
    @Override
    public Object fetchExportData(ExcelExportFetchDataContext context) {
        return super.fetchExportData(context);
    }
}
