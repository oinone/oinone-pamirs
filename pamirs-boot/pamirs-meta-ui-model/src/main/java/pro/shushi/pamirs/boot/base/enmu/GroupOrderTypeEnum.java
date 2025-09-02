package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author Gesi at 15:50 on 2025/9/1
 */
@Base
@Dict(dictionary = GroupOrderTypeEnum.DICTIONARY, displayName = "排序类型")
public enum GroupOrderTypeEnum implements IEnum<String> {

    ASC("ASC", "升序", "升序"),
    DESC("DESC", "降序", "降序"),
    ;

    public static final String DICTIONARY = "base.GroupOrderTypeEnum";

    private final String value;
    private final String displayName;
    private final String help;

    GroupOrderTypeEnum(String value, String displayName, String help) {
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
