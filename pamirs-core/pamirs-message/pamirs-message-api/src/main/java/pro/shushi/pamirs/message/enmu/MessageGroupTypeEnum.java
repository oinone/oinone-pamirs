package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "mail.enmu.MessageGroupTypeEnum", displayName = "")
public enum MessageGroupTypeEnum implements IEnum<String> {

    CHAT("chat", "聊天", "聊天"),
    CHANNEL("channel", "聊天频道组", "聊天频道组"),
    MODEL_MAIL("MODEL_MAIL", "模型消息", "模型消息"),
    SYSTEM_MAIL("SYSTEM_MAIL", "系统通知", "系统通知"),
    SYSTEM_MAIL_BROADCAST("SYSTEM_MAIL_BROADCAST", "广播消息", "广播消息");

    private String help;

    private String value;

    private String displayName;

    MessageGroupTypeEnum(String value, String displayName, String help) {
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
