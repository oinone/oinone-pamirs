package pro.shushi.pamirs.trigger.tbschedule.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.KeyGeneratorEnum;

/**
 * @author Adamancy Zhang
 * @date 2020-11-16 14:51
 */
@Base
@Model.model(PamirsSchedule.MODEL_MODEL)
@Model.Advanced(unique = {"technicalName"}, index = {"taskType,taskStatus,nextExecuteTime", "bizId"}, table = "pamirs_schedule")
@Model.Persistence
@Model(displayName = "TBSchedule任务分发表")
public class PamirsSchedule extends IdModel {

    private static final long serialVersionUID = -4890366879851414162L;

    public static final String MODEL_MODEL = "trigger.PamirsSchedule";

    @Base
    @Field.PrimaryKey(keyGenerator = KeyGeneratorEnum.AUTO_INCREMENT)
    @Field.Integer
    @Field(displayName = "ID", summary = "ID字段，唯一自增索引", required = true, priority = 5)
    private Long id;

    @Field.String(size = 256)
    @Field(displayName = "技术名称", summary = "任务参数: 技术名称")
    private String technicalName;

    @Field.Integer
    @Field(displayName = "数据表号", summary = "控制参数: 数据表number")
    private Integer tableNum;

    @Field.String(size = 256)
    @Field(displayName = "任务类型", summary = "任务参数: 任务类型")
    private String taskType;

    @Field.String(size = 1024)
    @Field(displayName = "备注", summary = "任务参数: 备注信息")
    private String remark;

    @Field.String(size = 256)
    @Field(displayName = "执行函数命名空间", summary = "函数参数: 接口名称")
    private String interfaceName;

    @Field.String(size = 256)
    @Field(displayName = "执行函数名称", summary = "函数参数: 方法名称")
    private String methodName;

    // version -> FunctionDefinition.version
    @Field.String(size = 256)
    @Field(displayName = "接口版本", summary = "函数参数: 接口版本")
    private String version;

    //group -> FunctionDefinition.group
    @Field.String(size = 256)
    @Field(displayName = "接口分组", summary = "函数参数: 接口分组")
    private String group;

    // timeout -> FunctionDefinition.timeout
    @Field.Advanced(columnDefinition = "int(11) DEFAULT '3000'")
    @Field(displayName = "超时时间", summary = "函数参数: 接口调用超时时间")
    private Integer timeout;

    @Field.String(size = 128)
    @Field(displayName = "租户", summary = "环境参数: 租户")
    private String tenant;

    @Field.String(size = 128)
    @Field(displayName = "环境", summary = "环境参数: 环境")
    private String env;

    @Field.Integer
    @Field(displayName = "用户Id", summary = "环境参数: 用户Id")
    private Long userId;

    @Field.String
    @Field(displayName = "用户名", summary = "环境参数: 用户名")
    private String username;

    @Field.String(size = 128)
    @Field(displayName = "所有者标记（隔离环境域）", summary = "环境参数: 所有者标记(隔离环境域)")
    private String ownSign;

    @Field.String(size = 256)
    @Field(displayName = "应用名", summary = "环境参数: 应用名")
    private String application;

    @Field.Integer
    @Field(displayName = "业务id", summary = "订阅者业务id(取模字段)")
    private Long bizId;

    @Field.Integer
    @Field(displayName = "父业务id", summary = "订阅者业务id(取模字段)")
    private Long parentBizId;

    @Field.String(size = 256)
    @Field(displayName = "业务code", summary = "业务编码")
    private String bizCode;

    @Field.Advanced(columnDefinition = "longtext")
    @Field(displayName = "任务上下文", summary = "任务上下文")
    private String context;

    @Field.Integer
    @Field(displayName = "最大执行次数", summary = "策略参数: 最大执行次数")
    private Integer limitExecuteNumber;

    @Field.Advanced(columnDefinition = "tinyint(1) DEFAULT '0'")
    @Field(displayName = "是否循环任务", summary = "策略参数: 是否循环任务")
    private Boolean isCycle;

    @Field.String(size = 128)
    @Field(displayName = "cron表达式", summary = "策略参数: 执行周期定义")
    private String cron;

    @Field.Integer
    @Field(displayName = "执行周期时间", summary = "策略参数: 执行周期时间")
    private Integer periodTimeValue;

    @Field.Advanced(columnDefinition = "tinyint(1)")
    @Field(displayName = "执行周期时间单位", summary = "策略参数: 执行周期时间单位")
    private Integer periodTimeUnit;

    @Field.Advanced(columnDefinition = "tinyint(1) DEFAULT '-1'")
    @Field(displayName = "执行周期时间锚点", summary = "策略参数: 执行周期时间锚点")
    private Integer periodTimeAnchor;

    @Field.Advanced(columnDefinition = "int(11) DEFAULT '-1'")
    @Field(displayName = "最大重试次数", summary = "策略参数: 最大重试次数")
    private Integer limitRetryNumber;

    @Field.Advanced(columnDefinition = "int(11) DEFAULT '1'")
    @Field(displayName = "下次重试间隔时间", summary = "策略参数: 下次重试间隔时间")
    private Integer nextRetryTimeValue;

    @Field.Advanced(columnDefinition = "tinyint(1) DEFAULT '13'")
    @Field(displayName = "下次重试间隔时间单位", summary = "策略参数: 下次重试间隔时间单位")
    private Integer nextRetryTimeUnit;

    @Field.Advanced(columnDefinition = "bigint(20) DEFAULT '0'")
    @Field(displayName = "执行次数", summary = "控制参数: 执行次数")
    private Integer executeNumber;

    @Field.Integer
    @Field(displayName = "下次执行时间", summary = "控制参数: 下次执行时间")
    private Long nextExecuteTime;

    @Field.Integer
    @Field(displayName = "最新执行时间", summary = "控制参数: 最新执行时间")
    private Long lastExecuteTime;

    @Field.Integer
    @Field(displayName = "锚定执行时间", summary = "控制参数: 执行成功后，推算下次执行时间使用的标准时间")
    private Long anchorExecuteTime;

    @Field.Advanced(columnDefinition = "int(11) DEFAULT '0'")
    @Field(displayName = "重试次数", summary = "控制参数: 重试次数")
    private Integer retryNumber;

    @Field.Advanced(columnDefinition = "tinyint(1) DEFAULT '0'")
    @Field(displayName = "任务状态", summary = "控制参数: 任务状态")
    private Integer taskStatus;

    @Field.Advanced(columnDefinition = "tinyint(1) DEFAULT '0'")
    @Field(displayName = "是否已迁移", summary = "控制参数: 是否已迁移")
    private Boolean isTransfer;

    @Field.Advanced(columnDefinition = "tinyint(1) DEFAULT '0'")
    @Field(displayName = "是否已取消", summary = "控制参数: 是否已取消")
    private Boolean isCanceled;

    @Field.Advanced(columnDefinition = "longtext")
    @Field(displayName = "异常日志", summary = "控制参数: 异常堆栈信息,注意要控制大小")
    private String errorLog;
}
