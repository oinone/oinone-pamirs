package pro.shushi.pamirs.file.api.extpoint;

import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;

import java.util.List;

@Ext(ExcelImportTask.class)
public interface ExcelImportO2mDataExtPoint<T> {

    /**
     * 导入数据
     *
     * @param importContext Excel导入上下文
     * @param dataList          收集的全部数据
     * @return 导入是否中断
     */
    @ExtPoint(displayName = "自定义导入多对多数据")
    List<T> importO2mData(ExcelImportContext importContext, List<T> dataList);
}
