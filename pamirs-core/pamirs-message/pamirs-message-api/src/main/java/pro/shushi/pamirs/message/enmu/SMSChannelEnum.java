package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * SMSChannel 短信渠道
 *
 * @author yakir on 2019/08/23 11:34.
 */
@Dict(dictionary = "mail.enmu.SMSChannelEnum", displayName = "")
public enum SMSChannelEnum implements IEnum<String> {

    ALIYUN("ALIYUN", "阿里云", "阿里云"),
    ALIYUN_NOTIFY("ALIYUN_NOTIFY", "阿里云通知类", "阿里云通知类"),
    CUSTOM("CUSTOM", "自定义", "自定义"),

    ;

    private String help;
    private String value;
    private String displayName;

    SMSChannelEnum(String value, String displayName, String help) {
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
