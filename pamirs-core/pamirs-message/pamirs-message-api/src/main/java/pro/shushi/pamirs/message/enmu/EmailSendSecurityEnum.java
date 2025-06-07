package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "mail.enmu.EmailSendSecurityEnum", displayName = "")
public enum EmailSendSecurityEnum implements IEnum<String> {


    NONE("none", "无", "无"),
    STARTTLS("starttls", "TLS", "TLS (STARTTLS)"),
    SSL("ssl", "SSL/TLS", "SSL/TLS");

    private String help;

    private String value;

    private String displayName;

    EmailSendSecurityEnum(String value, String displayName, String help) {
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
