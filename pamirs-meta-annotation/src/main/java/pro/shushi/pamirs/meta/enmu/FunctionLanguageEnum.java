package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 函数语言枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.FunctionLanguageType", displayName = "函数语言")
public enum FunctionLanguageEnum implements IEnum<String> {

    JAVA("JAVA", "JAVA", "java代码"),
    DSL("DSL", "DSL", "自定义代码"),
    JS("JS", "JS", "JS代码"),
    MVEL("MVEL", "MVEL", "动态表达式"),
    EXPRESSION("EXPRESSION", "EXPRESSION", "表达式"),
    GROOVY("GROOVY", "GROOVY", "groovy代码");

    private final String value;

    private final String displayName;

    private final String help;

    FunctionLanguageEnum(String value, String displayName, String help) {
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
