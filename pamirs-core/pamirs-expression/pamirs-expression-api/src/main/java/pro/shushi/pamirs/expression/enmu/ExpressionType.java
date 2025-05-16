package pro.shushi.pamirs.expression.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExpressionType.DICTIONARY, displayName = "表达式类型", summary = "表达式类型")
public enum ExpressionType implements IEnum<String> {

    BOOLEAN_CONDITION("BOOLEAN_CONDITION", "布尔表达式", "布尔表达式"),
    RSQL_CONDITION("RSQL_CONDITION", "rsql表达式", "rsql表达式"),
    OPERATION("OPERATION", "运算表达式", "运算表达式"),
    CONDITION("CONDITION", "条件表达式", "条件表达式"),
    ;

    public static final String DICTIONARY = "expression.ExpressionType";

    private final String value;
    private final String displayName;
    private final String help;

    ExpressionType(String value, String displayName, String help) {
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
