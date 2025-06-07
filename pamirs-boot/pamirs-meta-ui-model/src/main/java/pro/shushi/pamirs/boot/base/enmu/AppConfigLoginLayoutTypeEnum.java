package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = "base.AppConfigLoginLayoutTypeEnum", displayName = "登录页布局类型")
public enum AppConfigLoginLayoutTypeEnum implements IEnum<String> {

    GLOBAL("global", "全局配置", "全局配置"),
    LEFT_STICK("LEFT_STICK", "大背景居左登录", "大背景居左登录"),
    RIGHT_STICK("RIGHT_STICK", "大背景居右登录", "大背景居右登录"),
    CENTER_STICK("CENTER_STICK", "大背景居中登录", "大背景居中登录"),
    CENTER_STICK_LOGO("CENTER_STICK_LOGO", "大背景居中登录,logo在登录页里面", "大背景居中登录,logo在登录页里面"),
    STAND_LEFT("STAND_LEFT", "左侧登录", "左侧登录"),
    STAND_RIGHT("STAND_RIGHT", "侧登录", "侧登录");

    private final String help;

    private final String value;

    private final String displayName;

    AppConfigLoginLayoutTypeEnum(String value, String displayName, String help) {
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
