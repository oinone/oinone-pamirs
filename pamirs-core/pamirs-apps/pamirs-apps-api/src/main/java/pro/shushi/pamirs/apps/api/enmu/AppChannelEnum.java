package pro.shushi.pamirs.apps.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = AppChannelEnum.dictionary, displayName = "App产品")
public enum AppChannelEnum implements IEnum<String> {

    OMS("oms", "全渠道运营", "全渠道运营"),
    B2C("dms", "在线商城", "在线商城"),
    DMS("b2c", "分销协同", "分销协同"),
    GEMINI("gemini_core", "全员营销", "全员营销"),
    ;

    public static final String dictionary = "app.AppChannelEnum";

    private String value;
    private String displayName;
    private String help;

    AppChannelEnum(String value, String displayName, String help) {
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

    public static AppChannelEnum fetchByValue(String value) {
        for (AppChannelEnum welcomeChannelEnum : AppChannelEnum.values()) {
            if (welcomeChannelEnum.getValue().equals(value)) {
                return welcomeChannelEnum;
            }
        }
        return null;
    }

}
