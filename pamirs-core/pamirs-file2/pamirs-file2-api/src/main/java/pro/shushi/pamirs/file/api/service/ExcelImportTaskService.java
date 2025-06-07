package pro.shushi.pamirs.file.api.service;

import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

@Fun(ExcelImportTaskService.FUN_NAMESPACE)
public interface ExcelImportTaskService {

    String FUN_NAMESPACE = "file.ExcelImportTaskService";

    /**
     * 根据id查询接口
     *
     * @param id
     * @return
     */
    @Function
    ExcelImportTask queryById(Long id);

    @Function
    Integer updateById(ExcelImportTask task);

    @Function
    ExcelImportTask create(ExcelImportTask task);

}
