package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BitEnum;

/**
 * 函数开放级别枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.FunctionOpen", displayName = "函数开放级别")
public enum FunctionOpenEnum implements BitEnum {

    LOCAL(2, "本地调用", "本地调用"),
    REMOTE(2 << 1, "远程调用", "远程调用"),
    API(2 << 2, "开放接口", "开放接口");

    private final Long value;
    private final String displayName;
    private final String help;

    FunctionOpenEnum(long value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public Long value() {
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