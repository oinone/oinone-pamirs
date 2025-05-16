package pro.shushi.pamirs.channel.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * IncrementEnum
 *
 * @author yakir on 2020/04/27 17:25.
 */
@Dict(dictionary = "channel.IncrementEnum", displayName = "增强模型增量同步控制")
public enum IncrementEnum implements IEnum<String> {

    OPEN("OPEN", "已开启", "开启增量同步"),
    CLOSE("CLOSE", "已关闭", "关闭增量同步"),

    ;

    private String value;
    private String displayName;
    private String help;

    IncrementEnum(String value, String displayName, String help) {
        this.value       = value;
        this.displayName = displayName;
        this.help        = help;
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
