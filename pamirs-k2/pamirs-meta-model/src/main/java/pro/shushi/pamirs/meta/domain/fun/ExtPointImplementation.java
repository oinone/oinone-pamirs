package pro.shushi.pamirs.meta.domain.fun;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import static pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation.MODEL_MODEL;

/**
 * 扩展点实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 12)
@Base
@Model.Advanced(unique = "namespace,name,executeNamespace,executeFun", priority = 25)
@Model.model(MODEL_MODEL)
@Model(displayName = "扩展点实现", summary = "扩展点实现")
public class ExtPointImplementation extends MetaBaseModel {

    public static final String MODEL_MODEL = "base.ExtPointImplementation";
    private static final long serialVersionUID = -2686420362172442420L;

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
    @Field.many2one
    @Field.Relation(relationFields = {"executeNamespace", "executeFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "执行函数", required = true)
    private FunctionDefinition executeFunction;

    @Base
    @Field(displayName = "执行函数命名空间", required = true)
    private String executeNamespace;

    @Base
    @Field(displayName = "执行函数编码", required = true)
    private String executeFun;

    @Base
    @Field.String
    @Field(displayName = "描述", summary = "描述", required = true)
    private String description;

    @Base
    @Field.Integer
    @Field(displayName = "优先级", required = true, defaultValue = "99")
    private Integer priority;

    @Base
    @Field.many2one
    @Field.Advanced(columnDefinition = "varchar(1024)")
    @Field.Relation(store = false)
    @Field(displayName = "表达式定义")
    private ExpressionDefinition expressionDefinition;

    @Base
    @Field.Related(related = {"expressionDefinition", "expression"})
    @Field.String(size = 256)
    @Field(displayName = "表达式", store = NullableBoolEnum.TRUE)
    private String expression;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"namespace", "name"})
    @Field(displayName = "扩展点", invisible = true)
    private ExtPoint extPoint;

    @Base
    @Field(displayName = "是否激活", defaultValue = "true")
    private Boolean active;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

}

