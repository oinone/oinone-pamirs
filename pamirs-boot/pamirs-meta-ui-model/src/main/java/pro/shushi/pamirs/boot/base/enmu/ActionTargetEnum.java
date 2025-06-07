package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = "base.ActionTarget", displayName = "页面打开方式")
public enum ActionTargetEnum implements IEnum<String> {

    ROUTER("router", "页面路由", "页面路由"),
    DIALOG("dialog", "页面弹窗", "页面弹窗"),
    DRAWER("drawer", "打开抽屉", "打开抽屉"),
    INNER("inner", "页内路由", "页内路由"),
    OPEN_WINDOW("openWindow", "打开新窗口", "打开新窗口"),
    ;

    private final String displayName;

    private final String value;

    private final String help;

    ActionTargetEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
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
