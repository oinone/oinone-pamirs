package pro.shushi.pamirs.eip.api.enmu.alarm;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * AlarmNotifyStatus
 *
 * @author yakir on 2026/04/03 16:00.
 */
@Dict(dictionary = AlarmNotifyStatus.dictionary, displayName = "告警通知状态")
public enum AlarmNotifyStatus implements IEnum<String> {

    SENT("SENT", "已发送", "已发送"),
    SEND_FAILED("SEND_FAILED", "发送失败", "发送失败"),
    NOT_NEED_SENT("NOT_NEED_SENT", "无需发送", "无需发送"),
    NOT_SENT("NOT_SENT", "未发送", "未发送"),
    ;

    public static final String dictionary = "eip.AlarmNotifyStatus";

    private final String value;
    private final String displayName;
    private final String help;

    AlarmNotifyStatus(String value, String displayName, String help) {
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