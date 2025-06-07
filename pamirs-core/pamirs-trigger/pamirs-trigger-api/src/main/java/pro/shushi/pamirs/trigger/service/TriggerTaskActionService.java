package pro.shushi.pamirs.trigger.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleQuery;
import pro.shushi.pamirs.trigger.model.TriggerTaskAction;

import java.util.Collection;
import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2020-11-06 15:03
 */
@Fun(TriggerTaskActionService.FUN_NAMESPACE)
public interface TriggerTaskActionService extends TaskActionService<TriggerTaskAction> {

    String FUN_NAMESPACE = "pro.shushi.pamirs.trigger.service.TriggerTaskActionService";

    @Function
    TriggerTaskAction createOrUpdate(TriggerTaskAction data);

    @Function
    @Override
    Boolean submit(TriggerTaskAction taskItem);

    @Function
    @Override
    List<TriggerTaskAction> selectList(ScheduleQuery wrapper);

    @Function
    @Override
    Long countByEntity(TriggerTaskAction entity);

    @Function
    @Override
    List<TriggerTaskAction> selectListByEntity(TriggerTaskAction entity);

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
