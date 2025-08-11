package pro.shushi.pamirs.eip.api.enmu.connector;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = McpAuthType.dictionary, displayName = "Mcp鉴权类型", summary = "Mcp鉴权类型")
public enum McpAuthType implements IEnum<String> {

    NO_AUTH("NO_AUTH", "无需认证", "无需认证"),
    API_KEY("API_KEY", "API Key", "API Key"),
    ;

    public static final String dictionary = "designer.McpAuthType";

    private final String value;
    private final String displayName;
    private final String help;

    McpAuthType(String value, String displayName, String help) {
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
