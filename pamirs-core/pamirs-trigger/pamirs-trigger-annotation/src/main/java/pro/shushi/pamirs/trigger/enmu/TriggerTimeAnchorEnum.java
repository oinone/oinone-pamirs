package pro.shushi.pamirs.trigger.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TimeAnchor;

/**
 * @author Adamancy Zhang
 * @date 2020-11-06 14:39
 */
@Base
@Dict(dictionary = TriggerTimeAnchorEnum.DICTIONARY, displayName = "触发时机", summary = "触发时机")
public enum TriggerTimeAnchorEnum implements IEnum<String> {

    /**
     * 开始时
     */
    START("START", "开始时", "开始时", TimeAnchor.BEFORE),

    /**
     * 完成时
     */
    FINISHED("FINISHED", "完成时", "完成时", TimeAnchor.AFTER);

    public static final String DICTIONARY = "trigger.TriggerTimeAnchorEnum";

    private final String value;
    private final String displayName;
    private final String help;
    private final TimeAnchor timeAnchor;

    TriggerTimeAnchorEnum(String value, String displayName, String help, TimeAnchor timeAnchor) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
        this.timeAnchor = timeAnchor;
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

    public TimeAnchor getTimeAnchor() {
        return timeAnchor;
    }
}
