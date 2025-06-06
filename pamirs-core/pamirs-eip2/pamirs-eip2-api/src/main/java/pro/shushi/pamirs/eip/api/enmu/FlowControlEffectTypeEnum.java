package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author yeshenyue on 2025/4/21 10:02.
 */
@Dict(dictionary = FlowControlEffectTypeEnum.DICTIONARY, displayName = "流控效果")
public enum FlowControlEffectTypeEnum implements IEnum<String> {

    RAPID_FAILURE("RAPID_FAILURE", "快速失败", "快速失败"),
    QUEUEING_WAIT("QUEUEING_WAIT", "排队等待", "排队等待");

    public static final String DICTIONARY = "eip.FlowControlEffectTypeEnum";

    private final String value;
    private final String displayName;
    private final String help;

    FlowControlEffectTypeEnum(String value, String displayName, String help) {
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
