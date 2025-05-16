package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "mail.enmu.MessageMasterEnum", displayName = "")
public enum MessageMasterEnum implements IEnum<String> {

    OTHER( "other", "他人消息","他人消息"),
    SELF( "self", "自己消息","自己消息");

    private String help;

    private String value;

    private String displayName;

    MessageMasterEnum(String value, String displayName, String help) {
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
