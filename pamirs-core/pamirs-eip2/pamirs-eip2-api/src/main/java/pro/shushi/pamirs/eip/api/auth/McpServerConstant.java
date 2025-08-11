package pro.shushi.pamirs.eip.api.auth;

import pro.shushi.pamirs.eip.api.constant.EipConfigurationConstant;

/**
 * mcp api 常量
 *
 * @author Gesi at 17:32 on 2025/8/4
 */
public class McpServerConstant {

    public static final String AUTHORIZATION_KEY = "Authorization";

    public static final String BASIC_AUTH_PREFIX = "Basic ";
    public static final String BASIC_AUTH_SPLIT = ":";

    public static final String BEARER_AUTH_PREFIX = "Bearer ";

    public static final String DEFAULT_CUSTOM_AUTH_KEY = "X-Custom-API-Key";

    public static final String EIP_MCP_SERVER = "mcpServer";

    public static final String EIP_MCP_CATEGORY = "mcp";

    public static final String PAMIRS_EIP_MCP_SERVER_PREFIX = EipConfigurationConstant.PAMIRS_EIP_PREFIX + ".mcp-server";

    public static final String MCP_SERVER_MCP_SERVER_URI = PAMIRS_EIP_MCP_SERVER_PREFIX + ".uri";

    public static final String MCP_SERVER_MCP_SERVER_KEY = PAMIRS_EIP_MCP_SERVER_PREFIX + ".mcpServerInfo";

    public static final String MCP_SERVER_EIP_AUTHENTICATION_KEY = PAMIRS_EIP_MCP_SERVER_PREFIX + ".eipAuthentication";

    public static final String MCP_SERVER_MCP_SERVER_ID = PAMIRS_EIP_MCP_SERVER_PREFIX + ".id";

    public static final String MCP_SERVER_MCP_SERVER_METHOD = PAMIRS_EIP_MCP_SERVER_PREFIX + ".method";

    public static final String MCP_PROCESSOR_PREFIX = "MCP_PROCESSOR_";

    public static final String MCP_SERVER_CONVERTER_FUN = MCP_PROCESSOR_PREFIX + "mcpServerConverter";

    public static final String MCP_AUTHENTICATION_PROCESSOR_FUN = MCP_PROCESSOR_PREFIX + "mcpServerAuthenticationProcessor";
}
