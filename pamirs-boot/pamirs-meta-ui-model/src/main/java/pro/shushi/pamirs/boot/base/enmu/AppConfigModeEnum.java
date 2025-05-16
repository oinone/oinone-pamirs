package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = "base.AppConfigModeEnum", displayName = "主题模式")
public enum AppConfigModeEnum implements IEnum<String> {

    DEFAULT("DEFAULT", "浅色模式", "浅色模式"),
    DARK("DARK", "深色模式", "深色模式"),
    ;

    private final String help;

    private final String value;

    private final String displayName;

    AppConfigModeEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String help() {
        return help;
    }

}
