package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = GenderEnum.dictionary, displayName = "性别")
public enum GenderEnum implements IEnum<String> {

    NULL("NULL", "未知", "未知"),
    MALE("MALE", "男", "男"),
    FEMALE("FEMALE", "女", "女");

    public static final String dictionary = "resource.GenderEnum";

    private String value;
    private String displayName;
    private String help;

    GenderEnum(String value, String displayName, String help) {
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
