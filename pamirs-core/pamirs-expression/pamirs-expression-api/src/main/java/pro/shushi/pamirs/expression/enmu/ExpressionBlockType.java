package pro.shushi.pamirs.expression.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExpressionBlockType.DICTIONARY, displayName = "表达式块区域类型", summary = "表达式块区域类型")
public enum ExpressionBlockType implements IEnum<String> {

    FUN("FUN", "内置函数", "内置函数"),
    CONSTANT("CONSTANT", "常量", "常量"),
    VARIABLE("VARIABLE", "变量", "变量"),
    CONNECTOR("CONNECTOR", "连接符", "连接符"),
    @Deprecated
    LEFT_BRACKET("LEFT_BRACKET", "左括号", "左括号"),
    @Deprecated
    RIGHT_BRACKET("RIGHT_BRACKET", "右括号", "右括号"),
    ;

    public static final String DICTIONARY = "expression.ExpressionBlockType";

    private final String value;
    private final String displayName;
    private final String help;

    ExpressionBlockType(String value, String displayName, String help) {
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
