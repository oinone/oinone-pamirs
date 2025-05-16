package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = "base.AppConfigSizeEnum", displayName = "尺寸")
public enum AppConfigSizeEnum implements IEnum<String> {

    LARGE("LARGE", "大", "大"),
    MEDIUM("MEDIUM", "中", "中"),
    SMALL("SMALL", "小", "小"),
    ;

    private final String help;

    private final String value;

    private final String displayName;

    AppConfigSizeEnum(String value, String displayName, String help) {
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
