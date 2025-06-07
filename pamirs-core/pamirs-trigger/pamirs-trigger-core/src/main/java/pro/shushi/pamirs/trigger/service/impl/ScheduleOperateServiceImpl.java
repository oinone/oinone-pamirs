package pro.shushi.pamirs.trigger.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleService;
import pro.shushi.pamirs.middleware.schedule.common.Page;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleEnvironment;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.trigger.service.ScheduleOperateService;
import pro.shushi.pamirs.trigger.util.ScheduleEnvironmentHelper;

import java.util.Collection;
import java.util.List;

/**
 * @author Adamancy Zhang on 2021-04-27 14:59
 */
@Service
@Fun(ScheduleOperateService.FUN_NAMESPACE)
public class ScheduleOperateServiceImpl implements ScheduleOperateService {

    @Autowired
    private ScheduleService scheduleService;

    @Function
    @Override
    public Long addScheduleTask(ScheduleItem task) {
        return scheduleService.addScheduleTask(init(task));
    }

    @Function
    @Override
    public Long addScheduleTaskRecord(ScheduleItem task) {
        return scheduleService.addScheduleTaskRecord(init(task));
    }

    @Function
    @Override
    public Integer createOrUpdateScheduleTaskByTechnicalName(ScheduleItem task) {
        return scheduleService.createOrUpdateScheduleTaskByTechnicalName(init(task));
    }

    @Function
    @Override
    public Integer deleteScheduleTaskByTechnicalName(String technicalName) {
        return scheduleService.deleteScheduleTaskByTechnicalName(ScheduleEnvironmentHelper.generatorEnvironment(), technicalName);
    }

    @Function
    @Override
    public Integer deleteScheduleTaskByTechnicalNameBatch(Collection<String> technicalNames) {
        return scheduleService.deleteScheduleTaskByTechnicalNameBatch(ScheduleEnvironmentHelper.generatorEnvironment(), technicalNames);
    }

    @Function
    @Override
    public Integer activeScheduleTaskByTechnicalName(String technicalName) {
        return scheduleService.activeScheduleTaskByTechnicalName(ScheduleEnvironmentHelper.generatorEnvironment(), technicalName);
    }

    @Function
    @Override
    public Integer activeScheduleTaskByTechnicalNameBatch(Collection<String> technicalNames) {
        return scheduleService.activeScheduleTaskByTechnicalNameBatch(ScheduleEnvironmentHelper.generatorEnvironment(), technicalNames);
    }

    @Function
    @Override
    public Integer cancelScheduleTaskByTechnicalName(String technicalName) {
        return scheduleService.cancelScheduleTaskByTechnicalName(ScheduleEnvironmentHelper.generatorEnvironment(), technicalName);
    }

    @Function
    @Override
    public Integer cancelScheduleTaskByTechnicalNameBatch(Collection<String> technicalNames) {
        return scheduleService.cancelScheduleTaskByTechnicalNameBatch(ScheduleEnvironmentHelper.generatorEnvironment(), technicalNames);
    }

    @Function
    @Override
    public void cancelScheduleTaskByBizId(Long bizId) {
        scheduleService.cancelScheduleTaskByBizId(ScheduleEnvironmentHelper.generatorEnvironment(), bizId);
    }

    @Function
    @Override
    public Long countByEntity(ScheduleItem task) {
        return scheduleService.countByEntity(init(task));
    }

    @Function
    @Override
    public List<ScheduleItem> selectListByEntity(ScheduleItem task) {
        return scheduleService.selectListByEntity(init(task));
    }

    @Function
    @Override
    public Page<ScheduleItem> selectListByWhere(String where, String order, Integer currentPage, Integer size) {
        return scheduleService.selectListByWhere(ScheduleEnvironmentHelper.generatorEnvironment(), where, order, currentPage, size);
    }

    @Function
    @Override
    public ScheduleItem selectById(Long id) {
        return scheduleService.selectById(ScheduleEnvironmentHelper.generatorEnvironment(), id);
    }

    @Function
    @Override
    public List<ScheduleItem> selectByBizId(Long bizId) {
        return scheduleService.selectByBizId(ScheduleEnvironmentHelper.generatorEnvironment(), bizId);
    }

    @Function
    @Override
    public Integer updateById(ScheduleItem task) {
        return scheduleService.updateById(init(task));
    }

    @Function
    @Override
    public Integer deleteByIds(List<Long> ids) {
        return scheduleService.deleteByIds(ScheduleEnvironmentHelper.generatorEnvironment(), ids);
    }

    @Function
    @Override
    public Integer cancelByIds(List<Long> ids) {
        return scheduleService.cancelByIds(ScheduleEnvironmentHelper.generatorEnvironment(), ids);
    }

    @Function
    @Override
    public Integer recoveryByIds(List<Long> ids) {
        return scheduleService.recoveryByIds(ScheduleEnvironmentHelper.generatorEnvironment(), ids);
    }

    private ScheduleItem init(ScheduleItem task) {
        ScheduleEnvironment environment = ScheduleEnvironmentHelper.generatorEnvironment();
        environment.transferTo(task);
        return task;
    }
}
