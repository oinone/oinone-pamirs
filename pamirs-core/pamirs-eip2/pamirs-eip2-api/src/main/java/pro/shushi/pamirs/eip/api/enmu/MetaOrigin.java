package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * ApiSchema
 *
 * @author yakir on 2023/03/30 14:13.
 */
@Base
@Dict(dictionary = MetaOrigin.dictionary, displayName = "连接器/Api来源", summary = "连接器/Api来源")
public enum MetaOrigin implements IEnum<String> {

    Oinone("Oinone", "Oinone 官方", "Oinone 官方，不可删除"),
    Custom("Custom", "自定义", "自定义"),

    ;

    public static final String dictionary = "eip.MetaOrigin";

    private final String value;
    private final String displayName;
    private final String help;

    MetaOrigin(String value, String displayName, String help) {
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
