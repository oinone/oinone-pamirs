package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 字符集校验规则枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.Collation", displayName = "字符集校验规则")
public enum CollationEnum implements IEnum<String> {

    DEFAULT("default", "DEFAULT", "DEFAULT"),
    BIN("bin", "BIN", "BIN"),
    GENERAL_CI("general_ci", "GENERAL_CI", "GENERAL_CI"),
    GENERAL_CS("general_cs", "GENERAL_CS", "GENERAL_CS"),
    ;

    private final String value;

    private final String displayName;

    private final String help;

    CollationEnum(String value, String displayName, String help) {
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
