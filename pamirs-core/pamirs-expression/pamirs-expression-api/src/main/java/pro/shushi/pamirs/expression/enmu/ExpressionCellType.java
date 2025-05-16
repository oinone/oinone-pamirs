package pro.shushi.pamirs.expression.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExpressionCellType.DICTIONARY, displayName = "表达式最小单位类型", summary = "表达式最小单位类型")
public enum ExpressionCellType implements IEnum<String> {

    FUN("FUN", "内置函数", "内置函数"),
    CONSTANT("CONSTANT", "常量", "常量"),
    VARIABLE("VARIABLE", "变量", "变量"),
    FIELD("FIELD", "字段", "数据库字段,仅rsql使用"),
    OPTION("OPTION", "选项", "数据字典/布尔"),
    @Deprecated
    LEFT_BRACKET("LEFT_BRACKET", "左括号", "左括号"),
    @Deprecated
    RIGHT_BRACKET("RIGHT_BRACKET", "右括号", "右括号"),
    SESSION("SESSION", "上下文", "登录上下文"),
    ;

    public static final String DICTIONARY = "expression.ExpressionCellType";

    private final String value;
    private final String displayName;
    private final String help;

    ExpressionCellType(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String help() {
        return help;
    }
}
