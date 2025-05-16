package pro.shushi.pamirs.boot.web.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = OperatorsEnum.DICTIONARY, displayName = "EXP运算符")
public enum OperatorsEnum implements IEnum<String> {
    IN("=in=", "包含", "包含"),
    NOT_IN("=out=", "不包含", "不包含"),
    EQ("==", "等于", "等于");

    public static final String DICTIONARY = "boot.OperatorsEnum";

    private String value;

    private String displayName;

    private String help;

    OperatorsEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHelp() {
        return help;
    }
}
