package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "resource.IconLibTypeEnum", displayName = "图标库类型")
public enum IconLibTypeEnum implements IEnum<String> {

    ICONFONT("ICONFONT", "ICONFONT", "ICONFONT");

    private String value;
    private String displayName;
    private String help;

    IconLibTypeEnum(String value, String displayName, String help) {
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
