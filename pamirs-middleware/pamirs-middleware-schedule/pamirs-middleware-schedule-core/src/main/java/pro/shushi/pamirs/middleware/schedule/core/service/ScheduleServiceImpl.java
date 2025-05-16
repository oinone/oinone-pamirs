package pro.shushi.pamirs.middleware.schedule.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronSequenceGenerator;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleService;
import pro.shushi.pamirs.middleware.schedule.common.Page;
import pro.shushi.pamirs.middleware.schedule.core.conf.ScheduleSystemInfo;
import pro.shushi.pamirs.middleware.schedule.core.dao.mapper.ScheduleItemMapper;
import pro.shushi.pamirs.middleware.schedule.core.util.ExceptionHelper;
import pro.shushi.pamirs.middleware.schedule.core.verification.util.VerificationHelper;
import pro.shushi.pamirs.middleware.schedule.directive.IntValueEnumerationHelper;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleEnvironment;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TaskStatus;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TimeAnchor;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TimeUnit;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@DubboService
public class ScheduleServiceImpl implements ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    @Autowired
    private ScheduleItemMapper scheduleItemMapper;

    @Override
    public long addScheduleTask(ScheduleItem task) {
        task.setTechnicalName(UUID.randomUUID().toString());
        verificationAndSet(task);
        return scheduleItemMapper.insert(task);
    }

    @Override
    public long addScheduleTaskRecord(ScheduleItem task) {
        task.setTechnicalName(task.getTechnicalName() + "-" + System.currentTimeMillis());
        initSerialParams(null, task);
        return scheduleItemMapper.insertRecord(task);
    }

    /**
     * createOrUpdate 只所以改成catch到Duplicate在创建，是因为达梦等数据库不支持MySQL
     *
     * @param task 任务 {@link ScheduleItem}
     * @return
     */
    @Override
    public int createOrUpdateScheduleTaskByTechnicalName(ScheduleItem task) {
        VerificationHelper.required(task.getTechnicalName(), "未指定技术名称");
        verificationAndSet(task);
        try {
            return scheduleItemMapper.createScheduleItem(task);
        } catch (Throwable e) {
            if (ExceptionHelper.isDuplicateKeyException(e)) {
                return scheduleItemMapper.updateByTechnicalName(task);
            }
            throw e;
        }
    }

    @Override
    public int deleteScheduleTaskByTechnicalName(ScheduleEnvironment environment, String technicalName) {
        if (StringUtils.isBlank(technicalName)) {
            return 0;
        }
        initSerialParams(environment);
        return scheduleItemMapper.deleteTaskByTechnicalNameList(environment, Collections.singletonList(technicalName), System.currentTimeMillis());
    }

    @Override
    public int deleteScheduleTaskByTechnicalNameBatch(ScheduleEnvironment environment, Collection<String> technicalNames) {
        if (CollectionUtils.isEmpty(technicalNames)) {
            return 0;
        }
        initSerialParams(environment);
        return scheduleItemMapper.deleteTaskByTechnicalNameList(environment, technicalNames, System.currentTimeMillis());
    }

    @Override
    public int activeScheduleTaskByTechnicalName(ScheduleEnvironment environment, String technicalName) {
        if (StringUtils.isBlank(technicalName)) {
            return 0;
        }
        initSerialParams(environment);
        return scheduleItemMapper.activeTaskByTechnicalNameList(environment, Collections.singletonList(technicalName));
    }

    @Override
    public int activeScheduleTaskByTechnicalNameBatch(ScheduleEnvironment environment, Collection<String> technicalNames) {
        if (CollectionUtils.isEmpty(technicalNames)) {
            return 0;
        }
        initSerialParams(environment);
        return scheduleItemMapper.activeTaskByTechnicalNameList(environment, technicalNames);
    }

    @Override
    public int cancelScheduleTaskByTechnicalName(ScheduleEnvironment environment, String technicalName) {
        if (StringUtils.isBlank(technicalName)) {
            return 0;
        }
        initSerialParams(environment);
        return scheduleItemMapper.cancelTaskByTechnicalNameList(environment, Collections.singletonList(technicalName));
    }

    @Override
    public int cancelScheduleTaskByTechnicalNameBatch(ScheduleEnvironment environment, Collection<String> technicalNames) {
        if (CollectionUtils.isEmpty(technicalNames)) {
            return 0;
        }
        initSerialParams(environment);
        return scheduleItemMapper.cancelTaskByTechnicalNameList(environment, technicalNames);
    }

    @Override
    public void cancelScheduleTaskByBizId(ScheduleEnvironment environment, Long bizId) {
        ScheduleItem task = new ScheduleItem();
        initSerialParams(environment, task);
        task.setErrorLog("取消执行");
        task.setRemark("取消执行");
        task.setBizId(bizId);
        task.setTaskStatus(TaskStatus.CANCELED.intValue());
        scheduleItemMapper.updateTaskStatusByBizId(task);
    }

    @Override
    public Long countByEntity(ScheduleItem task) {
        initSerialParams(task);
        boolean isNotNeedQuery = true;
        if (task.getTechnicalName() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getTaskType() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getInterfaceName() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getMethodName() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getVersion() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getGroup() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getTimeout() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getTenant() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getEnv() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getOwnSign() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getApplication() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getBizId() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getParentBizId() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getBizCode() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getIsCycle() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && CollectionUtils.isNotEmpty(task.getIds())) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery) {
            return 0L;
        } else {
            return scheduleItemMapper.countByEntity(task);
        }
    }

    @Override
    public List<ScheduleItem> selectListByEntity(ScheduleItem task) {
        initSerialParams(task);
        boolean isNotNeedQuery = true;
        if (task.getTechnicalName() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getTaskType() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getInterfaceName() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getMethodName() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getVersion() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getGroup() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getTimeout() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getTenant() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getEnv() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getOwnSign() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getApplication() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getBizId() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getParentBizId() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getBizCode() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && task.getIsCycle() != null) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery && CollectionUtils.isNotEmpty(task.getIds())) {
            isNotNeedQuery = false;
        }
        if (isNotNeedQuery) {
            return Collections.emptyList();
        } else {
            return scheduleItemMapper.selectListByEntity(task);
        }
    }

    @Override
    public Page<ScheduleItem> selectListByWhere(ScheduleEnvironment environment, String where, String order, Integer currentPage, Integer size) {
        Page<ScheduleItem> page = new Page<>();
        if (StringUtils.isBlank(where)) {
            return page.setList(Collections.emptyList())
                    .setTotal(0)
                    .setTotalPage(0)
                    .setPageNo(0)
                    .setPageSize(0);
        }
        if (StringUtils.isBlank(order)) {
            order = "`write_date` DESC,`id` DESC";
        }
        if (currentPage == null || currentPage <= 0) {
            currentPage = 1;
        }
        if (size == null || size <= 0) {
            size = 20;
        }
        int offset = (currentPage - 1) * size;
        initSerialParams(environment);
        List<ScheduleItem> list = scheduleItemMapper.selectListByWhere(environment, where, order, offset, size);
        int count = scheduleItemMapper.countByWhere(environment, where);
        page.setList(list)
                .setPageSize(size)
                .setTotalPage(count / size)
                .setPageNo(currentPage)
                .setTotal(count);
        return page;
    }

    @Override
    public ScheduleItem selectById(ScheduleEnvironment environment, Long id) {
        ScheduleItem task = new ScheduleItem();
        initSerialParams(environment, task);
        task.setId(id);
        return scheduleItemMapper.selectById(task);
    }

    @Override
    public List<ScheduleItem> selectByBizId(ScheduleEnvironment environment, Long bizId) {
        initSerialParams(environment);
        return scheduleItemMapper.selectByBizId(environment, bizId);
    }

    @Override
    public int updateById(ScheduleItem task) {
        if (task == null) {
            return 0;
        }
        initSerialParams(task);
        return scheduleItemMapper.updateByPrimaryKey(task);
    }

    @Override
    public int deleteByIds(ScheduleEnvironment environment, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        initSerialParams(environment);
        return scheduleItemMapper.deleteByIds(environment, ids, System.currentTimeMillis());
    }

    @Override
    public int cancelByIds(ScheduleEnvironment environment, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        initSerialParams(environment);
        return scheduleItemMapper.cancelByIds(environment, ids);
    }

    @Override
    public int recoveryByIds(ScheduleEnvironment environment, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        initSerialParams(environment);
        return scheduleItemMapper.recoveryByIds(environment, ids);
    }

    private void verificationAndSet(ScheduleItem task) {
        VerificationHelper.required(task, "未传入计划任务");
        VerificationHelper.required(task.getTaskType(), "未指定任务类型");
        VerificationHelper.required(task.getInterfaceName(), "未指定接口名称");
        VerificationHelper.required(task.getMethodName(), "未指定方法名称");
        VerificationHelper.required(task.getApplication(), "未指定应用名");
        Boolean isCycle = task.getIsCycle();
        if (isCycle == null) {
            isCycle = Boolean.FALSE;
        }
        if (isCycle) {
            if (task.getLimitExecuteNumber() != 1) {
                String cron = task.getCron();
                if (StringUtils.isBlank(cron)) {
                    VerificationHelper.required(task.getPeriodTimeValue(), "循环任务未指定执行周期时间");
                    VerificationHelper.required(task.getPeriodTimeUnit(), "循环任务未指定执行周期时间单位");
                    VerificationHelper.required(IntValueEnumerationHelper.intValueOf(TimeUnit.class, task.getPeriodTimeUnit()), "循环任务指定的执行周期时间单位不合法，请参考TimeUnit");
                } else {
                    if (!CronSequenceGenerator.isValidExpression(cron)) {
                        throw new IllegalArgumentException("循环任务指定的cron表达式不合法，请参考CronSequenceGenerator");
                    }
                }
            }
            if (task.getPeriodTimeAnchor() == null) {
                task.setPeriodTimeAnchor(TimeAnchor.BEFORE.intValue());
            }
            VerificationHelper.required(IntValueEnumerationHelper.intValueOf(TimeAnchor.class, task.getPeriodTimeAnchor()), "循环任务指定的执行周期时间锚点不合法，请参考TimeAnchor");
        }
        if (task.getTimeout() == null) {
            task.setTimeout(3000);
        }
        if (task.getNextExecuteTime() == null) {
            //默认立即执行一次
            task.setNextExecuteTime(System.currentTimeMillis());
        }
        if (task.getLimitRetryNumber() == null) {
            //默认不限重试次数
            task.setLimitRetryNumber(-1);
        }
        if (task.getLimitRetryNumber() <= -1) {
            task.setLimitRetryNumber(-1);
        }
        if (task.getNextRetryTimeValue() == null) {
            task.setNextRetryTimeValue(0);
        }
        if (task.getNextRetryTimeUnit() == null) {
            task.setNextRetryTimeUnit(TimeUnit.SECOND.intValue());
        }
        VerificationHelper.required(IntValueEnumerationHelper.intValueOf(TimeUnit.class, task.getNextRetryTimeUnit()), "任务的执行重试周期时间单位不合法，请参考TimeUnit");
        initSerialParams(task);
    }

    private void initSerialParams(ScheduleItem task) {
        initSerialParams(null, task);
    }

    private void initSerialParams(ScheduleEnvironment environment) {
        initSerialParams(environment, null);
    }

    private void initSerialParams(ScheduleEnvironment environment, ScheduleItem task) {
        if (task == null) {
            if (environment == null) {
                return;
            }
            environment.init();
            return;
        }
        if (environment == null) {
            environment = new ScheduleEnvironment();
            environment.transferFrom(task);
        }
        environment.init();
        environment.transferTo(task);
        if (StringUtils.isBlank(task.getOwnSign())) {
            task.setOwnSign(ScheduleSystemInfo.STATIC_OWN_SIGN_VALUE);
        }
    }
}
