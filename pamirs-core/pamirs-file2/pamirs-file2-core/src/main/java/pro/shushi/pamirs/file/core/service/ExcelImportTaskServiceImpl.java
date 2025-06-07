package pro.shushi.pamirs.file.core.service;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.file.api.service.ExcelImportTaskService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

@Fun(ExcelImportTaskService.FUN_NAMESPACE)
@Component
public class ExcelImportTaskServiceImpl implements ExcelImportTaskService {

    @Override
    @Function
    public ExcelImportTask queryById(Long id) {
        return new ExcelImportTask().queryById(id);
    }

    @Override
    @Function
    public Integer updateById(ExcelImportTask task) {
        return task.updateById();
    }

    @Override
    @Function
    public ExcelImportTask create(ExcelImportTask task) {
        return task.create();
    }

}
