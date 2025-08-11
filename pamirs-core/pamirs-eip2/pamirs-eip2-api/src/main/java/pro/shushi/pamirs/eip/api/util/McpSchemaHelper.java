package pro.shushi.pamirs.eip.api.util;

/*
接口入参 -> inputSchema
接口出差 -> outputSchema

data
 name
 depot

{
  "data": {
    "type": "object",
    "description": "xxx",
    "properties": {
      "name": {
        "type": "string",
        "description": "xxx"
      },
      "age": {
        "type": "number",
        "description": "xxx"
      },
      "require": ["name"]
    }
  }
}

{
  "data": {
    "type": "array",
    "description": "xxx",
    "items": {
      "data": {
        "name": {
          "type": "string",
          "description": "xxx"
        },
        "age": {
          "type": "number",
          "description": "xxx"
        },
        "require": ["name"]
        }
    }
  }
}

用平台默认的入参出参转换器来构建对象
入参转换器对应 智能体输入(SuperMap arguments) -> 函数入参
出参转换器对应 函数返回值 -> content 第一个 TextContent 的 text 为json对象，outputSchema用工具内解析的，data用转换结果
*/
/**
 * todo
 * 根据参数声明转换成mcp的参数schema
 * @link Mcp.JsonSchema
 *
 * @author Gesi at 10:12 on 2025/8/8
 */
public class McpSchemaHelper {
}
