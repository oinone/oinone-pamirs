package pro.shushi.pamirs.channel.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * DumpStateEnum
 *
 * @author yakir on 2020/04/27 17:25.
 */
@Dict(dictionary = "channel.DumpStateEnum", displayName = "Dump状态")
public enum DumpStateEnum implements IEnum<String> {

    INIT("INIT", "INIT", "初始化"),
    SUCCESS("SUCCESS", "SUCCESS", "SUCCESS"),
    PENDING("PENDING", "PENDING", "PENDING"),
    ERROR("ERROR", "ERROR", "ERROR"),

    ;

    private String value;
    private String displayName;
    private String help;

    DumpStateEnum(String value, String displayName, String help) {
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
