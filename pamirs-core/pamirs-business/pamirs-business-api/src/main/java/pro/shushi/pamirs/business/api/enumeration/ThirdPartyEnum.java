package pro.shushi.pamirs.business.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * ThirdPartyEnum
 *
 * @author yakir on 2022/09/16 18:28.
 */
@Dict(dictionary = ThirdPartyEnum.dict, displayName = "组织类型")
public enum ThirdPartyEnum implements IEnum<String> {

    WECHAT("WECHAT", "微信", "微信"),
    DING_TALK("DING_TALK", "钉钉", "钉钉"),

    ;


    public static final String dict = "business.ThirdPartyEnum";


    private final String value;
    private final String displayName;
    private final String help;

    ThirdPartyEnum(String value, String displayName, String help) {
        this.value       = value;
        this.displayName = displayName;
        this.help        = help;
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
