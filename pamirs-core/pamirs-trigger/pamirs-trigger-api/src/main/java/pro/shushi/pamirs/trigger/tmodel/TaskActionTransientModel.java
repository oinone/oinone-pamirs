package pro.shushi.pamirs.trigger.tmodel;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Adamancy Zhang on 2021-02-04 15:58
 */
@Base
@Model.model(TaskActionTransientModel.MODEL_MODEL)
@Model(displayName = "任务表传输模型")
public class TaskActionTransientModel extends IdModel {

    private static final long serialVersionUID = 5554620058119719506L;

    public static final String MODEL_MODEL = "trigger.TaskActionTransientModel";

    @Field(displayName = "技术名称", summary = "任务参数: 技术名称")
    private String technicalName;

    @Field(displayName = "数据表number", summary = "控制参数: 数据表number")
    private Integer tableNum;

    @Field(displayName = "任务类型", summary = "任务参数: 任务类型")
    private String taskType;

    @Field.Text
    @Field(displayName = "备注", summary = "任务参数: 备注信息")
    private String remark;

    @Field(displayName = "执行函数命名空间", summary = "函数参数: 接口名称")
    private String interfaceName;

    @Field(displayName = "执行函数名称", summary = "函数参数: 方法名称")
    private String methodName;

    // version -> FunctionDefinition.version
    @Field(displayName = "接口版本", summary = "函数参数: 接口版本")
    private String version;

    // group -> FunctionDefinition.group
    @Field(displayName = "接口分组", summary = "函数参数: 接口分组")
    private String group;

    // timeout -> FunctionDefinition.timeout
    @Field(displayName = "超时时间", summary = "函数参数: 接口调用超时时间")
    private Integer timeout;

    @Field(displayName = "租户", summary = "环境参数: 租户")
    private String tenant;

    @Field(displayName = "环境", summary = "环境参数: 环境")
    private String env;

    @Field(displayName = "所有者标记（隔离环境域）", summary = "环境参数: 所有者标记(隔离环境域)")
    private String ownSign;

    @Field(displayName = "应用名", summary = "环境参数: 应用名")
    private String application;

    @Field(displayName = "业务id", summary = "订阅者业务id(取模字段)")
    private Long bizId;

    @Field(displayName = "父业务id", summary = "订阅者业务id(取模字段)")
    private Long parentBizId;

    @Field(displayName = "业务code", summary = "业务编码")
    private String bizCode;

    @Field.Text
    @Field(displayName = "任务上下文", summary = "任务上下文")
    private String context;

    @Field(displayName = "最大执行次数", summary = "策略参数: 最大执行次数")
    private Integer limitExecuteNumber;

    @Field(displayName = "是否循环任务", summary = "策略参数: 是否循环任务")
    private Boolean isCycle;

    @Field(displayName = "执行周期时间", summary = "策略参数: 执行周期时间")
    private Integer periodTimeValue;

    @Field(displayName = "执行周期时间单位", summary = "策略参数: 执行周期时间单位")
    private Integer periodTimeUnit;

    @Field(displayName = "执行周期时间锚点", summary = "策略参数: 执行周期时间锚点")
    private Integer periodTimeAnchor;

    @Field(displayName = "最大重试次数", summary = "策略参数: 最大重试次数")
    private Integer limitRetryNumber;

    @Field(displayName = "下次重试间隔时间", summary = "策略参数: 下次重试间隔时间")
    private Integer nextRetryTimeValue;

    @Field(displayName = "下次重试间隔时间单位", summary = "策略参数: 下次重试间隔时间单位")
    private Integer nextRetryTimeUnit;

    @Field(displayName = "执行次数", summary = "控制参数: 执行次数")
    private Integer executeNumber;

    @Field(displayName = "下次执行时间", summary = "控制参数: 下次执行时间")
    private Date nextExecuteTime;

    @Field(displayName = "最新执行时间", summary = "控制参数: 最新执行时间")
    private Date lastExecuteTime;

    @Field(displayName = "锚定执行时间", summary = "控制参数: 执行成功后，推算下次执行时间使用的标准时间")
    private Date anchorExecuteTime;

    @Field(displayName = "重试次数", summary = "控制参数: 重试次数")
    private Integer retryNumber;

    @Field(displayName = "任务状态", summary = "控制参数: 任务状态")
    private Integer taskStatus;

    @Field(displayName = "是否已迁移", summary = "控制参数: 是否已迁移")
    private Boolean isTransfer;

    @Field(displayName = "是否已取消", summary = "控制参数: 是否已取消")
    private Boolean isCanceled;

    @Field.Text
    @Field(displayName = "异常日志", summary = "控制参数: 异常堆栈信息,注意要控制大小")
    private String errorLog;

