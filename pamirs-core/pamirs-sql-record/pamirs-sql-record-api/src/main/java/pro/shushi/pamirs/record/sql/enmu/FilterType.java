package pro.shushi.pamirs.record.sql.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * FilterType
 *
 * @author yakir on 2023/06/28 18:18.
 */
@Base
@Dict(dictionary = FilterType.dictionary, displayName = "过滤类型")
public enum FilterType implements IEnum<String> {

    CHANGE_DATA("CHANGE_DATA", "ChangeData", "ChangeData"),
    BINLOG_EVENT("BINLOG_EVENT", "BinlogEvent", "BinlogEvent"),
    ALL("ALL", "ALL", "ALL"),

    ;

    public static final String dictionary = "record.FilterType";

    private final String value;
    private final String displayName;
    private final String help;

    FilterType(String value, String displayName, String help) {
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
