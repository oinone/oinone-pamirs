package pro.shushi.pamirs.expression.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

@Base
@Model(displayName = "模型设计器表达式显示")
@Model.model(ExpressionDisplay.MODEL_MODEL)
public class ExpressionDisplay extends TransientModel {

    public static final String MODEL_MODEL = "expression.ExpressionDisplay";

    @Field.String
    @Field(displayName = "原始量显示", summary = "通常是实际运行的技术变量或值")
    private String original;

    @Field.String
    @Field(displayName = "翻译后量显示", summary = "通常是中文显示名称")
    private String translation;

    @Field.String
    @Field(displayName = "值", summary = "供前端使用")
    private String value;

    @Field.Enum
    @Field(displayName = "业务类型", summary = "业务类型")
    private TtypeEnum ttype;

    @Field.Boolean
    @Field(displayName = "是否二进制枚举", summary = "是否二进制枚举", defaultValue = "false")
    private Boolean bitEnum;

    @Field.Boolean
    @Field(displayName = "多值字段", summary = "多值字段")
    private Boolean multi;

    @Field.Boolean
    @Field(displayName = "是否存储", summary = "是否存储")
    private Boolean store;
}
