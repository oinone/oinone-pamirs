package pro.shushi.pamirs.boot.base.ux.enmu.field;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 时间显示格式
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.TimeDisplayPattern", displayName = "时间显示格式")
public enum TimeDisplayPatternEnum implements IEnum<String> {

    SECOND("HH:mm:ss", "HH:mm:ss", "HH:mm:ss"),
    MINUTE("HH:mm", "HH:mm", "HH:mm"),
    TEXT_SECOND("A HH:mm:ss", "A HH:mm:ss", "A HH:mm:ss"),
    TEXT_MINUTE("A HH:mm", "A HH:mm", "A HH:mm");

    private final String displayName;

    private final String value;

    private final String help;

    TimeDisplayPatternEnum(String value, String displayName, String help) {
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
