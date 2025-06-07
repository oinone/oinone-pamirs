package pro.shushi.pamirs.file.core.service;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.service.ExcelExportTaskService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

@Fun(ExcelExportTaskService.FUN_NAMESPACE)
@Component
public class ExcelExportTaskServiceImpl implements ExcelExportTaskService {

    @Override
    @Function
    public ExcelExportTask queryById(Long id) {
        return new ExcelExportTask().queryById(id);
    }

    @Override
    @Function
    public Integer updateById(ExcelExportTask task) {
        return task.updateById();
    }

    @Override
    @Function
    public Long count(IWrapper<ExcelExportTask> wrapper) {
        return new ExcelExportTask().count(wrapper);
    }

}
