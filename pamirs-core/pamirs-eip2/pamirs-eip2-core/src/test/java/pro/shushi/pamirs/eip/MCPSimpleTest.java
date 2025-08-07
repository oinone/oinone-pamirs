package pro.shushi.pamirs.eip;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import pro.shushi.pamirs.eip.api.protocol.mcp.McpRequestSuperMap;
import pro.shushi.pamirs.eip.api.protocol.mcp.McpSchema;
import pro.shushi.pamirs.eip.api.serializable.DefaultJSONSerializable;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * @author Gesi at 15:47 on 2025/8/6
 */
@Slf4j
public class MCPSimpleTest {

    /**
     * 一个简单的包含 ls 和 cat 的mcp tool服务器入口，直接做springmvc的一个postmapping方法
     * @param body 请求体
     * @return 响应体
     */
    public String controller(String body) {
        log.info("request:\n{}", body);

        Object id = null;
        try {
            McpRequestSuperMap mcpSuperMap = new McpRequestSuperMap(BeanDefinitionUtils.getBean(DefaultJSONSerializable.class).serializable(body));
            String method = mcpSuperMap.getMethod();
            id = mcpSuperMap.getId();

            String result = "";

            if ("initialize".equals(method)) {
                result = initialize(mcpSuperMap);
            } else if ("tools/list".equals(method)) {
                result = listTools(mcpSuperMap);
            } else if ("tools/call".equals(method)) {
                result = callTool(mcpSuperMap);
            } else {
                McpSchema.JSONRPCResponse<Object> response = new McpSchema.JSONRPCResponse<>();
                result = new ObjectMapper().writeValueAsString(response);
            }

            log.info("response:\n {}", result);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            McpSchema.JSONRPCResponse<McpSchema.InitializeResult> response = new McpSchema.JSONRPCResponse<>();
            response.setId(id);
            response.setJsonrpc(McpSchema.JSONRPC_VERSION);
            response.setError(
                    new McpSchema.JSONRPCResponse.JSONRPCError()
                            .setCode(McpSchema.ErrorCodes.INTERNAL_ERROR)
                            .setMessage(e.getMessage())
            );
            try {
                return new ObjectMapper().writeValueAsString(response);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private String initialize(McpRequestSuperMap mcpSuperMap) throws JsonProcessingException {
        McpSchema.JSONRPCRequest<McpSchema.InitializeRequest> mcpSchemaObject = mcpSuperMap.getMCPSchemaObject(new TypeReference<McpSchema.JSONRPCRequest<McpSchema.InitializeRequest>>() {
        });

        Object id = mcpSuperMap.getId();

        McpSchema.JSONRPCResponse<McpSchema.InitializeResult> response = new McpSchema.JSONRPCResponse<>();
        response.setId(id);
        response.setJsonrpc(McpSchema.JSONRPC_VERSION);
        McpSchema.InitializeResult initializeResult = new McpSchema.InitializeResult();
        response.setResult(initializeResult);

        initializeResult.setProtocolVersion(McpSchema.LATEST_PROTOCOL_VERSION);
        McpSchema.Implementation implementation = new McpSchema.Implementation();
        initializeResult.setServerInfo(implementation);
        implementation.setName("MyMcpServer");
        implementation.setVersion("1.0.0");

        McpSchema.ServerCapabilities serverCapabilities = new McpSchema.ServerCapabilities();
        initializeResult.setCapabilities(serverCapabilities);
        // 当前只支持了tool的功能
//        serverCapabilities.setResources(new McpSchema.ServerCapabilities.ResourceCapabilities().setListChanged(false).setSubscribe(false));
//        serverCapabilities.setPrompts(new McpSchema.ServerCapabilities.PromptCapabilities().setListChanged(false));
        serverCapabilities.setTools(new McpSchema.ServerCapabilities.ToolCapabilities().setListChanged(false));
        serverCapabilities.setLogging(new McpSchema.ServerCapabilities.LoggingCapabilities());

        response.setResult(initializeResult);

        return new ObjectMapper().writeValueAsString(response);
    }

    private String listTools(McpRequestSuperMap mcpSuperMap) throws JsonProcessingException {
        Object id = mcpSuperMap.getId();

        McpSchema.JSONRPCResponse<McpSchema.ListToolsResult> response = new McpSchema.JSONRPCResponse<>();
        response.setId(id);
        response.setJsonrpc(McpSchema.JSONRPC_VERSION);

        McpSchema.ListToolsResult result = new McpSchema.ListToolsResult();
        result.setTools(new ArrayList<>());
        result.getTools().add(listToolLs());
        result.getTools().add(listToolCat());

        response.setResult(result);

        return new ObjectMapper().writeValueAsString(response);
    }

    private McpSchema.Tool listToolLs() {
        Map<String, Object> pathMap = new HashMap<>();
        pathMap.put("type", "string");
        pathMap.put("description", "需要查看文件列表的指定文件目录");
        return new McpSchema.Tool()
                .setName("ls")
                .setDescription("获取文件列表，类似linux下的ls命令，只能查询指定目录下的所有一级文件和目录")
                .setInputSchema(
                        new McpSchema.JsonSchema()
                                .setType("object")
                                .setProperties(Collections.singletonMap("path", pathMap))
                                .setRequired(Lists.newArrayList("path"))
                );
    }

    private McpSchema.Tool listToolCat() {
        Map<String, Object> pathMap = new HashMap<>();
        pathMap.put("type", "string");
        pathMap.put("description", "需要查看内容的文件");
        return new McpSchema.Tool()
                .setName("cat")
                .setDescription("根据文件路径获取文件内容(文本)，类似linux下的cat命令")
                .setInputSchema(
                        new McpSchema.JsonSchema()
                                .setType("object")
                                .setProperties(Collections.singletonMap("path", pathMap))
                                .setRequired(Lists.newArrayList("path"))
                );
    }

    private String callTool(McpRequestSuperMap mcpSuperMap) throws JsonProcessingException {
        Object id = mcpSuperMap.getId();

        McpSchema.JSONRPCRequest<McpSchema.CallToolRequest> callToolRequest = mcpSuperMap.getMCPSchemaObject(new TypeReference<McpSchema.JSONRPCRequest<McpSchema.CallToolRequest>>() {
        });

        McpSchema.CallToolRequest callTool = callToolRequest.getParams();
        String toolName = callTool.getName();
        Map<String, Object> arguments = callTool.getArguments();

        McpSchema.JSONRPCResponse<McpSchema.CallToolResult> response = new McpSchema.JSONRPCResponse<>();
        response.setId(id);
        response.setJsonrpc(McpSchema.JSONRPC_VERSION);

        if ("ls".equals(toolName)) {
            callLs(response, arguments);
        } else if ("cat".equals(toolName)) {
            callCat(response, arguments);
        }

        return new ObjectMapper().writeValueAsString(response);
    }

    private void callLs(McpSchema.JSONRPCResponse<McpSchema.CallToolResult> response, Map<String, Object> arguments) {
        Object pathObj = arguments.get("path");
        McpSchema.CallToolResult callToolResult = new McpSchema.CallToolResult();
        response.setResult(callToolResult);
        callToolResult.setIsError(false);

        try {
            String path = pathObj.toString();
            File file = new File(path);
            if (!file.exists()) {
                callToolResult.setIsError(true);
                callToolResult.setContent(Lists.newArrayList(new McpSchema.TextContent().setText(String.format("路径%s不存在", path))));
                return;
            }
            if (!file.isDirectory()) {
                callToolResult.setIsError(true);
                callToolResult.setContent(Lists.newArrayList(new McpSchema.TextContent().setText(String.format("路径%s不是一个目录", path))));
                return;
            }
            String[] childFileList = file.list();
            List<McpSchema.Content> content = new ArrayList<>();
            callToolResult.setContent(content);
            for (String childFile : childFileList) {
                content.add(new McpSchema.TextContent().setText(childFile));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            callToolResult.setIsError(true);
            callToolResult.setContent(Lists.newArrayList(new McpSchema.TextContent().setText("未知异常：" + e.getMessage())));
        }
    }

    private void callCat(McpSchema.JSONRPCResponse<McpSchema.CallToolResult> response, Map<String, Object> arguments) {
        Object pathObj = arguments.get("path");
        McpSchema.CallToolResult callToolResult = new McpSchema.CallToolResult();
        response.setResult(callToolResult);
        callToolResult.setIsError(false);

        try {
            String path = pathObj.toString();
            File file = new File(path);
            if (!file.exists()) {
                callToolResult.setIsError(true);
                callToolResult.setContent(Lists.newArrayList(new McpSchema.TextContent().setText(String.format("路径%s不存在", path))));
                return;
            }
            if (!file.isFile()) {
                callToolResult.setIsError(true);
                callToolResult.setContent(Lists.newArrayList(new McpSchema.TextContent().setText(String.format("路径%s不是一个文件", path))));
                return;
            }
            try (
                    FileInputStream is = new FileInputStream(file);
            ) {
                List<McpSchema.Content> content = new ArrayList<>();
                callToolResult.setContent(content);
                content.add(new McpSchema.TextContent().setText(IOUtils.toString(is)));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            callToolResult.setIsError(true);
            callToolResult.setContent(Lists.newArrayList(new McpSchema.TextContent().setText("未知异常：" + e.getMessage())));
        }
    }

}
