package pro.shushi.pamirs.expression.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExpressionRowType.DICTIONARY, displayName = "表达式行类型", summary = "表达式行类型")
public enum ExpressionRowType implements IEnum<String> {

    FUN("FUN", "内置函数", "内置函数"),
    CONSTANT("CONSTANT", "常量", "常量"),
    VARIABLE("VARIABLE", "变量", "变量"),
    LEFT_BRACKET("LEFT_BRACKET", "左括号", "左括号"),
    RIGHT_BRACKET("RIGHT_BRACKET", "右括号", "右括号"),
    MIX("MIX", "混合", "混合"),
    ;

    public static final String DICTIONARY = "expression.ExpressionRowType";

    private final String value;
    private final String displayName;
    private final String help;

    ExpressionRowType(String value, String displayName, String help) {
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
