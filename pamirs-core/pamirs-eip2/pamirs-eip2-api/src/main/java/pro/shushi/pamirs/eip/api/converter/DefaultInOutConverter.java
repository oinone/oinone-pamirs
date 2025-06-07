package pro.shushi.pamirs.eip.api.converter;

import com.alibaba.fastjson.JSON;
import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.eip.api.IEipInOutConverter;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.io.InputStream;

/**
 * 默认输入输出转换器
 *
 * @author Adamancy Zhang at 17:34 on 2024-04-25
 */
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
public class DefaultInOutConverter implements IEipInOutConverter {

    @Function.fun(EipFunctionConstant.DEFAULT_IN_OUT_CONVERTER_FUN)
    @Function.Advanced(displayName = "默认输入输出转换器")
    @Function(name = EipFunctionConstant.DEFAULT_IN_OUT_CONVERTER_FUN)
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
            outObject = JsonUtils.toJSONString(inObject);
        }
        return outObject;
    }
}
