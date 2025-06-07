package pro.shushi.pamirs.file.api.extpoint;

import pro.shushi.pamirs.file.api.entity.ExcelExportFetchDataContext;
import pro.shushi.pamirs.file.api.executor.ExcelExportExecutor;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.sys.Base;

/**
 * Excel导出区块数据扩展点
 *
 * @author Adamancy Zhang at 16:32 on 2024-03-28
 */
@Base
@Ext(ExcelExportExecutor.class)
public interface ExcelBlockFetchDataExtPoint {

    /**
     * <h>获取导出数据</h>
     * <p>
     * 1. 返回值为null，则不做任何处理<br>
     * 2. 返回值为Pagination，则根据分页条件继续重复调用当前扩展点<br>
     * 3. 返回值为其他值，则自动添加至{@link ExcelExportFetchDataContext#getResults()}结果集
     * </p>
     * <p>
     * PS: 不建议手动添加结果到结果集，在流式获取模式下，结果集永远为空，不保留任何数据
     * </p>
     *
     * @param context Excel导出获取数据上下文
     * @return 单个区块数据
     */
    @ExtPoint(displayName = "获取导出数据")
    Object fetchExportData(ExcelExportFetchDataContext context);

}
