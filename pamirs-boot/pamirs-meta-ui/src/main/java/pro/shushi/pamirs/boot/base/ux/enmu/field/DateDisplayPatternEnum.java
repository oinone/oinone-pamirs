package pro.shushi.pamirs.boot.base.ux.enmu.field;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 日期显示格式
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.DateDisplayPattern", displayName = "日期显示格式")
public enum DateDisplayPatternEnum implements IEnum<String> {

    TEXT("text", "yyyy年MM月dd", "yyyy年MM月dd"),
    HYPHEN("hyphen", "yyyy-MM-dd", "yyyy-MM-dd"),
    SLASH("end", "yyyy/MM/dd", "yyyy/MM/dd");

    private final String displayName;

    private final String value;

    private final String help;

    DateDisplayPatternEnum(String value, String displayName, String help) {
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
