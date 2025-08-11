package pro.shushi.pamirs.eip.api.enmu.connector;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = McpCertType.dictionary, displayName = "Mcp认证类型", summary = "Mcp认证类型")
public enum McpCertType implements IEnum<String> {

    BASIC("BASIC", "Basic Auth", "Basic Auth"),
    BEARER("BEARER", "Bearer Auth", "Bearer Auth"),
    CUSTOM("CUSTOM", "自定义认证", "自定义认证"),
    ;

    public static final String dictionary = "designer.McpCertType";

    private final String value;
    private final String displayName;
    private final String help;

    McpCertType(String value, String displayName, String help) {
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
