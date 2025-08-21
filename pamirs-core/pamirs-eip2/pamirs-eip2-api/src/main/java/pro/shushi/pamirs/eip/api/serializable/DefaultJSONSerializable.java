package pro.shushi.pamirs.eip.api.serializable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONValidator;
import com.google.common.primitives.Primitives;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipDeserialization;
import pro.shushi.pamirs.eip.api.IEipSerializable;
import pro.shushi.pamirs.eip.api.constant.EipContextConstant;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.List;

@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
@Component
public class DefaultJSONSerializable extends AbstractSerializable implements IEipSerializable<SuperMap>, IEipDeserialization<SuperMap> {

    @Function.fun(EipFunctionConstant.DEFAULT_JSON_SERIALIZABLE_FUN)
    @Function.Advanced(displayName = "默认JSON序列化")
    @Function(name = EipFunctionConstant.DEFAULT_JSON_SERIALIZABLE_FUN)
    @Override
    public SuperMap serializable(Object inObject) {
        return super.serializable(inObject);
    }

    @Function.fun(EipFunctionConstant.DEFAULT_JSON_DESERIALIZATION_FUN)
    @Function.Advanced(displayName = "默认JSON反序列化")
    @Function(name = EipFunctionConstant.DEFAULT_JSON_DESERIALIZATION_FUN)
    @Override
    public Object deserialization(SuperMap outObject) {
        return super.deserialization(outObject);
    }

    @Override
    protected List<?> listSerializableList(List<?> list) {
        Class<?> serializableClass = null;
        for (Object item : list) {
            if (item != null) {
                serializableClass = item.getClass();
                break;
            }
        }
        if (serializableClass == null) {
            return list;
        }
        if (SuperMap.class.isAssignableFrom(serializableClass)) {
            return list;
        }
        if (!Primitives.isWrapperType(serializableClass)) {
            serializableClass = SuperMap.class;
        }
        return JSON.parseArray(JSON.toJSONString(list), serializableClass);
    }

    @Override
    protected SuperMap stringToSuperMap(String s) {
        JSONValidator.Type jsonType = JsonUtils.validateJSONType(s);
        SuperMap result;
        if (JSONValidator.Type.Array.equals(jsonType)) {
            result = new SuperMap();
            result.put(EipContextConstant.LIST_KEY, JSON.parseArray(s, SuperMap.class));
        } else if (JSONValidator.Type.Object.equals(jsonType)) {
            result = JSON.parseObject(s, SuperMap.class);
        } else {
            result = new SuperMap();
            result.put(EipContextConstant.RESULT_KEY, s);
        }
        return result;
    }

}
