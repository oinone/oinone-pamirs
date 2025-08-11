package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * MCP服务器功能类型
 * @link <a href="https://modelcontextprotocol.io/specification/2025-03-26/server">MCP服务器功能类型</a>
 *
 * @author Gesi at 10:16 on 2025/8/7
 */
@Base
@Dict(dictionary = McpServerFeatureType.dictionary, displayName = "MCP服务器功能类型")
public enum McpServerFeatureType implements IEnum<String> {

    PROMPT("Prompt", "提示", "Interactive templates invoked by user choice"),
    RESOURCE("Resource", "资源", "Contextual data attached and managed by the client"),
    TOOL("Tool", "工具", "Functions exposed to the LLM to take actions"),
    ;

    public static final String dictionary = "pamirs.eip.McpServerFeatureType";

    private final String value;

    private final String displayName;

    private final String help;

    McpServerFeatureType(String value, String displayName, String help) {
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
