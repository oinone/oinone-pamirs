package pro.shushi.pamirs.eip;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import pro.shushi.pamirs.eip.api.protocol.mcp.MCPRequestSuperMap;
import pro.shushi.pamirs.eip.api.protocol.mcp.McpSchema;
import pro.shushi.pamirs.eip.api.serializable.DefaultJSONSerializable;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.*;

/**
 * @author Gesi at 18:02 on 2025/8/6
 */
@Slf4j
public class MCPUserSqlTest {

    public static JdbcTemplate createJdbcTemplate() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/test_db?useSSL=false&allowPublicKeyRetrieval=true&useServerPrepStmts=true&cachePrepStmts=true&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&autoReconnect=true&allowMultiQueries=true");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        return new JdbcTemplate(dataSource);
    }

    public static void main(String[] args) {
        JdbcTemplate jdbcTemplate = createJdbcTemplate();
        List<User> users = jdbcTemplate.query("select * from user", new BeanPropertyRowMapper<>(User.class));
        System.out.println(users);
    }

    /**
     * CREATE TABLE `user`  (
     * `id` bigint NOT NULL,
     * `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
     * `age` int NULL DEFAULT NULL,
     * PRIMARY KEY (`id`) USING BTREE
     * ) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;
     * INSERT INTO `user` VALUES (1, '张三', 18);
     * INSERT INTO `user` VALUES (2, '李四', 20);
     * INSERT INTO `user` VALUES (3, '王五', 21);
     */
    @Data
    public static class User {
        private Long id;
        private String name;
        private Integer age;
    }

    public String controller(String body) {
        log.info("request:\n{}", body);

        Object id = null;
        try {
            MCPRequestSuperMap mcpSuperMap = new MCPRequestSuperMap(BeanDefinitionUtils.getBean(DefaultJSONSerializable.class).serializable(body));
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

    private String initialize(MCPRequestSuperMap mcpSuperMap) throws JsonProcessingException {
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

    private String listTools(MCPRequestSuperMap mcpSuperMap) throws JsonProcessingException {
        Object id = mcpSuperMap.getId();

        McpSchema.JSONRPCResponse<McpSchema.ListToolsResult> response = new McpSchema.JSONRPCResponse<>();
        response.setId(id);
        response.setJsonrpc(McpSchema.JSONRPC_VERSION);

        McpSchema.ListToolsResult result = new McpSchema.ListToolsResult();
        result.setTools(new ArrayList<>());
        result.getTools().add(listToolCreateUser());
        result.getTools().add(listToolQueryUser());

        response.setResult(result);

        return new ObjectMapper().writeValueAsString(response);
    }

    private McpSchema.Tool listToolCreateUser() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("type", "object");
        userMap.put("description", "插入的用户信息");
        {
            Map<String, Object> propertiesMap = new HashMap<>();
            userMap.put("properties", propertiesMap);
            Map<String, Object> idMap = new HashMap<>();
            idMap.put("type", "number");
            idMap.put("description", "用户id");
            Map<String, Object> nameMap = new HashMap<>();
            nameMap.put("type", "string");
            nameMap.put("description", "用户名");
            Map<String, Object> ageMap = new HashMap<>();
            ageMap.put("type", "number");
            ageMap.put("description", "用户年龄");
            propertiesMap.put("id", idMap);
            propertiesMap.put("name", nameMap);
            propertiesMap.put("age", ageMap);
        }
        return new McpSchema.Tool()
                .setName("create_user")
                .setDescription("往数据库里插入用户，id数据库自增")
                .setInputSchema(
                        new McpSchema.JsonSchema()
                                .setType("object")
                                .setProperties(Collections.singletonMap("user", userMap))
                                .setRequired(Lists.newArrayList("user"))
                );
    }

    private McpSchema.Tool listToolQueryUser() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("type", "object");
        userMap.put("description", "用户信息查询条件，用法类似于jpa，提供一个User实体对象，会根据里面的非空字段做查询条件");
        {
            Map<String, Object> propertiesMap = new HashMap<>();
            userMap.put("properties", propertiesMap);
            Map<String, Object> idMap = new HashMap<>();
            idMap.put("type", "number");
            idMap.put("description", "用户id");
            Map<String, Object> nameMap = new HashMap<>();
            nameMap.put("type", "string");
            nameMap.put("description", "用户名");
            Map<String, Object> ageMap = new HashMap<>();
            ageMap.put("type", "number");
            ageMap.put("description", "用户年龄");
            propertiesMap.put("id", idMap);
            propertiesMap.put("name", nameMap);
            propertiesMap.put("age", ageMap);
        }
        return new McpSchema.Tool()
                .setName("query_user_list")
                .setDescription("数据库查询用户列表")
                .setInputSchema(
                        new McpSchema.JsonSchema()
                                .setType("object")
                                .setProperties(Collections.singletonMap("user", userMap))
                                .setRequired(Lists.newArrayList())
                );
    }

    private String callTool(MCPRequestSuperMap mcpSuperMap) throws JsonProcessingException {
        Object id = mcpSuperMap.getId();

        McpSchema.JSONRPCRequest<McpSchema.CallToolRequest> callToolRequest = mcpSuperMap.getMCPSchemaObject(new TypeReference<McpSchema.JSONRPCRequest<McpSchema.CallToolRequest>>() {
        });

        McpSchema.CallToolRequest callTool = callToolRequest.getParams();
        String toolName = callTool.getName();
        Map<String, Object> arguments = callTool.getArguments();

        McpSchema.JSONRPCResponse<McpSchema.CallToolResult> response = new McpSchema.JSONRPCResponse<>();
        response.setId(id);
        response.setJsonrpc(McpSchema.JSONRPC_VERSION);

        if ("create_user".equals(toolName)) {
            callCreateUser(response, arguments);
        } else if ("query_user_list".equals(toolName)) {
            callQueryUserList(response, arguments);
        }

        return new ObjectMapper().writeValueAsString(response);
    }

    private void callCreateUser(McpSchema.JSONRPCResponse<McpSchema.CallToolResult> response, Map<String, Object> arguments) {
        Object userObj = arguments.get("user");
        McpSchema.CallToolResult callToolResult = new McpSchema.CallToolResult();
        response.setResult(callToolResult);
        callToolResult.setIsError(false);

        try {
            System.out.println(userObj.getClass());
            JSONObject param = JSON.parseObject(JSON.toJSONString(userObj));

            JdbcTemplate jdbcTemplate = createJdbcTemplate();
            int update = jdbcTemplate.update("insert into user(name, age) values(?, ?)",
                    param.getString("name"), param.getInteger("age")
            );

            List<McpSchema.Content> contents = new ArrayList<>();
            callToolResult.setContent(contents);

            contents.add(new McpSchema.TextContent().setText("update数量：" + update));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            callToolResult.setIsError(true);
            callToolResult.setContent(Lists.newArrayList(new McpSchema.TextContent().setText("未知异常：" + e.getMessage())));
        }
    }

    private void callQueryUserList(McpSchema.JSONRPCResponse<McpSchema.CallToolResult> response, Map<String, Object> arguments) {
        Object userObj = arguments.get("user");
        McpSchema.CallToolResult callToolResult = new McpSchema.CallToolResult();
        response.setResult(callToolResult);
        callToolResult.setIsError(false);

        try {
            System.out.println(userObj);
            JSONObject param = userObj != null ? JSON.parseObject(JSON.toJSONString(userObj)) : new JSONObject();

            boolean hasWhere = false;
            StringBuilder sqlBuilder = new StringBuilder("select * from user");
            List<Object> selectParams = new ArrayList<>();

            if (param.containsKey("id") && param.get("id") != null) {
                sqlBuilder.append(" where ").append(" id = ? ");
                selectParams.add(param.get("id"));
                hasWhere = true;
            }
            if (param.containsKey("name") && param.get("name") != null) {
                sqlBuilder.append(!hasWhere ? " where " : " and ").append(" name = ? ");
                selectParams.add(param.get("name"));
                hasWhere = true;
            }
            if (param.containsKey("age") && param.get("age") != null) {
                sqlBuilder.append(!hasWhere ? " where " : " and ").append(" age = ? ");
                selectParams.add(param.get("age"));
                hasWhere = true;
            }

            JdbcTemplate jdbcTemplate = createJdbcTemplate();
            List<User> users = jdbcTemplate.query(sqlBuilder.toString(),
                    selectParams.toArray(new Object[param.size()]),
                    new BeanPropertyRowMapper<>(User.class)
            );

            List<McpSchema.Content> contents = new ArrayList<>();
            callToolResult.setContent(contents);

            contents.add(new McpSchema.TextContent().setText("查询用户信息：\n" + JSON.toJSONString(users)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            callToolResult.setIsError(true);
            callToolResult.setContent(Lists.newArrayList(new McpSchema.TextContent().setText("未知异常：" + e.getMessage())));
        }
    }

}
