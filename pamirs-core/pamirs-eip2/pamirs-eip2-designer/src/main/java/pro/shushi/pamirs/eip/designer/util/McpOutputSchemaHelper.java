package pro.shushi.pamirs.eip.designer.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.eip.designer.model.open.EipOpenRespParam;
import pro.shushi.pamirs.eip.mcp.api.enums.McpParamTypeEnum;
import pro.shushi.pamirs.eip.mcp.api.protocol.McpSchema;
import pro.shushi.pamirs.eip.mcp.api.utils.schema.AbstractMcpSchemaNode;
import pro.shushi.pamirs.eip.mcp.api.utils.schema.McpSchemaArrayNode;
import pro.shushi.pamirs.eip.mcp.api.utils.schema.McpSchemaObjectNode;
import pro.shushi.pamirs.eip.mcp.api.utils.schema.McpSchemaSimpleNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * mcp outputSchema 构建工具类
 *
 * @author Gesi at 18:16 on 2025/8/12
 */
public class McpOutputSchemaHelper {

    public static McpSchema.JsonSchema node2JsonSchema(final McpSchemaObjectNode outputSchemaNode) {
        return new ObjectMapper().convertValue(outputSchemaNode.getSchemaObject(), McpSchema.JsonSchema.class);
    }

    public static McpSchemaObjectNode respBody2InputSchemaNode(List<EipOpenRespParam> resp) {
        McpSchemaObjectNode inputSchema = new McpSchemaObjectNode();
        inputSchema.setKey("outputSchema");
        inputSchema.setRequired(true);
        inputSchema.setParamType(McpParamTypeEnum.OBJECT);
        inputSchema.setChildren(new ArrayList<>());

        for (EipOpenRespParam param : resp) {
            inputSchema.getChildren().add(respBody2SchemaNode(param));
        }

        return inputSchema;
    }

    public static List<AbstractMcpSchemaNode> respBody2SchemaNode(List<EipOpenRespParam> paramList) {
        if (CollectionUtils.isEmpty(paramList)) {
            return new ArrayList<>();
        }
        List<AbstractMcpSchemaNode> result = new ArrayList<>(paramList.size());
        for (EipOpenRespParam param : paramList) {
            result.add(respBody2SchemaNode(param));
        }
        return result;
    }

    public static AbstractMcpSchemaNode respBody2SchemaNode(EipOpenRespParam param) {
        boolean isObject = ParamTypeEnum.OBJECT.equals(param.getParamType());
        if (Boolean.TRUE.equals(param.getIsMulti())) {
            return new McpSchemaArrayNode()
                    .setKey(param.getKey())
                    .setDesc(param.getDesc())
                    .setParamType(McpParamTypeEnum.ARRAY)
                    .setValueExpr(param.getValueExpr())
                    .setChildren(Collections.singletonList(isObject ?
                            new McpSchemaObjectNode()
                                    .setDesc(param.getDesc())
                                    .setParamType(McpParamTypeEnum.OBJECT)
                                    .setChildren(respBody2SchemaNode(param.getChildren()))
                            :
                            new McpSchemaSimpleNode()
                                    .setDesc(param.getDesc())
                                    .setParamType(McpParamTypeEnum.fetchByParamType(param.getParamType()))
                    ));
        } else if (isObject) {
            return new McpSchemaObjectNode()
                    .setKey(param.getKey())
                    .setDesc(param.getDesc())
                    .setValueExpr(param.getValueExpr())
                    .setParamType(McpParamTypeEnum.OBJECT)
                    .setChildren(respBody2SchemaNode(param.getChildren()));
        } else {
            return new McpSchemaSimpleNode()
                    .setKey(param.getKey())
                    .setDesc(param.getDesc())
                    .setParamType(McpParamTypeEnum.fetchByParamType(param.getParamType()))
                    .setValueExpr(param.getValueExpr());
        }
    }

}
