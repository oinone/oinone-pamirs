package pro.shushi.pamirs.eip.api.serializable;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipDeserialization;
import pro.shushi.pamirs.eip.api.IEipSerializable;
import pro.shushi.pamirs.eip.api.constant.EipContextConstant;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
@Component
public class DefaultStringSerializable extends AbstractSerializable implements IEipSerializable<SuperMap>, IEipDeserialization<SuperMap> {

    @Function.fun(EipFunctionConstant.DEFAULT_STRING_SERIALIZABLE_FUN)
    @Function.Advanced(displayName = "默认JSON序列化")
    @Function(name = EipFunctionConstant.DEFAULT_STRING_SERIALIZABLE_FUN)
    @Override
    public SuperMap serializable(Object inObject) {
        return super.serializable(inObject);
    }

    @Function.fun(EipFunctionConstant.DEFAULT_STRING_DESERIALIZATION_FUN)
    @Function.Advanced(displayName = "默认JSON序列化")
    @Function(name = EipFunctionConstant.DEFAULT_STRING_DESERIALIZATION_FUN)
    @Override
    public Object deserialization(SuperMap outObject) {
        return super.deserialization(outObject);
    }

    @Override
    protected List<?> listSerializableList(List<?> list) {
        return JSON.parseArray(JSON.toJSONString(list));
    }

    @Override
    protected SuperMap stringToSuperMap(String inObjectString) {
        SuperMap superMap = new SuperMap();
        superMap.put(EipContextConstant.RESULT_KEY, inObjectString);
        return superMap;
    }

}
