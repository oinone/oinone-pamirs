package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 校验策略枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.CheckStrategy", displayName = "校验策略")
public enum CheckStrategyEnum implements IEnum<String> {

    RETURN_WHEN_COMPLETED("RETURN_WHEN_COMPLETED", "全部校验完成再返回结果", "全部校验完成再返回结果"),
    RETURN_WHEN_ERROR("RETURN_WHEN_ERROR", "校验错误即返回结果", "校验错误即返回结果");

    private final String value;
    private final String displayName;
    private final String help;

    CheckStrategyEnum(String value, String displayName, String help) {
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