package pro.shushi.pamirs.expression.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

@Base
@Model(displayName = "模型设计器表达式连接显示")
@Model.model(ExpressionConnectDisplay.MODEL_MODEL)
public class ExpressionConnectDisplay extends ExpressionDisplay {

    public static final String MODEL_MODEL = "expression.ExpressionConnectDisplay";

    @Field.many2one
    @Field(displayName = "行连接符", summary = "行连接符")
    private ExpressionCell connector;


}
