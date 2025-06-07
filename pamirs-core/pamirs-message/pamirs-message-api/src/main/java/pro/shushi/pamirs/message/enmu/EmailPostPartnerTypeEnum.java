package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "mail.enmu.EmailPostPartnerTypeEnum", displayName = "")
public enum EmailPostPartnerTypeEnum implements IEnum<String> {

    PARTNER("partner", "指定人员", "指定人员"),
    ADDRESS("address", "固定邮箱地址", "固定邮箱地址"),
    FIELD("field", "模型字段", "模型字段"),
    ;

    private String help;

    private String value;

    private String displayName;

    EmailPostPartnerTypeEnum(String value, String displayName, String help) {
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
