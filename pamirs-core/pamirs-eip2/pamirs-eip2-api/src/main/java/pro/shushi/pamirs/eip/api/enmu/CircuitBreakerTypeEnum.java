package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author yeshenyue on 2025/4/14 14:38.
 */
@Dict(dictionary = CircuitBreakerTypeEnum.DICTIONARY, displayName = "熔断类型")
public enum CircuitBreakerTypeEnum implements IEnum<String> {

    SLOW_CALL("SLOW_CALL", "慢调用熔断", "根据慢调用的比例进行熔断"),
    EXCEPTION("EXCEPTION", "异常熔断", "根据异常调用的比例进行熔断")
    ;

    public static final String DICTIONARY = "pamirs.eip.CircuitBreakerTypeEnum";

    private final String value;
    private final String displayName;
    private final String help;

    CircuitBreakerTypeEnum(String value, String displayName, String help) {
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
