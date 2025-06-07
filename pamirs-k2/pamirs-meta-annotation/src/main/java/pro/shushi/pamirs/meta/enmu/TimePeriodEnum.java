package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 时间周期
 *
 * @author Adamancy Zhang at 17:38 on 2021-08-11
 */
@Base
@Dict(dictionary = TimePeriodEnum.DICTIONARY, displayName = "时间周期", summary = "时间周期")
public enum TimePeriodEnum implements IEnum<String> {

    YEAR("YEAR", "以年为周期", "以年为周期"),
    MONTH("MONTH", "以月为周期", "以月为周期"),
    DAY("DAY", "以天为周期", "以天为周期"),
    ;

    public static final String DICTIONARY = "base.TimePeriodEnum";

    private final String value;
    private final String displayName;
    private final String help;

    TimePeriodEnum(String value, String displayName, String help) {
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
