package pro.shushi.pamirs.expression.tmodel;

import pro.shushi.pamirs.expression.enmu.ExpressionRowType;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

import java.util.List;

@Base
@Model(displayName = "模型设计器表达式行定义")
@Model.model(ExpressionRow.MODEL_MODEL)
public class ExpressionRow extends ExpressionConnectDisplay {

    public static final String MODEL_MODEL = "expression.ExpressionRow";

    @Field.one2many
    @Field(displayName = "运算符占位符", summary = "运算符左侧占位符")
    private List<ExpressionBlock> blockList;

    @Field.Enum
    @Field(displayName = "表达式行类型", summary = "表达式行类型")
    private ExpressionRowType rowType;

}
