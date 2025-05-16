package pro.shushi.pamirs.file.api.extpoint;

import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;

@Ext(ExcelImportTask.class)
public interface ExcelImportDataExtPoint<T> {

    /**
     * 导入数据
     *
     * @param importContext Excel导入上下文
     * @param data          合并后的数据对象
     * @return 导入是否中断
     */
    @ExtPoint(displayName = "自定义导入数据")
    Boolean importData(ExcelImportContext importContext, T data);
}
