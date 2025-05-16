package pro.shushi.pamirs.trigger.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * @author Adamancy Zhang
 * @date 2020-11-02 19:28
 */
@Base
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model.model(AbstractTaskAction.MODEL_MODEL)
@Model(displayName = "任务抽象基类", labelFields = "name")
public abstract class AbstractTaskAction extends MetaBaseModel {

    private static final long serialVersionUID = -4326522559143308705L;

    public static final String MODEL_MODEL = "trigger.AbstractTaskAction";

    @Base
    @Field.String
    @Field(displayName = "显示名称", required = true)
    private String displayName;

    @Base
    @Field.String
    @Field(displayName = "描述", summary = "描述")
    private String description;

    @Base
    @Field.String
    @Field(displayName = "租户", invisible = true)
    private String tenant;

    @Base
    @Field.String
    @Field(displayName = "环境", invisible = true)
    private String env;

    @Base
    @Field.String
    @Field(displayName = "所有者标记", invisible = true)
    private String ownSign;

    @Base
    @Field.String
    @Field(displayName = "应用名称", invisible = true)
    private String application;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"executeNamespace", "executeFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "执行函数")
    private FunctionDefinition executeFunction;

    @Base
    @Field.String
    @Field(displayName = "执行函数命名空间", summary = "触发函数命名空间", required = true, invisible = true)
    private String executeNamespace;

    @Base
    @Field.String
    @Field(displayName = "执行函数编码", summary = "触发函数编码", required = true, invisible = true)
    private String executeFun;

    @Base
    @Field.Text
    @Field(displayName = "上下文")
    private String context;

    @Base
    @Field.Integer
    @Field(displayName = "首次执行时间")
    private Long firstExecuteTime;

    @Base
    @Field.Boolean
    @Field(displayName = "是否启用", required = true, defaultValue = "true")
    private Boolean active;
}
