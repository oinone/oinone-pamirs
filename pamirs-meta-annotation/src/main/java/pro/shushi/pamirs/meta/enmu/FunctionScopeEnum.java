package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = "base.FunctionScope", displayName = "函数域")
public enum FunctionScopeEnum implements IEnum<String> {

    BOTH("BOTH", "通用函数", "通用函数"),
    CLIENT("CLIENT", "客户端函数", "客户端函数"),
    SERVER("SERVER", "服务器函数", "服务器函数");

    private final String value;
    private final String displayName;
    private final String help;

    FunctionScopeEnum(String value, String displayName, String help) {
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