package pro.shushi.pamirs.business.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * TeamAuthEnum
 *
 * @author yakir on 2022/09/15 13:48.
 */
@Dict(dictionary = TeamAuthEnum.dict, displayName = "认证状态")
public enum TeamAuthEnum implements IEnum<String> {

    AUTHED("AUTHED", "已认证", "已认证"),
    UNAUTH("UNAUTH", "未认证", "已认证"),

    ;

    public static final String dict = "business.TeamAuthEnum";


    private final String value;
    private final String displayName;
    private final String help;

    TeamAuthEnum(String value, String displayName, String help) {
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
