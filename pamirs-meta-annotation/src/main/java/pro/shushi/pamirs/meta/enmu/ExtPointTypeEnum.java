package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 扩展点类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.ExtPointType", displayName = "扩展点类型")
public enum ExtPointTypeEnum implements IEnum<String> {

    BEFORE("before", "BEFORE", "前置"),
    POINT("point", "POINT", "执行点"),
    AFTER("after", "AFTER", "后置");

    private final String value;
    private final String displayName;
    private final String help;

    ExtPointTypeEnum(String value, String displayName, String help) {
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