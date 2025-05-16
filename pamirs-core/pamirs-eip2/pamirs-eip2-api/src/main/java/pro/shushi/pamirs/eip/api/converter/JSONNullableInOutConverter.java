package pro.shushi.pamirs.eip.api.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.eip.api.IEipInOutConverter;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.io.InputStream;

/**
 * JSON输入输出转换器-允许空值
 *
 * @author Adamancy Zhang at 17:27 on 2024-04-25
 * @author haibo(xf.z @ shushi.pro)
 */
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
public class JSONNullableInOutConverter implements IEipInOutConverter {

    private static final String FUN = EipFunctionConstant.IN_OUT_CONVERTER_PREFIX + "jsonNullableInOutConverter";

    @Function.fun(FUN)
    @Function.Advanced(displayName = "JSON转换器-允许空值", category = FunctionCategoryEnum.EIP_IN_OUT)
    @Function(name = FUN, openLevel = {FunctionOpenEnum.API, FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    @Override
    public Object exchangeObject(ExtendedExchange exchange, Object inObject) throws Exception {
        if (inObject == null) {
            return null;
        }
        String outObject;
        if (inObject instanceof InputStream) {
            outObject = JSON.parseObject((InputStream) inObject, String.class);
        } else if (inObject instanceof String) {
            outObject = (String) inObject;
        } else {
            outObject = JsonUtils.toJSONString(inObject, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteMapNullValue);
        }
        return outObject;
    }
}
