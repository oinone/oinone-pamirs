package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "mail.enmu.DefaultAvatarEnum", displayName = "")
public enum DefaultAvatarEnum implements IEnum<String> {

    CHATURL("chatUrl", "chat头像", "chat头像"),
    CHANNELURL("channelUrl", "channel头像", "channel头像"),
    MODELMAILURL("modelMailUrl", "模型消息头像", "模型消息头像"),
    SYSTEMMAILURL("systemMailUrl", "系统消息头像", "系统消息头像");

    private String help;

    private String value;

    private String displayName;

    DefaultAvatarEnum(String value, String displayName, String help) {
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
