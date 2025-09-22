package pro.shushi.pamirs.eip.api.converter;

import org.apache.camel.ExtendedExchange;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.eip.api.IEipInOutConverter;
import pro.shushi.pamirs.eip.api.constant.EipCharacterConstant;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Adamancy Zhang on 2021-02-06 13:57
 */
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
public class DefaultFormUrlencodedConverter extends DefaultInOutConverter implements IEipInOutConverter {

    @SuppressWarnings("unchecked")
    @Function.fun(EipFunctionConstant.DEFAULT_FORM_URLENCODED_IN_OUT_CONVERTER_FUN)
    @Function.Advanced(displayName = "默认application/x-www-form-urlencoded输入输出转换器")
    @Function(name = EipFunctionConstant.DEFAULT_FORM_URLENCODED_IN_OUT_CONVERTER_FUN)
    @Override
    public Object exchangeObject(ExtendedExchange exchange, Object inObject) throws Exception {
        // 设置CONTENT_TYPE 为 application/x-www-form-urlencoded
        exchange.getIn().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=" + StandardCharsets.UTF_8.name());

        if (inObject instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) inObject;
            if (MapUtils.isEmpty(map)) {
                return null;
            }

            StringBuilder formData = new StringBuilder();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String value = StringHelper.valueOf(entry.getValue());
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                if (formData.length() != 0) {
                    formData.append(EipCharacterConstant.PARAMETER_VALUE_CONNECTOR);
                }
                formData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()))
                        .append(EipCharacterConstant.PARAMETER_VALUE_SEPARATOR)
                        .append(URLEncoder.encode(value, StandardCharsets.UTF_8.name()));
            }

            // 直接返回 InputStream，解决 Camel 类型转换问题：
            // 1、如果返回的是Map，Camel会尝试将其序列化为JSON（默认行为），而不是form-urlencoded 格式
            // 2、返回ByteArrayInputStream 即直接提供字节流，避免中间转换
            return new ByteArrayInputStream(formData.toString().getBytes(StandardCharsets.UTF_8));
        } else {
            return super.exchangeObject(exchange, inObject);
        }
    }
}
