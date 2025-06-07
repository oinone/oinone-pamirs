package pro.shushi.pamirs.trigger.model;

import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.trigger.enmu.TriggerTimeAnchorEnum;

/**
 * @author Adamancy Zhang
 * @date 2020-11-02 17:34
 */
@MetaModel
@Base
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, unique = {"technicalName"})
@Model.model(ScheduleTaskAction.MODEL_MODEL)
@Model(displayName = "计划任务")
public class ScheduleTaskAction extends ExecuteTaskAction {

    private static final long serialVersionUID = -1542711515705689092L;

    public static final String MODEL_MODEL = "trigger.ScheduleTaskAction";

    @Base
    @Field.String
    @Field(displayName = "技术名称", required = true)
    private String technicalName;

    @Base
    @Field.Integer
    @Field(displayName = "限制执行次数", summary = "不设置或者小于零时表示无限制，设为0时按照执行任务的处理逻辑进行处理", defaultValue = "-1")
    private Integer limitExecuteNumber;

    @Base
    @Field.String
    @Field(displayName = "cron表达式")
    private String cron;

    @Base
    @Field.Integer
    @Field(displayName = "执行周期时间", summary = "根据下次执行时间向后计算的时间，如MINUTES单位下，该值设为1，则表示在执行指定函数1分钟后再次执行函数", required = true)
    private Integer periodTimeValue;

    @Base
    @Field.Enum
    @Field(displayName = "执行周期时间单位", summary = "根据下次执行时间向后计算的时间单位", required = true)
    private TimeUnitEnum periodTimeUnit;

    @Base
    @Field.Enum
    @Field(displayName = "触发时机", defaultValue = "START", required = true)
    private TriggerTimeAnchorEnum periodTimeAnchor;
}