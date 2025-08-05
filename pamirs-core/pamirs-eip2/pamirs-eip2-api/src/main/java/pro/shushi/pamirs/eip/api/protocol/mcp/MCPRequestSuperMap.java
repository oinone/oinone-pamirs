package pro.shushi.pamirs.eip.api.protocol.mcp;

import pro.shushi.pamirs.core.common.SuperMap;

/**
 * MCP协议请求体对象
 *
 * @author Gesi at 15:24 on 2025/8/4
 */
public class MCPRequestSuperMap extends MCPRootSuperMap {

    // MCP 协议请求key
    public static final String METHOD_KEY = "method";
    public static final String PARAMS_KEY = "params";

    public MCPRequestSuperMap() {
        this(new SuperMap());
    }

    public MCPRequestSuperMap(SuperMap map) {
        super(map);
    }

    public void setMethod(String method) {
        put(METHOD_KEY, method);
    }

    public String getMethod() {
        return (String) get(METHOD_KEY);
    }

}
