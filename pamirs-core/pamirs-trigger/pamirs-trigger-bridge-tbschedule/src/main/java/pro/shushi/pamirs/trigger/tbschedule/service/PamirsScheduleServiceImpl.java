package pro.shushi.pamirs.trigger.tbschedule.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.meta.annotation.sys.Ds;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleService;
import pro.shushi.pamirs.middleware.schedule.common.Page;
import pro.shushi.pamirs.middleware.schedule.core.service.ScheduleServiceImpl;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleEnvironment;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.trigger.tbschedule.model.PamirsSchedule;

import java.util.Collection;
import java.util.List;

/**
 * @author Adamancy Zhang on 2021-03-03 21:56
 */
@Ds(model = PamirsSchedule.MODEL_MODEL)
@Primary
@Service
public class PamirsScheduleServiceImpl extends ScheduleServiceImpl implements ScheduleService {

    @Override
    public long addScheduleTask(ScheduleItem task) {
        return super.addScheduleTask(task);
    }

    @Override
    public long addScheduleTaskRecord(ScheduleItem task) {
        return super.addScheduleTaskRecord(task);
    }

    @Override
    public int createOrUpdateScheduleTaskByTechnicalName(ScheduleItem task) {
        return super.createOrUpdateScheduleTaskByTechnicalName(task);
    }

    @Override
    public int deleteScheduleTaskByTechnicalName(ScheduleEnvironment environment, String technicalName) {
        return super.deleteScheduleTaskByTechnicalName(environment, technicalName);
    }

    @Override
    public int deleteScheduleTaskByTechnicalNameBatch(ScheduleEnvironment environment, Collection<String> technicalNames) {
        return super.deleteScheduleTaskByTechnicalNameBatch(environment, technicalNames);
    }

    @Override
    public int activeScheduleTaskByTechnicalName(ScheduleEnvironment environment, String technicalName) {
        return super.activeScheduleTaskByTechnicalName(environment, technicalName);
    }

    @Override
    public int activeScheduleTaskByTechnicalNameBatch(ScheduleEnvironment environment, Collection<String> technicalNames) {
        return super.activeScheduleTaskByTechnicalNameBatch(environment, technicalNames);
    }

    @Override
    public int cancelScheduleTaskByTechnicalName(ScheduleEnvironment environment, String technicalName) {
        return super.cancelScheduleTaskByTechnicalName(environment, technicalName);
    }

    @Override
    public int cancelScheduleTaskByTechnicalNameBatch(ScheduleEnvironment environment, Collection<String> technicalNames) {
        return super.cancelScheduleTaskByTechnicalNameBatch(environment, technicalNames);
    }

    @Override
    public void cancelScheduleTaskByBizId(ScheduleEnvironment environment, Long bizId) {
        super.cancelScheduleTaskByBizId(environment, bizId);
    }

    @Override
    public Long countByEntity(ScheduleItem task) {
        return super.countByEntity(task);
    }

    @Override
    public List<ScheduleItem> selectListByEntity(ScheduleItem task) {
        return super.selectListByEntity(task);
    }

    @Override
    public Page<ScheduleItem> selectListByWhere(ScheduleEnvironment environment, String where, String order, Integer currentPage, Integer size) {
        return super.selectListByWhere(environment, where, order, currentPage, size);
    }

    @Override
    public ScheduleItem selectById(ScheduleEnvironment environment, Long id) {
        return super.selectById(environment, id);
    }

    @Override
    public List<ScheduleItem> selectByBizId(ScheduleEnvironment environment, Long bizId) {
        return super.selectByBizId(environment, bizId);
    }

    @Override
    public int updateById(ScheduleItem task) {
        return super.updateById(task);
    }

    @Override
    public int deleteByIds(ScheduleEnvironment environment, List<Long> ids) {
        return super.deleteByIds(environment, ids);
    }

    @Override
    public int cancelByIds(ScheduleEnvironment environment, List<Long> ids) {
        return super.cancelByIds(environment, ids);
    }

    @Override
    public int recoveryByIds(ScheduleEnvironment environment, List<Long> ids) {
        return super.recoveryByIds(environment, ids);
    }
}
