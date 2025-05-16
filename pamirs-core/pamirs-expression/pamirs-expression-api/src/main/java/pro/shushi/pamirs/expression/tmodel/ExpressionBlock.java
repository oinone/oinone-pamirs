package pro.shushi.pamirs.expression.tmodel;

import pro.shushi.pamirs.expression.enmu.ExpressionBlockType;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

import java.util.List;

@Base
@Model(displayName = "模型设计器表达式块区域定义")
@Model.model(ExpressionBlock.MODEL_MODEL)
public class ExpressionBlock extends ExpressionConnectDisplay {

    public static final String MODEL_MODEL = "expression.ExpressionBlock";

    @Field.one2many
    @Field(displayName = "表达式块区域中所有最小单位", summary = "表达式块区域中所有最小单位")
    private List<ExpressionCell> cellList;

    @Field.Enum
    @Field(displayName = "表达式块区域类型", summary = "表达式块区域类型")
    private ExpressionBlockType blockType;

    /*
      连接符和内置函数两种类型过于特殊,使用拓展字段维护
     */

    @Field.many2one
    @Field(displayName = "内置函数", summary = "内置函数扩展字段")
    private ExpressionCell fun;

    @Field.one2many
    @Field(displayName = "内置函数参数", summary = "内置函数参数,一个参数一个块")
    private List<ExpressionBlock> funArgList;

}
