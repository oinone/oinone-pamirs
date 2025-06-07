package pro.shushi.pamirs.expression.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

/**
 * DesignerExpressionDefinition
 *
 * @author drome on 2021/08/09 11:38.
 */
@Base
@Model.MultiTableInherited(
        type = "ExpressionDHistory"
)
@Model(displayName = "模型设计器表达式定义历史")
@Model.model(ExpressionDHistory.MODEL_MODEL)
@Model.Advanced(index = "model,field,key", unique = "code,version")
public class ExpressionDHistory extends ExpressionDefine {

    public static final String MODEL_MODEL = "expression.ExpressionDHistory";

    @Field.String
    @Field.Advanced(columnDefinition = "varchar(384)")
    @Field(displayName = "编码", required = true)
    private String code;

    @Field.String
    @Field(displayName = "表达式归属行", summary = "可确定唯一行的key")
    private String version;

}
