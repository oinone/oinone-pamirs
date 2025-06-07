package pro.shushi.pamirs.trigger.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleQuery;
import pro.shushi.pamirs.trigger.model.ScheduleTaskAction;

import java.util.Collection;
import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2020-11-02 15:18
 */
@Fun(ScheduleTaskActionService.FUN_NAMESPACE)
public interface ScheduleTaskActionService extends TaskActionService<ScheduleTaskAction> {

    String FUN_NAMESPACE = "pro.shushi.pamirs.trigger.service.ScheduleTaskActionService";

    @Function
    @Override
    Boolean submit(ScheduleTaskAction taskItem);

    @Function
    @Override
    List<ScheduleTaskAction> selectList(ScheduleQuery wrapper);

    @Function
    @Override
    Long countByEntity(ScheduleTaskAction entity);

    @Function
    @Override
    List<ScheduleTaskAction> selectListByEntity(ScheduleTaskAction entity);

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
