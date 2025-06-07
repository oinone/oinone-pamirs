package pro.shushi.pamirs.trigger.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

@Base
@Model.Advanced(unique = "topic, tags")
@Model.model("trigger.NotifyFunctionDefinition")
@Model(displayName = "消息通知目标函数", summary = "目前支持RocketMQ和Aliyun的RocketMQ")
public class NotifyFunctionDefinition extends IdModel {

    /**
     * <p>消费函数在用户定义的情况下是一定存在的，但通过配置定义的情况下是不一定存在的</p>
     */
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"targetNamespace", "targetFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "目标函数", invisible = true)
    private FunctionDefinition targetFunction;

    @Base
    @Field.String
    @Field(displayName = "目标函数命名空间", summary = "目标函数命名空间", required = true, invisible = true)
    private String targetNamespace;

    @Base
    @Field.String
    @Field(displayName = "目标函数编码", summary = "目标函数编码", required = true, invisible = true)
    private String targetFun;

    /**
     * 用于配置文件配置的消息通知做适配，用户不可自定义，所以没有标记{@link Field}注解
     */
    private String topic;

    /**
     * 用于配置文件配置的消息通知做适配，用户不可自定义，所以没有标记{@link Field}注解
     */
    private String tags;
}
