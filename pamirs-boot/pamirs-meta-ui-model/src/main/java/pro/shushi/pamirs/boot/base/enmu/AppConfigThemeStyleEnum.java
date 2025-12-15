package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = "base.AppConfigThemeStyleEnum", displayName = "主题风格")
public enum AppConfigThemeStyleEnum implements IEnum<String> {

    MINIMALISM("MINIMALISM", "极简", "极简"),
    CLASSIC("CLASSIC", "经典", "经典"),
    ;

    private final String help;

    private final String value;

    private final String displayName;

    AppConfigThemeStyleEnum(String value, String displayName, String help) {
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
