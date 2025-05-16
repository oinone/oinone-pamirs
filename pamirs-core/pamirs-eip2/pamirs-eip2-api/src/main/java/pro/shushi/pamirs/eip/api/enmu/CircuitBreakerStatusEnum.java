package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author yeshenyue on 2025/4/15 10:19.
 */
@Dict(dictionary = CircuitBreakerStatusEnum.DICTIONARY, displayName = "熔断状态")
public enum CircuitBreakerStatusEnum implements IEnum<String> {

    OPEN("OPEN", "熔断", "熔断"),
    CLOSED("CLOSED", "正常", "正常"),
    HALF_OPEN("HALF_OPEN", "尝试恢复", "尝试恢复"),
    ;

    public static final String DICTIONARY = "..CircuitBreakerStatusEnum";

    private final String value;
    private final String displayName;
    private final String help;

    CircuitBreakerStatusEnum(String value, String displayName, String help) {
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
