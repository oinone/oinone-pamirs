package pro.shushi.pamirs.trigger.model;

import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * @author Adamancy Zhang
 * @date 2020-11-02 17:34
 */
@Base
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model.model(ExecuteTaskAction.MODEL_MODEL)
@Model(displayName = "异步执行任务")
public class ExecuteTaskAction extends AbstractTaskAction {

    private static final long serialVersionUID = -7270741634440093993L;

    public static final String MODEL_MODEL = "trigger.ExecuteTaskAction";

    @Base
    @Field.String
    @Field(displayName = "任务类型")
    private String taskType;

    @Field.Integer
    @Field(displayName = "延时执行时间")
    private Integer delayTimeValue;

    @Field.Enum
    @Field(displayName = "延时执行时间单位")
    private TimeUnitEnum delayTimeUnit;

    @Base
    @Field.Integer
    @Field(displayName = "最大重试次数", defaultValue = "-1")
    private Integer limitRetryNumber;

    @Base
    @Field.Integer
    @Field(displayName = "下次重试执行时间", defaultValue = "3", summary = "根据执行时间向后延时的时间，如MINUTES单位下，该值设为1，则表示在执行函数1分钟后重新执行执行函数")
    private Integer nextRetryTimeValue;

    @Base
    @Field.Enum
    @Field(displayName = "下次重试时间单位", defaultValue = "SECOND", summary = "根据执行时间向后延时的时间单位")
    private TimeUnitEnum nextRetryTimeUnit;

    @Base
    @Field.Integer
    @Field(displayName = "业务id")
    private Long bizId;

    @Base
    @Field.String
    @Field(displayName = "业务code")
    private String bizCode;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }
}
