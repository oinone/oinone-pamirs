package pro.shushi.pamirs.eip.api.converter;

import com.alibaba.fastjson.JSONObject;
import org.apache.camel.ExtendedExchange;
import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.eip.api.IEipInOutConverter;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.constant.EipSqlFunctionConstant;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
public class DefaultSqlInOutConverter implements IEipInOutConverter {

    @Function.fun(EipSqlFunctionConstant.DEFAULT_SQL_IN_OUT_CONVERTER_FUN)
    @Function.Advanced(displayName = "camel-sql默认输入输出转换器")
    @Function(name = EipSqlFunctionConstant.DEFAULT_SQL_IN_OUT_CONVERTER_FUN)
    @Override
    public Object exchangeObject(ExtendedExchange exchange, Object inObject) throws Exception {
        if (null == inObject) {
            return null;
        }
        if (inObject instanceof Map) {
            List<Object> outList = new ArrayList<>();
            Map<String, Object> outObjectMap = new HashMap<>();
            Map<String, Object> inObjectMap = JsonUtils.parseMap(JsonUtils.toJSONString(inObject));
            for (Map.Entry<String, Object> entry : inObjectMap.entrySet()) {
                if (entry.getValue() instanceof List) {
                    List<Object> list = (List<Object>) entry.getValue();
                    for (Object obj : list) {
                        if (obj instanceof JSONObject) {
                            Map<String, Object> map = JsonUtils.parseMap(JsonUtils.toJSONString(obj));
                            outList.add(map);
                        } else {
                            outObjectMap.put(entry.getKey(), list);
                            break;
                        }
                    }
                } else {
                    outObjectMap.put(entry.getKey(), entry.getValue());
                }
            }
            if (CollectionUtils.isNotEmpty(outList)) {
                return outList;
            } else if (0 != outObjectMap.size()) {
                return outObjectMap;
            }
        } else {
            return JsonUtils.parseMap(JsonUtils.toJSONString(inObject));
        }
        return inObject;
    }
}