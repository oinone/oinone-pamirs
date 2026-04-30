package pro.shushi.pamirs.eip.api.enmu.alarm;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * AlarmNotifyType
 *
 * @author yakir on 2026/04/03 15:54.
 */
@Dict(dictionary = AlarmNotifyType.dictionary, displayName = "告警通知方式")
public enum AlarmNotifyType implements IEnum<String> {

    EMAIL("EMAIL", "邮件", "邮件"),
    WEBHOOK("WEBHOOK", "Webhook", "Webhook"),
    ;

    public static final String dictionary = "eip.AlarmNotifyType";

    private final String value;
    private final String displayName;
    private final String help;

    AlarmNotifyType(String value, String displayName, String help) {
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