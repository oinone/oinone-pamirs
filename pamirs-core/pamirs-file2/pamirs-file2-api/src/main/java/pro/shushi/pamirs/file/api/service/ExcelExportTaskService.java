package pro.shushi.pamirs.file.api.service;

import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

@Fun(ExcelExportTaskService.FUN_NAMESPACE)
public interface ExcelExportTaskService {

    String FUN_NAMESPACE = "file.ExcelExportTaskService";

    /**
     * 根据id查询接口
     *
     * @param id
     * @return
     */
    @Function
    ExcelExportTask queryById(Long id);

    @Function
    Integer updateById(ExcelExportTask task);

    @Function
    Long count(IWrapper<ExcelExportTask> wrapper);
}
