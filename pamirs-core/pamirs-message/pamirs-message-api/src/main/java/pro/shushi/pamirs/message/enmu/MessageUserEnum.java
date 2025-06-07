package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "mail.enmu.MessageUserEnum", displayName = "")
public enum MessageUserEnum implements IEnum<String> {

    DISTRIBUTOR("updater", "经销商", "经销商"),
    UPDATER("updater", "更新人", "更新人"),
    CREATER("creater", "创建人", "创建人"),

    ;

    private String help;

    private String value;

    private String displayName;

    MessageUserEnum(String value, String displayName, String help) {
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