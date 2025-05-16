package pro.shushi.pamirs.business.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * TeamType
 *
 * @author yakir on 2022/09/13 17:34.
 */
@Dict(dictionary = TeamType.dict, displayName = "组织类型")
public enum TeamType implements IEnum<String> {

    ENTERPRISE("ENTERPRISE", "企业", "企业"),
    GOVERNMENT("GOVERNMENT", "政府", "政府"),
    OTHERS("OTHERS", "其他", "其他"),

    ;

    public static final String dict = "business.TeamType";

    private final String value;
    private final String displayName;
    private final String help;

    TeamType(String value, String displayName, String help) {
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
