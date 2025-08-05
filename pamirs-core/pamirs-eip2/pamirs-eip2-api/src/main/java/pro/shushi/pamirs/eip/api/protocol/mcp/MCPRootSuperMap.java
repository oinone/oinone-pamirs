package pro.shushi.pamirs.eip.api.protocol.mcp;

import pro.shushi.pamirs.core.common.SuperMap;

import java.util.Objects;

/**
 * MCP 协议根对象
 *
 * @author Gesi at 9:30 on 2025/8/5
 */
public abstract class MCPRootSuperMap extends MCPSuperMap {

    // MCP 协议通用key
    public static final String JSONRPC_KEY = "jsonrpc";
    public static final String ID_KEY = "id";

    public MCPRootSuperMap() {
        this(new SuperMap());
    }

    public MCPRootSuperMap(SuperMap map) {
        super(map);
    }

    // MCP 协议基础内容

    /**
     * mcp 基于jsonrpc2.0，这个值应该固定是2.0
     * @return
     */
    public String getJsonrpc() {
        return (String) get(JSONRPC_KEY);
    }

    public void setId(Object id) {
        put(ID_KEY, id);
    }

    /**
     * @return id 允许Number或String，但不能为空，请求和响应的id必须一致
     */
    public Object getId() {
        return get(ID_KEY);
    }

}
