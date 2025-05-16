package pro.shushi.pamirs.apps.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ModuleTypeEnum.dictionary, displayName = "模块类型")
public enum ModuleTypeEnum implements IEnum<String> {

    APPLICATION("APPLICATION", "应用", "应用"),
    MODULE("MODULE", "模块", "模块"),
    ;

    public static final String dictionary = "app.ModuleTypeEnum";

    private String value;
    private String displayName;
    private String help;

    ModuleTypeEnum(String value, String displayName, String help) {
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
