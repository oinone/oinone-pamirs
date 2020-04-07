package pro.shushi.pamirs.meta.domain.fun;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * 扩展点实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaModel(priority = 12)
@Base
@Model.Advanced(unique = {"namespace,name"})
@Model.model("base.ExtpointInstance")
@Model(displayName = "扩展点实现", summary = "扩展点实现")
public class ExtPointInstance extends IdModel {

    @Base
    @Field.String
    @Field(displayName = "实例名称", required = true)
    private String displayName;

    @Base
    @Field.String
    @Field(displayName = "技术名称", summary = "技术名称", required = true)
    private String name;

    @Base
    @Field.String
    @Field(displayName = "命名空间", summary = "扩展点命名空间", invisible = true)
    private String namespace;

    @Base
    @Field.one2one
    @Field.Relation(relationFields = {"namespace","executeFun"})
    @Field(displayName = "执行函数", invisible = true)
    private FunctionDefinition executeFunction;

    @Base
    @Field.String
    @Field(displayName = "执行函数编码", summary = "执行函数编码", required = true, invisible = true, store = NullableBoolEnum.TRUE)
    private String executeFun;

    @Base
    @Field.String
    @Field(displayName = "描述", summary = "描述", required = true)
    private String description;

    @Base
    @Field.Integer
    @Field(displayName = "优先级", required = true, defaultValue = "10")
    private Integer priority;

    @Base
    @Field.many2one
    @Field.Relation(store = false)
    @Field(displayName = "表达式")
    private ExpressionDefinition expressionDefinition;

    @Base
    @Field.Related(related = {"expressionDefinition","expression"})
    @Field.String(size = 256)
    @Field(displayName = "表达式")
    private String expression;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"namespace","name"})
    @Field(displayName = "扩展点", invisible = true)
    private ExtPoint extPoint;

}

