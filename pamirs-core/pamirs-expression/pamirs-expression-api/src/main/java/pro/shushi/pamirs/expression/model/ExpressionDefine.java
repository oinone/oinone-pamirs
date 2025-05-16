package pro.shushi.pamirs.expression.model;

import pro.shushi.pamirs.expression.enmu.ExpressionType;
import pro.shushi.pamirs.expression.tmodel.ExpressionDisplay;
import pro.shushi.pamirs.expression.tmodel.ExpressionRow;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

/**
 * ExpressionDefinition同名了,gql有问题
 *
 * @author drome on 2021/08/09 11:38.
 */
@Base
@Model(displayName = "模型设计器表达式定义")
@Model.model(ExpressionDefine.MODEL_MODEL)
@Model.Advanced(index = "model,field,key")
public class ExpressionDefine extends IdModel {

    public static final String MODEL_MODEL = "expression.ExpressionDefine";

    @Field.String
    @Field.Advanced(columnDefinition = "varchar(512)")
    @Field(displayName = "编码", unique = true, required = true)
    private String code;

    @Field.many2one
    @Field.Advanced(columnDefinition = "text")
    @Field.Relation(store = false)
    @Field(displayName = "表达式显示", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private ExpressionDisplay expressionDisplay;

    @Field.Enum
    @Field(displayName = "表达式类型", summary = "表达式类型")
    private ExpressionType expressionType;

    @Field.one2many
    @Field.Advanced(columnDefinition = "text")
    @Field.Relation(store = false)
    @Field(displayName = "表达式集合", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private List<ExpressionRow> rowList;

    //定位表达式作用域.并且用于构造code,保证唯一
    @Field.String
    @Field(displayName = "表达式归属模型", summary = "表达式归属对象,例如:base.View,可理解为表", required = true)
    private String model;//table

    @Field.String
    @Field(displayName = "表达式归属字段", summary = "表达式归属变量,例如:filter,可理解为列")
    private String field;//Column

    @Field.String
    @Field.Advanced(columnDefinition = "varchar(256)")
    @Field(displayName = "表达式归属行", summary = "可确定唯一行的key")
    private String key;//row

}
