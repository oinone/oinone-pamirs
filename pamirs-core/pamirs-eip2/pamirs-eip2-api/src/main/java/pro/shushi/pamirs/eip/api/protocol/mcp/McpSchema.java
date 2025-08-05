package pro.shushi.pamirs.eip.api.protocol.mcp;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.util.Assert;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Tzolov
 * @link io.modelcontextprotocol.spec.McpSchema
 * <p>
 * Based on the <a href="http://www.jsonrpc.org/specification">JSON-RPC 2.0
 * specification</a> and the <a href=
 * "https://github.com/modelcontextprotocol/specification/blob/main/schema/schema.ts">Model
 * Context Protocol Schema</a>.
 */
public class McpSchema {

    private McpSchema() {
    }

    public static final String LATEST_PROTOCOL_VERSION = "2024-11-05";

    public static final String JSONRPC_VERSION = "2.0";

    // ---------------------------
    // Method Names
    // ---------------------------

    // Lifecycle Methods
    public static final String METHOD_INITIALIZE = "initialize";

    public static final String METHOD_NOTIFICATION_INITIALIZED = "notifications/initialized";

    public static final String METHOD_PING = "ping";

    // Tool Methods
    public static final String METHOD_TOOLS_LIST = "tools/list";

    public static final String METHOD_TOOLS_CALL = "tools/call";

    public static final String METHOD_NOTIFICATION_TOOLS_LIST_CHANGED = "notifications/tools/list_changed";

    // Resources Methods
    public static final String METHOD_RESOURCES_LIST = "resources/list";

    public static final String METHOD_RESOURCES_READ = "resources/read";

    public static final String METHOD_NOTIFICATION_RESOURCES_LIST_CHANGED = "notifications/resources/list_changed";

    public static final String METHOD_RESOURCES_TEMPLATES_LIST = "resources/templates/list";

    public static final String METHOD_RESOURCES_SUBSCRIBE = "resources/subscribe";

    public static final String METHOD_RESOURCES_UNSUBSCRIBE = "resources/unsubscribe";

    // Prompt Methods
    public static final String METHOD_PROMPT_LIST = "prompts/list";

    public static final String METHOD_PROMPT_GET = "prompts/get";

    public static final String METHOD_NOTIFICATION_PROMPTS_LIST_CHANGED = "notifications/prompts/list_changed";

    public static final String METHOD_COMPLETION_COMPLETE = "completion/complete";

    // Logging Methods
    public static final String METHOD_LOGGING_SET_LEVEL = "logging/setLevel";

    public static final String METHOD_NOTIFICATION_MESSAGE = "notifications/message";

    // Roots Methods
    public static final String METHOD_ROOTS_LIST = "roots/list";

    public static final String METHOD_NOTIFICATION_ROOTS_LIST_CHANGED = "notifications/roots/list_changed";

    // Sampling Methods
    public static final String METHOD_SAMPLING_CREATE_MESSAGE = "sampling/createMessage";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // ---------------------------
    // JSON-RPC Error Codes
    // ---------------------------

    /**
     * Standard error codes used in MCP JSON-RPC responses.
     */
    public static final class ErrorCodes {

        /**
         * Invalid JSON was received by the server.
         */
        public static final int PARSE_ERROR = -32700;

        /**
         * The JSON sent is not a valid Request object.
         */
        public static final int INVALID_REQUEST = -32600;

        /**
         * The method does not exist / is not available.
         */
        public static final int METHOD_NOT_FOUND = -32601;

        /**
         * Invalid method parameter(s).
         */
        public static final int INVALID_PARAMS = -32602;

        /**
         * Internal JSON-RPC error.
         */
        public static final int INTERNAL_ERROR = -32603;

    }

    public interface Request {
    }

    private static final TypeReference<HashMap<String, Object>> MAP_TYPE_REF = new TypeReference<HashMap<String, Object>>() {
    };

    public interface JSONRPCMessage {

        String getJsonrpc();

    }

    // ---------------------------
    // JSON-RPC Message Types
    // ---------------------------

