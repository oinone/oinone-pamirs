package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = "base.AppConfigScope", displayName = "应用配置域")
public enum AppConfigScopeEnum implements IEnum<String> {

    GLOBAL("global", "全局配置", "全局配置"),
    APP("app", "应用配置", "应用配置"),
    PRODUCT("product", "产品通用配置", "产品通用配置"),
    PRODUCT_APP("product_app", "产品应用配置", "产品应用配置"),
    ;

    private final String help;

    private final String value;

    private final String displayName;

    AppConfigScopeEnum(String value, String displayName, String help) {
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
