package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "furniture.enum.MessagePosition")
public enum MessagePosition implements IEnum<String> {

    BEFORE("before", "前面", "前面"),
    AFTER("after", "后面", "后面"),

    ;

    private String help;

    private String value;

    private String displayName;

    MessagePosition(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


    public String getHelp() {
        return help;
    }


    public String getValue() {
        return value;
    }


    public String getDisplayName() {
        return displayName;
    }
}