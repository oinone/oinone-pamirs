package pro.shushi.pamirs.file.api.extpoint;

import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;

/**
 * 导入全部结束后执行
 *
 * @author Adamancy Zhang at 21:50 on 2024-03-19
 */
@Ext(ExcelImportTask.class)
public interface ExcelImportAllDataExtPoint {

    /**
     * 导入数据
     *
     * @param importContext Excel导入上下文
     * @return 导入是否中断
     */
    @ExtPoint(displayName = "导入全部数据后处理")
    Boolean importAfterProperties(ExcelImportContext importContext);

}