    public static TaskActionTransientModel transfer(ScheduleItem item) {
        TaskActionTransientModel taskAction = new TaskActionTransientModel();
        taskAction.setTechnicalName(item.getTechnicalName())
                .setTableNum(item.getTableNum())
                .setTaskType(item.getTaskType())
                .setRemark(item.getRemark())
                .setInterfaceName(item.getInterfaceName())
                .setMethodName(item.getMethodName())
                .setVersion(item.getVersion())
                .setGroup(item.getGroup())
                .setTimeout(item.getTimeout())
                .setTenant(item.getTenant())
                .setEnv(item.getEnv())
                .setOwnSign(item.getOwnSign())
                .setApplication(item.getApplication())
                .setBizId(item.getBizId())
                .setParentBizId(item.getParentBizId())
                .setBizCode(item.getBizCode())
                .setContext(item.getContext())
                .setLimitExecuteNumber(item.getLimitExecuteNumber())
                .setIsCycle(item.getIsCycle())
                .setPeriodTimeValue(item.getPeriodTimeValue())
                .setPeriodTimeUnit(item.getPeriodTimeUnit())
                .setPeriodTimeAnchor(item.getPeriodTimeAnchor())
                .setLimitRetryNumber(item.getLimitRetryNumber())
                .setNextRetryTimeValue(item.getNextRetryTimeValue())
                .setNextRetryTimeUnit(item.getNextRetryTimeUnit())
                .setExecuteNumber(item.getExecuteNumber())
                .setNextExecuteTime(Optional.ofNullable(item.getNextExecuteTime()).map(Date::new).orElse(null))
                .setLastExecuteTime(Optional.ofNullable(item.getLastExecuteTime()).map(Date::new).orElse(null))
                .setAnchorExecuteTime(Optional.ofNullable(item.getAnchorExecuteTime()).map(Date::new).orElse(null))
                .setRetryNumber(item.getRetryNumber())
                .setTaskStatus(item.getTaskStatus())
                .setIsTransfer(item.getIsTransfer())
                .setIsCanceled(item.getIsCanceled())
                .setErrorLog(item.getErrorLog())
                .setId(item.getId())
                .setCreateDate(Optional.ofNullable(item.getCreateDate()).map(v -> v.atZone(ZoneId.systemDefault())).map(ZonedDateTime::toInstant).map(Date::from).orElse(null))
                .setWriteDate(Optional.ofNullable(item.getWriteDate()).map(v -> v.atZone(ZoneId.systemDefault())).map(ZonedDateTime::toInstant).map(Date::from).orElse(null));
        if (StringUtils.isNotEmpty(item.getRemark())) {
            List<String> remarks = Splitter.on(",").splitToList(item.getRemark());
            taskAction.setRemark(remarks.get(0));
        }
        return taskAction;
    }

    public static List<TaskActionTransientModel> transfers(List<ScheduleItem> items) {
        List<TaskActionTransientModel> list = new ArrayList<>();
        for (ScheduleItem item : items) {
            list.add(transfer(item));
        }
        return list;
    }

    public static ScheduleItem reverse(TaskActionTransientModel task) {
        return new ScheduleItem()
                .setTechnicalName(task.getTechnicalName())
                .setTableNum(task.getTableNum())
                .setTaskType(task.getTaskType())
                .setRemark(task.getRemark())
                .setInterfaceName(task.getInterfaceName())
                .setMethodName(task.getMethodName())
                .setVersion(task.getVersion())
                .setGroup(task.getGroup())
                .setTimeout(task.getTimeout())
                .setTenant(task.getTenant())
                .setEnv(task.getEnv())
                .setOwnSign(task.getOwnSign())
                .setApplication(task.getApplication())
                .setBizId(task.getBizId())
                .setParentBizId(task.getParentBizId())
                .setBizCode(task.getBizCode())
                .setContext(task.getContext())
                .setLimitExecuteNumber(task.getLimitExecuteNumber())
                .setIsCycle(task.getIsCycle())
                .setPeriodTimeValue(task.getPeriodTimeValue())
                .setPeriodTimeUnit(task.getPeriodTimeUnit())
                .setPeriodTimeAnchor(task.getPeriodTimeAnchor())
                .setLimitRetryNumber(task.getLimitRetryNumber())
                .setNextRetryTimeValue(task.getNextRetryTimeValue())
                .setNextRetryTimeUnit(task.getNextRetryTimeUnit())
                .setExecuteNumber(task.getExecuteNumber())
                .setNextExecuteTime(Optional.ofNullable(task.getNextExecuteTime()).map(Date::getTime).orElse(null))
                .setLastExecuteTime(Optional.ofNullable(task.getLastExecuteTime()).map(Date::getTime).orElse(null))
                .setAnchorExecuteTime(Optional.ofNullable(task.getAnchorExecuteTime()).map(Date::getTime).orElse(null))
                .setRetryNumber(task.getRetryNumber())
                .setTaskStatus(task.getTaskStatus())
                .setIsTransfer(task.getIsTransfer())
                .setIsCanceled(task.getIsCanceled())
                .setErrorLog(task.getErrorLog())
                .setId(task.getId())
                .setCreateDate(Optional.ofNullable(task.getCreateDate()).map(Date::toInstant).map(v -> v.atZone(ZoneId.systemDefault())).map(ZonedDateTime::toLocalDateTime).orElse(null))
                .setWriteDate(Optional.ofNullable(task.getWriteDate()).map(Date::toInstant).map(v -> v.atZone(ZoneId.systemDefault())).map(ZonedDateTime::toLocalDateTime).orElse(null))
                ;
    }

    public static List<ScheduleItem> reverses(List<TaskActionTransientModel> items) {
        List<ScheduleItem> list = new ArrayList<>();
        for (TaskActionTransientModel item : items) {
            list.add(reverse(item));
        }
        return list;
    }
}
