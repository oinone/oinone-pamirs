package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "mail.enmu.MessageChannelTypeEnum", displayName = "")
public enum MessageChannelTypeEnum implements IEnum<String> {

    CHAT( "chat", "聊天","聊天"),
    CHANNEL( "channel", "聊天频道组","聊天频道组"),
    SYSTEM_MAIL( "systemMail", "系统通知频道","系统通知频道"),
    SYSTEM_MAIL_BROADCAST( "systemMailBroadcast", "广播消息频道","广播消息频道");

    private String help;

    private String value;

    private String displayName;

    MessageChannelTypeEnum(String value, String displayName, String help) {
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
