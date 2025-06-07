package pro.shushi.pamirs.trigger.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleQuery;
import pro.shushi.pamirs.trigger.model.ExecuteTaskAction;

import java.util.Collection;
import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2020-11-02 15:44
 */
@Fun(ExecuteTaskActionService.FUN_NAMESPACE)
public interface ExecuteTaskActionService extends TaskActionService<ExecuteTaskAction> {

    String FUN_NAMESPACE = "pro.shushi.pamirs.trigger.service.ExecuteTaskActionService";

    @Function
    @Override
    Boolean submit(ExecuteTaskAction taskItem);

    @Function
    @Override
    List<ExecuteTaskAction> selectList(ScheduleQuery wrapper);

    @Function
    @Override
    Long countByEntity(ExecuteTaskAction entity);

    @Function
    @Override
    List<ExecuteTaskAction> selectListByEntity(ExecuteTaskAction entity);

    @Function
    @Override
    Boolean delete(String technicalName);

    @Function
    @Override
    Boolean deleteBatch(Collection<String> technicalNames);

    @Function
    @Override
    Boolean active(String technicalName);

    @Function
    @Override
    Boolean activeBatch(Collection<String> technicalNames);

    @Function
    @Override
    Boolean cancel(String technicalName);

    @Function
    @Override
    Boolean cancelBatch(Collection<String> technicalNames);
}
