package pro.shushi.pamirs.eip.api.enmu.connector;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * ConnDBType
 *
 * @author yakir on 2023/03/29 16:07.
 */
@Base
@Dict(dictionary = ConnType.dictionary, displayName = "连接器类型", summary = "连接器类型")
public enum ConnType implements IEnum<String> {

    APP("APP", "应用", "应用"),
    DB("DB", "数据库", "数据库"),
    EXCEL("EXCEL", "文件集", "文件集"),

    ;

    public static final String dictionary = "designer.ConnType";

    private final String value;
    private final String displayName;
    private final String help;

    ConnType(String value, String displayName, String help) {
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
