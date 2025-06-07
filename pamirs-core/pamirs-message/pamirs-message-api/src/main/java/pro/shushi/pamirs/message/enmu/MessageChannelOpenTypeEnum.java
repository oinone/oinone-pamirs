package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "mail.enmu.MessageChannelOpenTypeEnum", displayName = "")
public enum MessageChannelOpenTypeEnum implements IEnum<String> {

    PUBLIC("public", "所有人", "所有人"),
    PRIVATE("private", "私人频道", "私人频道"),
    GROUPS("groups", "群组频道", "群组频道");

    private String help;

    private String value;

    private String displayName;

    MessageChannelOpenTypeEnum(String value, String displayName, String help) {
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
