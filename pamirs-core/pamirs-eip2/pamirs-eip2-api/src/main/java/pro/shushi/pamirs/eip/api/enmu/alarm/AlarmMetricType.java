package pro.shushi.pamirs.eip.api.enmu.alarm;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * AlarmMetricType
 *
 * @author yakir on 2026/04/03 15:52.
 */
@Dict(dictionary = AlarmMetricType.dictionary, displayName = "指标类型")
public enum AlarmMetricType implements IEnum<String> {

    FAILURE_COUNT("FAILURE_COUNT", "失败次数", "失败次数"),
    FAILURE_RATE("FAILURE_RATE", "失败率", "失败率"),
    ;

    public static final String dictionary = "eip.AlarmMetricType";

    private final String value;
    private final String displayName;
    private final String help;

    AlarmMetricType(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    public String value() {
        return this.value;
    }

    public String displayName() {
        return this.displayName;
    }

    public String help() {
        return this.help;
    }
}