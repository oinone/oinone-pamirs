package pro.shushi.pamirs.trigger.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyQueueSelector;
import pro.shushi.pamirs.framework.connectors.event.api.NotifySendCallback;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyTagsGenerator;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.trigger.enmu.NotifyTypeEnum;

import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2020-12-20 12:34
 */
@Base
@Model.Advanced(unique = {"displayName", "executeNamespace, executeFun"})
@Model.model(NotifyDefinition.MODEL_MODEL)
@Model(displayName = "消息通知", summary = "目前支持RocketMQ和Aliyun的RocketMQ")
public class NotifyDefinition extends IdModel {

    public static final String MODEL_MODEL = "trigger.NotifyDefinition";

    @Base
    @Field.String
    @Field(displayName = "显示名称", required = true)
    private String displayName;

    @Base
    @Field.String
    @Field(displayName = "描述", summary = "描述", required = true)
    private String description;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"executeNamespace", "executeFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "执行函数", invisible = true)
    private FunctionDefinition executeFunction;

    @Base
    @Field.String
    @Field(displayName = "执行函数命名空间", summary = "执行函数命名空间", required = true, invisible = true)
    private String executeNamespace;

    @Base
    @Field.String
    @Field(displayName = "执行函数编码", summary = "执行函数编码", required = true, invisible = true)
    private String executeFun;

    @Base
    @Field.Enum
    @Field(displayName = "消息队列类型", summary = "目前仅支持RocketMQ", required = true)
    private NotifyTypeEnum notifyType;

    @Base
    @Field.Boolean
    @Field(displayName = "是否激活", summary = "是否开启该消息通知", required = true, defaultValue = "true")
    private Boolean active;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"id"}, referenceFields = {"notifyDefinitionId"})
    @Field(displayName = "目标函数列表", summary = "当消息队列角色为Consumer相关项时，该属性标识消费者的消费类型是并发消费还是顺序消费或其他实现")
    private List<NotifyFunctionDefinition> targetFunctionList;

    @JsonIgnore
    private transient NotifyTagsGenerator tagsGenerator;

    @JsonIgnore
    private transient NotifySendCallback sendCallback;

    @JsonIgnore
    private transient NotifyQueueSelector queueSelector;
}
