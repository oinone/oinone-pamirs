package pro.shushi.pamirs.trigger.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author Adamancy Zhang
 * @date 2020-11-06 14:38
 */
@Base
@Dict(dictionary = NotifyTypeEnum.DICTIONARY, displayName = "通知类型", summary = "通知类型")
public enum NotifyTypeEnum implements IEnum<String> {

    /**
     * RocketMQ support
     */
    ROCKET_MQ("ROCKET_MQ", "RocketMQ", "RocketMQ");

    public static final String DICTIONARY = "trigger.NotifyTypeEnum";

    private final String value;
    private final String displayName;
    private final String help;

    NotifyTypeEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHelp() {
        return help;
    }
}