    @Data
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JSONRPCRequest<T> implements JSONRPCMessage {
        @JsonProperty("jsonrpc")
        String jsonrpc;
        @JsonProperty("method")
        String method;
        @JsonProperty("id")
        Object id;
        @JsonProperty("params")
        T params;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JSONRPCNotification implements JSONRPCMessage {
        @JsonProperty("jsonrpc")
        String jsonrpc;
        @JsonProperty("method")
        String method;
        @JsonProperty("params")
        Map<String, Object> params;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JSONRPCResponse<T> implements JSONRPCMessage {
        @JsonProperty("jsonrpc")
        String jsonrpc;
        @JsonProperty("id")
        Object id;
        @JsonProperty("result")
        T result;
        @JsonProperty("error")
        JSONRPCError error;

        @Data
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class JSONRPCError {
            @JsonProperty("code")
            int code;
            @JsonProperty("message")
            String message;
            @JsonProperty("data")
            Object data;
        }
    }

    // ---------------------------
    // Initialization
    // ---------------------------
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class InitializeRequest implements Request {
        @JsonProperty("protocolVersion")
        String protocolVersion;
        @JsonProperty("capabilities")
        ClientCapabilities capabilities;
        @JsonProperty("clientInfo")
        Implementation clientInfo;
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class InitializeResult {
        @JsonProperty("protocolVersion")
        String protocolVersion;
        @JsonProperty("capabilities")
        ServerCapabilities capabilities;
        @JsonProperty("serverInfo")
        Implementation serverInfo;
        @JsonProperty("instructions")
        String instructions;
    }

    /**
     * Clients can implement additional features to enrich connected MCP servers with
     * additional capabilities. These capabilities can be used to extend the functionality
     * of the server, or to provide additional information to the server about the
     * client's capabilities.
     *
     * @field experimental WIP
     * @field roots define the boundaries of where servers can operate within the
     * filesystem, allowing them to understand which directories and files they have
     * access to.
     * @field sampling Provides a standardized way for servers to request LLM sampling
     * (“completions” or “generations”) from language models via clients.
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ClientCapabilities {
        @JsonProperty("experimental")
        Map<String, Object> experimental;
        @JsonProperty("roots")
        RootCapabilities roots;
        @JsonProperty("sampling")
        Sampling sampling;

        /**
         * Roots define the boundaries of where servers can operate within the filesystem,
         * allowing them to understand which directories and files they have access to.
         * Servers can request the list of roots from supporting clients and
         * receive notifications when that list changes.
         *
         * @field listChanged Whether the client would send notification about roots
         * has changed since the last time the server checked.
         */
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data
        public static class RootCapabilities {
            @JsonProperty("listChanged")
            Boolean listChanged;
        }

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @Data
        public static class Sampling {
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Map<String, Object> experimental;
            private RootCapabilities roots;
            private Sampling sampling;

            public Builder experimental(Map<String, Object> experimental) {
                this.experimental = experimental;
                return this;
            }

            public Builder roots(Boolean listChanged) {
                this.roots = new RootCapabilities();
                this.roots.listChanged = listChanged;
                return this;
            }

            public Builder sampling() {
                this.sampling = new Sampling();
                return this;
            }

            public ClientCapabilities build() {
                ClientCapabilities clientCapabilities = new ClientCapabilities();
                clientCapabilities.experimental = experimental;
                clientCapabilities.roots = roots;
                clientCapabilities.sampling = sampling;
                return clientCapabilities;
            }
        }
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ServerCapabilities {
        @JsonProperty("completions")
        CompletionCapabilities completions;
        @JsonProperty("experimental")
        Map<String, Object> experimental;
        @JsonProperty("logging")
        LoggingCapabilities logging;
        @JsonProperty("prompts")
        PromptCapabilities prompts;
        @JsonProperty("resources")
        ResourceCapabilities resources;
        @JsonProperty("tools")
        ToolCapabilities tools;

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @Data
        public static class CompletionCapabilities {
        }

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @Data
        public static class LoggingCapabilities {
        }

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @Data
        public static class PromptCapabilities {
            @JsonProperty("listChanged")
            Boolean listChanged;
        }

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @Data
        public static class ResourceCapabilities {
            @JsonProperty("subscribe")
            Boolean subscribe;
            @JsonProperty("listChanged")
            Boolean listChanged;
        }

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @Data
        public static class ToolCapabilities {
            @JsonProperty("listChanged")
            Boolean listChanged;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private CompletionCapabilities completions;
            private Map<String, Object> experimental;
            private LoggingCapabilities logging = new LoggingCapabilities();
            private PromptCapabilities prompts;
            private ResourceCapabilities resources;
            private ToolCapabilities tools;

            public Builder completions() {
                this.completions = new CompletionCapabilities();
                return this;
            }

            public Builder experimental(Map<String, Object> experimental) {
                this.experimental = experimental;
                return this;
            }

            public Builder logging() {
                this.logging = new LoggingCapabilities();
                return this;
            }

            public Builder prompts(Boolean listChanged) {
                this.prompts = new PromptCapabilities();
                this.prompts.listChanged = listChanged;
                return this;
            }

            public Builder resources(Boolean subscribe, Boolean listChanged) {
                this.resources = new ResourceCapabilities();
                this.resources.subscribe = subscribe;
                this.resources.listChanged = listChanged;
                return this;
            }

            public Builder tools(Boolean listChanged) {
                this.tools = new ToolCapabilities();
                this.tools.listChanged = listChanged;
                return this;
            }

            public ServerCapabilities build() {
                ServerCapabilities serverCapabilities = new ServerCapabilities();
                serverCapabilities.completions = completions;
                serverCapabilities.experimental = experimental;
                serverCapabilities.logging = logging;
                serverCapabilities.prompts = prompts;
                serverCapabilities.resources = resources;
                serverCapabilities.tools = tools;
                return serverCapabilities;
            }
        }
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Implementation {
        @JsonProperty("name")
        String name;
        @JsonProperty("version")
        String version;
    }

    // Existing Enums and Base Types (from previous implementation)
    public enum Role {
        @JsonProperty("user") USER,
        @JsonProperty("assistant") ASSISTANT
    }

    /**
     * Optional annotations for the client. The client can use annotations to inform how
     * objects are used or displayed.
     *
     * @field audience Describes who the intended customer of this object or data is. It
     * can include multiple entries to indicate content useful for multiple audiences
     * (e.g., `["user", "assistant"]`).
     * @field priority Describes how important this data is for operating the server. A
     * value of 1 means "most important," and indicates that the data is effectively
     * required, while 0 means "least important," and indicates that the data is entirely
     * optional. It is a number between 0 and 1.
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Annotations {
        @JsonProperty("audience")
        List<Role> audience;
        @JsonProperty("priority")
        Double priority;
    }

    /**
     * A known resource that the server is capable of reading.
     *
     * @field uri the URI of the resource.
     * @field name A human-readable name for this resource. This can be used by clients to
     * populate UI elements.
     * @field description A description of what this resource represents. This can be used
     * by clients to improve the LLM's understanding of available resources. It can be
     * thought of like a "hint" to the model.
     * @field mimeType The MIME type of this resource, if known.
     * @field annotations Optional annotations for the client. The client can use
     * annotations to inform how objects are used or displayed.
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Resource {
        @JsonProperty("uri")
        String uri;
        @JsonProperty("name")
        String name;
        @JsonProperty("description")
        String description;
        @JsonProperty("mimeType")
        String mimeType;
        @JsonProperty("annotations")
        Annotations annotations;
    }

    /**
     * Resource templates allow servers to expose parameterized resources using URI
     * templates.
     *
     * @field uriTemplate A URI template that can be used to generate URIs for this
     * resource.
     * @field name A human-readable name for this resource. This can be used by clients to
     * populate UI elements.
     * @field description A description of what this resource represents. This can be used
     * by clients to improve the LLM's understanding of available resources. It can be
     * thought of like a "hint" to the model.
     * @field mimeType The MIME type of this resource, if known.
     * @field annotations Optional annotations for the client. The client can use
     * annotations to inform how objects are used or displayed.
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6570">RFC 6570</a>
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ResourceTemplate {
        @JsonProperty("uriTemplate")
        String uriTemplate;
        @JsonProperty("name")
        String name;
        @JsonProperty("description")
        String description;
        @JsonProperty("mimeType")
        String mimeType;
        @JsonProperty("annotations")
        Annotations annotations;
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ListResourcesResult {
        @JsonProperty("resources")
        List<Resource> resources;
        @JsonProperty("nextCursor")
        String nextCursor;
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ListResourceTemplatesResul {
        @JsonProperty("resourceTemplates")
        List<ResourceTemplate> resourceTemplates;
        @JsonProperty("nextCursor")
        String nextCursor;
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ReadResourceRequest {
        @JsonProperty("uri")
        String uri;
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ReadResourceResult {
        @JsonProperty("contents")
        List<ResourceContents> contents;
    }

    /**
     * Sent from the client to request resources/updated notifications from the server
     * whenever a particular resource changes.
     *
     * @field uri the URI of the resource to subscribe to. The URI can use any protocol;
     * it is up to the server how to interpret it.
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class SubscribeRequest {
        @JsonProperty("uri")
        String uri;
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class UnsubscribeRequest {
        @JsonProperty("uri")
        String uri;
    }

    public interface ResourceContents {
        /**
         * The URI of this resource.
         *
         * @return the URI of this resource.
         */
        String getUri();

        /**
         * The MIME type of this resource.
         *
         * @return the MIME type of this resource.
         */
        String getMimeType();
    }

    /**
     * Text contents of a resource.
     *
     * @field uri the URI of this resource.
     * @field mimeType the MIME type of this resource.
     * @field text the text of the resource. This must only be set if the resource can
     * actually be represented as text (not binary data).
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class TextResourceContents implements ResourceContents {
        @JsonProperty("uri")
        String uri;
        @JsonProperty("mimeType")
        String mimeType;
        @JsonProperty("text")
        String text;
    }

    /**
     * Binary contents of a resource.
     *
     * @field uri the URI of this resource.
     * @field mimeType the MIME type of this resource.
     * @field blob a base64-encoded string representing the binary data of the resource.
     * This must only be set if the resource can actually be represented as binary data
     * (not text).
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class BlobResourceContents implements ResourceContents {
        @JsonProperty("uri")
        String uri;
        @JsonProperty("mimeType")
        String mimeType;
        @JsonProperty("blob")
        String blob;
    }

    // ---------------------------
    // Prompt Interfaces
    // ---------------------------

    /**
     * A prompt or prompt template that the server offers.
     *
     * @field name The name of the prompt or prompt template.
     * @field description An optional description of what this prompt provides.
     * @field arguments A list of arguments to use for templating the prompt.
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Prompt {
        @JsonProperty("name")
        String name;
        @JsonProperty("description")
        String description;
        @JsonProperty("arguments")
        List<PromptArgument> arguments;
    }

    /**
     * Describes an argument that a prompt can accept.
     *
     * @field name The name of the argument.
     * @field description A human-readable description of the argument.
     * @field required Whether this argument must be provided.
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class PromptArgument {
        @JsonProperty("name")
        String name;
        @JsonProperty("description")
        String description;
        @JsonProperty("required")
        Boolean required;
    }

    /**
     * Describes a message returned as part of a prompt.
     * <p>
     * This is similar to `SamplingMessage`, but also supports the embedding of resources
     * from the MCP server.
     *
     * @field role The sender or recipient of messages and data in a conversation.
     * @field content The content of the message of type {@link Content}.
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class PromptMessage {
        @JsonProperty("role")
        Role role;
        @JsonProperty("content")
        Content content;
    }

    /**
     * The server's response to a prompts/list request from the client.
     *
     * @field prompts A list of prompts that the server provides.
     * @field nextCursor An optional cursor for pagination. If present, indicates there
     * are more prompts available.
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ListPromptsResult {
        @JsonProperty("prompts")
        List<Prompt> prompts;
        @JsonProperty("nextCursor")
        String nextCursor;
    }

    /**
     * Used by the client to get a prompt provided by the server.
     *
     * @field name The name of the prompt or prompt template.
     * @field arguments Arguments to use for templating the prompt.
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class GetPromptRequest implements Request {
        @JsonProperty("name")
        String name;
        @JsonProperty("arguments")
        Map<String, Object> arguments;
    }

    /**
     * The server's response to a prompts/get request from the client.
     *
     * @field description An optional description for the prompt.
     * @field messages A list of messages to display as part of the prompt.
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class GetPromptResult {
        @JsonProperty("description")
        String description;
        @JsonProperty("messages")
        List<PromptMessage> messages;
    }

    // ---------------------------
    // Tool Interfaces
    // ---------------------------

    /**
     * The server's response to a tools/list request from the client.
     *
     * @field tools A list of tools that the server provides.
     * @field nextCursor An optional cursor for pagination. If present, indicates there
     * are more tools available.
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ListToolsResult {
        @JsonProperty("tools")
        List<Tool> tools;
        @JsonProperty("nextCursor")
        String nextCursor;
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class JsonSchema {
        @JsonProperty("type")
        String type;
        @JsonProperty("properties")
        Map<String, Object> properties;
        @JsonProperty("required")
        List<String> required;
        @JsonProperty("additionalProperties")
        Boolean additionalProperties;
        @JsonProperty("$defs")
        Map<String, Object> defs;
        @JsonProperty("definitions")
        Map<String, Object> definitions;
    }

    /**
     * Represents a tool that the server provides. Tools enable servers to expose
     * executable functionality to the system. Through these tools, you can interact with
     * external systems, perform computations, and take actions in the real world.
     *
     * @field name A unique identifier for the tool. This name is used when calling the
     * tool.
     * @field description A human-readable description of what the tool does. This can be
     * used by clients to improve the LLM's understanding of available tools.
     * @field inputSchema A JSON Schema object that describes the expected structure of
     * the arguments when calling this tool. This allows clients to validate tool
     * arguments before sending them to the server.
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Tool {
        @JsonProperty("name")
        String name;
        @JsonProperty("description")
        String description;
        @JsonProperty("inputSchema")
        JsonSchema inputSchema;

        public Tool() {
        }

        public Tool(String name, String description, String schema) {
            this.name = name;
            this.description = description;
            this.inputSchema = parseSchema(schema);
        }

        private static JsonSchema parseSchema(String schema) {
            try {
                return OBJECT_MAPPER.readValue(schema, JsonSchema.class);
            } catch (IOException e) {
                throw new IllegalArgumentException("Invalid schema: " + schema, e);
            }
        }
    }


    /**
     * Used by the client to call a tool provided by the server.
     *
     * @field name The name of the tool to call. This must match a tool name from
     * tools/list.
     * @field arguments Arguments to pass to the tool. These must conform to the tool's
     * input schema.
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class CallToolRequest implements Request {
        @JsonProperty("name")
        String name;
        @JsonProperty("arguments")
        Map<String, Object> arguments;

        public CallToolRequest() {
        }

        public CallToolRequest(String name, String jsonArguments) {
            this(name, parseJsonArguments(jsonArguments));
        }

        public CallToolRequest(String name, Map<String, Object> jsonArguments) {
            this.name = name;
            this.arguments = jsonArguments;
        }

        private static Map<String, Object> parseJsonArguments(String jsonArguments) {
            try {
                return OBJECT_MAPPER.readValue(jsonArguments, MAP_TYPE_REF);
            } catch (IOException e) {
                throw new IllegalArgumentException("Invalid arguments: " + jsonArguments, e);
            }
        }
    }

    /**
     * The server's response to a tools/call request from the client.
     *
     * @field content A list of content items representing the tool's output. Each item can be text, an image,
     * or an embedded resource.
     * @field isError If true, indicates that the tool execution failed and the content contains error information.
     * If false or absent, indicates successful execution.
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class CallToolResult {
        @JsonProperty("content")
        List<Content> content;
        @JsonProperty("isError")
        Boolean isError;

        public CallToolResult() {
        }

        public CallToolResult(List<Content> content, Boolean isError) {
            this.content = content;
            this.isError = isError;
        }

        /**
         * Creates a new instance of {@link CallToolResult} with a string containing the
         * tool result.
         *
         * @param content The content of the tool result. This will be mapped to a one-sized list
         *                with a {@link TextContent} element.
         * @param isError If true, indicates that the tool execution failed and the content contains error information.
         *                If false or absent, indicates successful execution.
         */
        public CallToolResult(String content, Boolean isError) {
            this(Lists.newArrayList(new TextContent(content)), isError);
        }

        /**
         * Creates a builder for {@link CallToolResult}.
         *
         * @return a new builder instance
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Builder for {@link CallToolResult}.
         */
        public static class Builder {
            private List<Content> content = new ArrayList<>();
            private Boolean isError;

            /**
             * Sets the content list for the tool result.
             *
             * @param content the content list
             * @return this builder
             */
            public Builder content(List<Content> content) {
                Assert.notNull(content, "content must not be null");
                this.content = content;
                return this;
            }

            /**
             * Sets the text content for the tool result.
             *
             * @param textContent the text content
             * @return this builder
             */
            public Builder textContent(List<String> textContent) {
                Assert.notNull(textContent, "textContent must not be null");
                textContent.stream()
                        .map(TextContent::new)
                        .forEach(this.content::add);
                return this;
            }

            /**
             * Adds a content item to the tool result.
             *
             * @param contentItem the content item to add
             * @return this builder
             */
            public Builder addContent(Content contentItem) {
                Assert.notNull(contentItem, "contentItem must not be null");
                if (this.content == null) {
                    this.content = new ArrayList<>();
                }
                this.content.add(contentItem);
                return this;
            }

            /**
             * Adds a text content item to the tool result.
             *
             * @param text the text content
             * @return this builder
             */
            public Builder addTextContent(String text) {
                Assert.notNull(text, "text must not be null");
                return addContent(new TextContent(text));
            }

            /**
             * Sets whether the tool execution resulted in an error.
             *
             * @param isError true if the tool execution failed, false otherwise
             * @return this builder
             */
            public Builder isError(Boolean isError) {
                Assert.notNull(isError, "isError must not be null");
                this.isError = isError;
                return this;
            }

            /**
             * Builds a new {@link CallToolResult} instance.
             *
             * @return a new CallToolResult instance
             */
            public CallToolResult build() {
                return new CallToolResult(content, isError);
            }
        }
    }

    // ---------------------------
    // Sampling Interfaces
    // ---------------------------
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ModelPreferences {
        @JsonProperty("hints")
        List<ModelHint> hints;
        @JsonProperty("costPriority")
        Double costPriority;
        @JsonProperty("speedPriority")
        Double speedPriority;
        @JsonProperty("intelligencePriority")
        Double intelligencePriority;

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private List<ModelHint> hints;
            private Double costPriority;
            private Double speedPriority;
            private Double intelligencePriority;

            public Builder hints(List<ModelHint> hints) {
                this.hints = hints;
                return this;
            }

            public Builder addHint(String name) {
                if (this.hints == null) {
                    this.hints = new ArrayList<>();
                }
                ModelHint modelHint = new ModelHint();
                modelHint.setName(name);
                this.hints.add(modelHint);
                return this;
            }

            public Builder costPriority(Double costPriority) {
                this.costPriority = costPriority;
                return this;
            }

            public Builder speedPriority(Double speedPriority) {
                this.speedPriority = speedPriority;
                return this;
            }

            public Builder intelligencePriority(Double intelligencePriority) {
                this.intelligencePriority = intelligencePriority;
                return this;
            }

            public ModelPreferences build() {
                ModelPreferences modelPreferences = new ModelPreferences();
                modelPreferences.hints = hints;
                modelPreferences.costPriority = costPriority;
                modelPreferences.speedPriority = speedPriority;
                modelPreferences.intelligencePriority = intelligencePriority;
                return modelPreferences;
            }
        }
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ModelHint {
        @JsonProperty("name")
        String name;
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class SamplingMessage {
        @JsonProperty("role")
        Role role;
        @JsonProperty("content")
        Content content;
    }

    // Sampling and Message Creation
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class CreateMessageRequest implements Request {
        @JsonProperty("messages")
        List<SamplingMessage> messages;
        @JsonProperty("modelPreferences")
        ModelPreferences modelPreferences;
        @JsonProperty("systemPrompt")
        String systemPrompt;
        @JsonProperty("includeContext")
        ContextInclusionStrategy includeContext;
        @JsonProperty("temperature")
        Double temperature;
        @JsonProperty("maxTokens")
        int maxTokens;
        @JsonProperty("stopSequences")
        List<String> stopSequences;
        @JsonProperty("metadata")
        Map<String, Object> metadata;

        public enum ContextInclusionStrategy {
            @JsonProperty("none") NONE,
            @JsonProperty("thisServer") THIS_SERVER,
            @JsonProperty("allServers") ALL_SERVERS
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private List<SamplingMessage> messages;
            private ModelPreferences modelPreferences;
            private String systemPrompt;
            private ContextInclusionStrategy includeContext;
            private Double temperature;
            private int maxTokens;
            private List<String> stopSequences;
            private Map<String, Object> metadata;

            public Builder messages(List<SamplingMessage> messages) {
                this.messages = messages;
                return this;
            }

            public Builder modelPreferences(ModelPreferences modelPreferences) {
                this.modelPreferences = modelPreferences;
                return this;
            }

            public Builder systemPrompt(String systemPrompt) {
                this.systemPrompt = systemPrompt;
                return this;
            }

            public Builder includeContext(ContextInclusionStrategy includeContext) {
                this.includeContext = includeContext;
                return this;
            }

            public Builder temperature(Double temperature) {
                this.temperature = temperature;
                return this;
            }

            public Builder maxTokens(int maxTokens) {
                this.maxTokens = maxTokens;
                return this;
            }

            public Builder stopSequences(List<String> stopSequences) {
                this.stopSequences = stopSequences;
                return this;
            }

            public Builder metadata(Map<String, Object> metadata) {
                this.metadata = metadata;
                return this;
            }

            public CreateMessageRequest build() {
                CreateMessageRequest createMessageRequest = new CreateMessageRequest();
                createMessageRequest.messages = messages;
                createMessageRequest.modelPreferences = modelPreferences;
                createMessageRequest.systemPrompt = systemPrompt;
                createMessageRequest.includeContext = includeContext;
                createMessageRequest.temperature = temperature;
                createMessageRequest.maxTokens = maxTokens;
                createMessageRequest.stopSequences = stopSequences;
                createMessageRequest.metadata = metadata;
                return createMessageRequest;
            }
        }

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data
        public static class CreateMessageResult {
            @JsonProperty("role")
            Role role;
            @JsonProperty("content")
            Content content;
            @JsonProperty("model")
            String model;
            @JsonProperty("stopReason")
            StopReason stopReason;

            public enum StopReason {
                @JsonProperty("endTurn") END_TURN,
                @JsonProperty("stopSequence") STOP_SEQUENCE,
                @JsonProperty("maxTokens") MAX_TOKENS
            }

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {
                private Role role = Role.ASSISTANT;
                private Content content;
                private String model;
                private StopReason stopReason = StopReason.END_TURN;

                public Builder role(Role role) {
                    this.role = role;
                    return this;
                }

                public Builder content(Content content) {
                    this.content = content;
                    return this;
                }

                public Builder model(String model) {
                    this.model = model;
                    return this;
                }

                public Builder stopReason(StopReason stopReason) {
                    this.stopReason = stopReason;
                    return this;
                }

                public Builder message(String message) {
                    this.content = new TextContent(message);
                    return this;
                }

                public CreateMessageResult build() {
                    CreateMessageResult createMessageResult = new CreateMessageResult();
                    createMessageResult.role = role;
                    createMessageResult.content = content;
                    createMessageResult.model = model;
                    createMessageResult.stopReason = stopReason;
                    return createMessageResult;
                }
            }
        }

        // ---------------------------
        // Pagination Interfaces
        // ---------------------------
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data
        public static class PaginatedRequest {
            @JsonProperty("cursor")
            String cursor;
        }

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data
        public static class PaginatedResult {
            @JsonProperty("nextCursor")
            String nextCursor;
        }

        // ---------------------------
        // Progress and Logging
        // ---------------------------
        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data
        public static class ProgressNotification {
            @JsonProperty("progressToken")
            String progressToken;
            @JsonProperty("progress")
            double progress;
            @JsonProperty("total")
            Double total;
        }

        /**
         * The Model Context Protocol (MCP) provides a standardized way for servers to send
         * structured log messages to clients. Clients can control logging verbosity by
         * setting minimum log levels, with servers sending notifications containing severity
         * levels, optional logger names, and arbitrary JSON-serializable data.
         *
         * @field level The severity levels. The minimum log level is set by the client.
         * @field logger The logger that generated the message.
         * @field data JSON-serializable logging data.
         */
        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data
        public static class LoggingMessageNotification {
            @JsonProperty("level")
            LoggingLevel level;
            @JsonProperty("logger")
            String logger;
            @JsonProperty("data")
            String data;

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {
                private LoggingLevel level = LoggingLevel.INFO;
                private String logger = "server";
                private String data;

                public Builder level(LoggingLevel level) {
                    this.level = level;
                    return this;
                }

                public Builder logger(String logger) {
                    this.logger = logger;
                    return this;
                }

                public Builder data(String data) {
                    this.data = data;
                    return this;
                }

                public LoggingMessageNotification build() {
                    LoggingMessageNotification loggingMessageNotification = new LoggingMessageNotification();
                    loggingMessageNotification.level = level;
                    loggingMessageNotification.logger = logger;
                    loggingMessageNotification.data = data;
                    return loggingMessageNotification;
                }
            }
        }

        public enum LoggingLevel {
            @JsonProperty("debug") DEBUG(0),
            @JsonProperty("info") INFO(1),
            @JsonProperty("notice") NOTICE(2),
            @JsonProperty("warning") WARNING(3),
            @JsonProperty("error") ERROR(4),
            @JsonProperty("critical") CRITICAL(5),
            @JsonProperty("alert") ALERT(6),
            @JsonProperty("emergency") EMERGENCY(7);

            private final int level;

            LoggingLevel(int level) {
                this.level = level;
            }

            public int level() {
                return level;
            }

        }

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class SetLevelRequest {
            @JsonProperty("level")
            LoggingLevel level;
        }

        // ---------------------------
        // Autocomplete
        // ---------------------------
        public interface CompleteReference {

            String getType();

            String getIdentifier();

        }

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data
        public static class PromptReference implements CompleteReference {

            @JsonProperty("type")
            String type;
            @JsonProperty("name")
            String name;

            public PromptReference() {
                this.type = "ref/prompt";
            }

            public PromptReference(String name) {
                this.type = "ref/prompt";
                this.name = name;
            }

            @Override
            public String getIdentifier() {
                return getName();
            }
        }

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data
        public static class ResourceReference implements CompleteReference {

            @JsonProperty("type")
            String type;
            @JsonProperty("uri")
            String uri;

            public ResourceReference() {
                this.type = "ref/resource";
            }

            public ResourceReference(String uri) {
                this.type = "ref/resource";
                this.uri = uri;
            }

            @Override
            public String getIdentifier() {
                return getUri();
            }
        }

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data
        public static class CompleteRequest implements Request {
            @JsonProperty("ref")
            CompleteReference ref;
            @JsonProperty("argument")
            CompleteArgument argument;

            @JsonInclude(JsonInclude.Include.NON_ABSENT)
            @JsonIgnoreProperties(ignoreUnknown = true)
            @Data
            public static class CompleteArgument {
                @JsonProperty("name")
                String name;
                @JsonProperty("value")
                String value;
            }
        }

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data
        public static class CompleteResult {
            CompleteCompletion completion;

            public static class CompleteCompletion {
                @JsonProperty("values")
                List<String> values;
                @JsonProperty("total")
                Integer total;
                @JsonProperty("hasMore")
                Boolean hasMore;
            }
        }
    }

    // ---------------------------
    // Content Types
    // ---------------------------
    public interface Content {

        default String type() {
            if (this instanceof TextContent) {
                return "text";
            }
            else if (this instanceof ImageContent) {
                return "image";
            }
            else if (this instanceof EmbeddedResource) {
                return "resource";
            }
            throw new IllegalArgumentException("Unknown content type: " + this);
        }

    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class TextContent implements Content {
        @JsonProperty("audience")
        List<Role> audience;
        @JsonProperty("priority")
        Double priority;
        @JsonProperty("text")
        String text;

        public TextContent() {
        }

        public TextContent(String content) {
            text = content;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ImageContent implements Content {
        @JsonProperty("audience")
        List<Role> audience;
        @JsonProperty("priority")
        Double priority;
        @JsonProperty("data")
        String data;
        @JsonProperty("mimeType")
        String mimeType;
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class EmbeddedResource implements Content {
        @JsonProperty("audience")
        List<Role> audience;
        @JsonProperty("priority")
        Double priority;
        @JsonProperty("resource")
        ResourceContents resource;
    }

    // ---------------------------
    // Roots
    // ---------------------------

    /**
     * Represents a root directory or file that the server can operate on.
     *
     * @field uri The URI identifying the root. This *must* start with file:// for now.
     * This restriction may be relaxed in future versions of the protocol to allow other
     * URI schemes.
     * @field name An optional name for the root. This can be used to provide a
     * human-readable identifier for the root, which may be useful for display purposes or
     * for referencing the root in other parts of the application.
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Root {
        @JsonProperty("uri")
        String uri;
        @JsonProperty("name")
        String name;
    }

    /**
     * The client's response to a roots/list request from the server. This result contains
     * an array of Root objects, each representing a root directory or file that the
     * server can operate on.
     *
     * @field roots An array of Root objects, each representing a root directory or file
     * that the server can operate on.
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ListRootsResult {
        @JsonProperty("roots")
        List<Root> roots;
    }
}
