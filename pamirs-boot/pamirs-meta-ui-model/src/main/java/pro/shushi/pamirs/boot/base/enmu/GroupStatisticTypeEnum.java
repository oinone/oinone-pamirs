package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author Gesi at 15:50 on 2025/9/1
 */
@Base
@Dict(dictionary = GroupStatisticTypeEnum.DICTIONARY, displayName = "统计类型")
public enum GroupStatisticTypeEnum implements IEnum<String> {

    ;

    public static final String DICTIONARY = "base.GroupStatisticTypeEnum";

    private final String value;
    private final String displayName;
    private final String help;

    GroupStatisticTypeEnum(String value, String displayName, String help) {
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
