package pro.shushi.pamirs.trigger.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.trigger.TriggerModule;
import pro.shushi.pamirs.trigger.enmu.TriggerConditionEnum;

/**
 * @author Adamancy Zhang
 * @date 2020-11-02 21:41
 */
@MetaSimulator(module = TriggerModule.MODULE_MODULE, onlyBasicTypeField = false)
@MetaModel
@Base
@Model.Advanced(unique = {"technicalName"})
@Model.model(TriggerTaskAction.MODEL_MODEL)
@Model(displayName = "触发任务")
public class TriggerTaskAction extends ExecuteTaskAction {

    private static final long serialVersionUID = 6544096542366094300L;

    public static final String MODEL_MODEL = "trigger.TriggerTaskAction";

    @Base
    @Field.String
    @Field(displayName = "技术名称", required = true)
    private String technicalName;

    @Base
    @Field.String
    @Field(displayName = "触发模型", summary = "触发模型", required = true)
    private String model;

    @Base
    @Field.String
    @Field(displayName = "触发场景", summary = "触发场景", required = true)
    private TriggerConditionEnum condition;

    @Base
    @Field.String
    @Field(displayName = "装配上下文参数", summary = "装配上下文参数名称", invisible = true)
    private String wiredContext;

    @Base
    @Field.String
    @Field(displayName = "事件Id参数", summary = "EventId参数名称，MQ为MsgId，只能处理String类型", invisible = true)
    private String eventParameter;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }
}
