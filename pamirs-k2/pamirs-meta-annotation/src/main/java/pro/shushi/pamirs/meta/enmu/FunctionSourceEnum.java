package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 函数来源枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.FunctionSource", displayName = "函数来源")
public enum FunctionSourceEnum implements IEnum<String> {

    FUNCTION("FUNCTION", "函数", "函数"),
    ACTION("ACTION", "服务器动作", "服务器动作"),
    EXTPOINT("EXTPOINT", "扩展点", "扩展点"),
    HOOK("HOOK", "平台扩展机制", "平台扩展机制");

    private final String value;

    private final String displayName;

    private final String help;

    FunctionSourceEnum(String value, String displayName, String help) {
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
