package pro.shushi.pamirs.eip.api.auth;

import org.apache.camel.ExtendedExchange;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipAuthenticationProcessor;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.config.EipOpenApiSwitchCondition;
import pro.shushi.pamirs.eip.api.model.EipApplication;
import pro.shushi.pamirs.eip.api.util.EipSignUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Adamancy Zhang
 * @date 2021-01-05 10:22
 */
@Slf4j
@Component
@Conditional(EipOpenApiSwitchCondition.class)
public class OpenApiMD5SignatureProcessor extends AbstractOpenApiAuthenticationProcessor implements IEipAuthenticationProcessor<SuperMap> {

    @Override
    public boolean authentication(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        SuperMap map = context.getInterfaceContext();
        String signatureMethod = fetchString(context, exchange, OpenApiConstant.SIGNATURE_METHOD_KEY, EipSignUtils.SIGN_METHOD_MD5);
        String signature = fetchString(context, exchange, OpenApiConstant.SIGNATURE_KEY, "600001", "无法获取signature签名参数");
        if (signature == null) {
            return false;
        }
        Map<String, String> params = new HashMap<>(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                params.put(key, (String) value);
            }
        }
        EipApplication application = (EipApplication) context.getExecutorContextValue(OpenApiConstant.OPEN_API_EIP_APPLICATION_KEY);
        try {
            if (signature.equals(EipSignUtils.signTopRequest(params, application.getAppKey(), signatureMethod))) {
                return true;
            } else {
                error(exchange, "600002", "参数签名不匹配");
                return false;
            }
        } catch (IOException e) {
            error(exchange, "600003", "参数签名处理异常");
            log.error("开放接口数据验签异常", e);
            return false;
        }
    }
}
