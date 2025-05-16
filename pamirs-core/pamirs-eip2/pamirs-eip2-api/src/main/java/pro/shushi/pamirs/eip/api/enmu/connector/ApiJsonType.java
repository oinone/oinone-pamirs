package pro.shushi.pamirs.eip.api.enmu.connector;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author yeshenyue on 2024/4/17 18:38.
 */
@Base
@Dict(dictionary = ApiJsonType.dictionary, displayName = "JSON格式入参类型", summary = "JSON格式顶层入参类型")
public enum ApiJsonType implements IEnum<String> {

    OBJECT("OBJECT", "OBJECT", "顶层json是对象类型"),
    LIST("LIST", "LIST", "顶层json是数组类型");

    public static final String dictionary = "designer.ApiJsonType";

    private final String value;
    private final String displayName;
    private final String help;

    ApiJsonType(String value, String displayName, String help) {
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

