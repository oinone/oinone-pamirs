package pro.shushi.pamirs.meta.domain.fun;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

/**
 * rsql表达式
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.Advanced(inherited = "base.IdModel", unInheritedFields = {"result","message"}, unInheritedFunctions = {"execute"})
@Model.model("base.RsqlExpression")
@Model(displayName = "校验表达式", summary = "校验表达式")
public class RsqlExpression extends ExpressionDefinition {

    @Base
    @Field.String
    @Field(displayName = "表达式", summary = "表达式", check = "checkRsqlExpression", required = true)
    private String expression;

}
