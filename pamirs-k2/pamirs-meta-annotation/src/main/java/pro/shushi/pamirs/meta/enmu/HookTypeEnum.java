package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = "base.HookType", displayName = "拦截器类型")
public enum HookTypeEnum implements IEnum<String> {

    BEFORE("before", "BEFORE", "前置"),
    AFTER("after", "AFTER", "后置");

    private final String value;
    private final String displayName;
    private final String help;

    HookTypeEnum(String value, String displayName, String help) {
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