package pro.shushi.pamirs.file.api.extpoint;

import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;

import java.util.List;

@Ext(ExcelExportTask.class)
public interface ExcelExportFetchDataExtPoint {

    /**
     * <h>获取导出数据</h>
     * <p>
     * 导出数据按照区块进行划分，List&lt;Object&gt;中的每一项对应一个区块中的数据
     * 在工作表中的每个区块按创建顺序依次排列，与列表中的每项数据一一对应
     * </p>
     *
     * @param exportTask 导出任务
     * @param context    Excel定义上下文
     * @return 导出数据
     */
    @ExtPoint(displayName = "获取导出数据")
    List<Object> fetchExportData(ExcelExportTask exportTask, ExcelDefinitionContext context);
}
