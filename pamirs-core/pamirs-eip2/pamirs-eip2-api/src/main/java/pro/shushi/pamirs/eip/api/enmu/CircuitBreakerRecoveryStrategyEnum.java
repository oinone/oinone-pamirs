package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author yeshenyue on 2025/4/14 14:41.
 */
@Dict(dictionary = CircuitBreakerRecoveryStrategyEnum.DICTIONARY, displayName = "熔断恢复策略")
public enum CircuitBreakerRecoveryStrategyEnum implements IEnum<String> {

    SINGLE_PROBE("SINGLE_PROBE", "单次探测恢复", "通过单次探测请求判断是否恢复");

    public static final String DICTIONARY = "pamirs.eip.CircuitBreakerRecoveryStrategyEnum";

    private final String value;
    private final String displayName;
    private final String help;

    CircuitBreakerRecoveryStrategyEnum(String value, String displayName, String help) {
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
