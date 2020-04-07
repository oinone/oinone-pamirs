package pro.shushi.pamirs.meta.domain.fun;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.Map;

import static pro.shushi.pamirs.meta.annotation.Field.serialize.JSON;

/**
 * 表达式
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.Expression")
@Model(displayName = "表达式", summary = "表达式", labelFields = {"displayName","expression"})
public class ExpressionDefinition extends IdModel {

    @Base
    @Field.String
    @Field(displayName = "显示名称")
    private String displayName;

    @Base
    @Field.String(size = 1024)
    @Field(displayName = "表达式", summary = "表达式", check = "checkExpression", required = true)
    private String expression;

    @Base
    @Field.String
    @Field(displayName = "描述", summary = "描述")
    private String description;

    @Base
    @Field.String
    @Field(displayName = "返回结果", summary = "返回结果", store = NullableBoolEnum.FALSE, invisible = true)
    private String result;

    @Base
    @Field.String
    @Field(displayName = "上下文", summary = "上下文", store = NullableBoolEnum.FALSE, serialize = JSON, invisible = true)
    private Map<String, Object> context;

    @Base
    @Field.Boolean
    @Field(displayName = "执行成功", summary = "执行成功", store = NullableBoolEnum.FALSE, invisible = true)
    private Boolean success;

    @Base
    @Field.String
    @Field(displayName = "返回信息", summary = "返回信息", invisible = true)
    private String message;

    @Function
    public ExpressionDefinition validate(ExpressionDefinition expressionDefinition){
        return expressionDefinition;
    }

    @Function
    public ExpressionDefinition execute(ExpressionDefinition expressionDefinition){
        return expressionDefinition;
    }

}
