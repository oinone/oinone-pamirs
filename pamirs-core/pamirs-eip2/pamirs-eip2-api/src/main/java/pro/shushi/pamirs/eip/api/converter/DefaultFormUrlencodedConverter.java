package pro.shushi.pamirs.eip.api.converter;

import org.apache.camel.ExtendedExchange;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.eip.api.IEipInOutConverter;
import pro.shushi.pamirs.eip.api.constant.EipCharacterConstant;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.Map;

/**
 * @author Adamancy Zhang on 2021-02-06 13:57
 */
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
public class DefaultFormUrlencodedConverter extends DefaultInOutConverter implements IEipInOutConverter {

    @SuppressWarnings("unchecked")
    @Function.fun(EipFunctionConstant.DEFAULT_FORM_URLENCODED_IN_OUT_CONVERTER_FUN)
    @Function.Advanced(displayName = "默认form-data/x-www-form-urlencoded输入输出转换器")
    @Function(name = EipFunctionConstant.DEFAULT_FORM_URLENCODED_IN_OUT_CONVERTER_FUN)
    @Override
    public Object exchangeObject(ExtendedExchange exchange, Object inObject) throws Exception {
        if (inObject instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) inObject;
            StringBuilder builder = new StringBuilder();
            if (MapUtils.isNotEmpty(map)) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String value = StringHelper.valueOf(entry.getValue());
                    if (StringUtils.isBlank(value)) {
                        continue;
                    }
                    if (builder.length() != 0) {
                        builder.append(EipCharacterConstant.PARAMETER_VALUE_CONNECTOR);
                    }
                    builder.append(entry.getKey()).append(EipCharacterConstant.PARAMETER_VALUE_SEPARATOR).append(value);
                }
            } else {
                return null;
            }
            return builder.toString();
        } else {
            return super.exchangeObject(exchange, inObject);
        }
    }
}
