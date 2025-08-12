package pro.shushi.pamirs.eip.designer.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.eip.designer.model.open.EipOpenReqBodyParam;
import pro.shushi.pamirs.eip.mcp.api.enums.McpParamTypeEnum;
import pro.shushi.pamirs.eip.mcp.api.protocol.McpSchema;
import pro.shushi.pamirs.eip.mcp.api.utils.schema.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * mcp inputSchema 构建工具类
 *
 * @author Gesi at 18:16 on 2025/8/12
 */
public class McpInputSchemaHelper {

    public static McpSchema.JsonSchema node2JsonSchema(final McpSchemaObjectNode inputSchemaNode) {
        return new ObjectMapper().convertValue(inputSchemaNode.getSchemaObject(), McpSchema.JsonSchema.class);
    }

    public static McpSchemaObjectNode reqBody2InputSchemaNode(List<EipOpenReqBodyParam> req) {
        McpSchemaObjectNode inputSchema = new McpSchemaObjectNode();
        inputSchema.setKey("inputSchema");
        inputSchema.setRequired(true);
        inputSchema.setParamType(McpParamTypeEnum.OBJECT);
        inputSchema.setChildren(new ArrayList<>());

        for (EipOpenReqBodyParam param : req) {
            inputSchema.getChildren().add(reqBody2SchemaNode(param));
        }

        return inputSchema;
    }

    public static List<AbstractMcpSchemaNode> reqBody2SchemaNode(List<EipOpenReqBodyParam> paramList) {
        if (CollectionUtils.isEmpty(paramList)) {
            return new ArrayList<>();
        }
        List<AbstractMcpSchemaNode> result = new ArrayList<>(paramList.size());
        for (EipOpenReqBodyParam param : paramList) {
            result.add(reqBody2SchemaNode(param));
        }
        return result;
    }

    public static AbstractMcpSchemaNode reqBody2SchemaNode(EipOpenReqBodyParam param) {
        boolean isObject = ParamTypeEnum.OBJECT.equals(param.getParamType());
        if (Boolean.TRUE.equals(param.getIsMulti())) {
            return new McpSchemaArrayNode()
                    .setKey(param.getKey())
                    .setRequired(param.getRequired())
                    .setDesc(param.getDesc())
                    .setDefaultValue(param.getDefaultValue())
                    .setParamType(McpParamTypeEnum.ARRAY)
                    .setValueExpr(param.getValueExpr())
                    .setChildren(Collections.singletonList(isObject ?
                            new McpSchemaObjectNode()
                                    .setDesc(param.getDesc())
                                    .setParamType(McpParamTypeEnum.OBJECT)
                                    .setChildren(reqBody2SchemaNode(param.getChildren()))
                            :
                            new McpSchemaSimpleNode()
                                    .setDesc(param.getDesc())
                                    .setParamType(McpParamTypeEnum.fetchByParamType(param.getParamType()))
                    ));
        } else if (isObject) {
            return new McpSchemaObjectNode()
                    .setKey(param.getKey())
                    .setRequired(param.getRequired())
                    .setDesc(param.getDesc())
                    .setValueExpr(param.getValueExpr())
                    .setDefaultValue(param.getDefaultValue())
                    .setParamType(McpParamTypeEnum.OBJECT)
                    .setChildren(reqBody2SchemaNode(param.getChildren()));
        } else {
            return new McpSchemaSimpleNode()
                    .setKey(param.getKey())
                    .setDesc(param.getDesc())
                    .setParamType(McpParamTypeEnum.fetchByParamType(param.getParamType()))
                    .setRequired(param.getRequired())
                    .setValueExpr(param.getValueExpr())
                    .setDefaultValue(param.getDefaultValue());
        }
    }

}
