package pro.shushi.pamirs.expression.tmodel;

import pro.shushi.pamirs.expression.enmu.ExpressionCellType;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

@Base
@Model(displayName = "模型设计器表达式最小单位定义")
@Model.model(ExpressionCell.MODEL_MODEL)
public class ExpressionCell extends ExpressionDisplay/*最小单元连接目前只能是拼接,因此只继承顶级显示模型*/ {

    public static final String MODEL_MODEL = "expression.ExpressionCell";

    @Field.Enum
    @Field(displayName = "表达式最小单位类型", summary = "表达式最小单位类型")
    private ExpressionCellType cellType;


}
