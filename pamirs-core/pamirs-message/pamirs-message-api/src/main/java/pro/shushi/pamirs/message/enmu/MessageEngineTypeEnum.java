package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "mail.enmu.MessageEngineTypeEnum", displayName = "")
public enum MessageEngineTypeEnum implements IEnum<String> {

    EMAIL_SEND("EMAIL_SEND", "发送邮件", "发送邮件"),
    SMS_SEND("SMS_SEND", "发送短信", "发送短信"),
    APP_PUSH("APP_PUSH", "app推送", "app推送"),
    WECHAT_PUSH("WHCHAT_PUSH", "微信推送", "微信推送"),
    THIRD_PUSH("THIRD_PUSH", "第三方应用推送", "第三方应用推送"),
    MAIL_SEND("MAIL_SEND", "mail_send", "站内信发送"),
    ;
    private String help;

    private String value;

    private String displayName;

    MessageEngineTypeEnum(String value, String displayName, String help) {
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
