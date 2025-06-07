package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author haibo(xf.z @ shushi.pro)
 * @date 2021/8/9 下午8:33
 */
@Dict(dictionary = "mail.enmu.SMSTemplateStatusEnum", displayName = "短信模板审核状态")
public enum SMSTemplateStatusEnum implements IEnum<String> {

    PENDING_AUDIT("PENDING_AUDIT", "待审核", "待审核"),
    AUDITING("AUDITING", "审核中", "审核中"),
    SUCCESS("SUCCESS", "审核通过", "审核通过"),
    FAILURE("FAILURE", "审核不通过", "审核不通过"),

    ;

    private String help;
    private String value;
    private String displayName;

    SMSTemplateStatusEnum(String value, String displayName, String help) {
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
