package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 信息等级
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.MessageLevel", displayName = "信息等级")
public enum InformationLevelEnum implements IEnum<String> {

    DEBUG("DEBUG", "调试", "调试"),
    INFO("INFO", "信息", "信息"),
    SUCCESS("SUCCESS", "成功", "成功"),
    WARN("WARN", "警告", "警告"),
    ERROR("ERROR", "错误", "错误");

    private final String value;
    private final String displayName;
    private final String help;

    InformationLevelEnum(String value, String displayName, String help) {
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