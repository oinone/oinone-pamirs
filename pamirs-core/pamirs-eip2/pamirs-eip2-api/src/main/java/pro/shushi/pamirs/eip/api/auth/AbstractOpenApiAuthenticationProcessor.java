package pro.shushi.pamirs.eip.api.auth;

import org.apache.camel.ExtendedExchange;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.entity.openapi.OpenEipResult;

public abstract class AbstractOpenApiAuthenticationProcessor {

    protected String fetchString(IEipContext<SuperMap> context, ExtendedExchange exchange, String key, String defaultValue) {
        String value = StringHelper.valueOf(context.getInterfaceContextValue(key));
        if (StringUtils.isBlank(value)) {
            value = StringHelper.valueOf(exchange.getMessage().getHeader(key));
            if (StringUtils.isBlank(value)) {
                return defaultValue;
            }
        }
        return value;
    }

    protected String fetchString(IEipContext<SuperMap> context, ExtendedExchange exchange, String key, String errorCode, String errorMsg) {
        String value = StringHelper.valueOf(context.getInterfaceContextValue(key));
        if (StringUtils.isBlank(value)) {
            value = StringHelper.valueOf(exchange.getMessage().getHeader(key));
            if (StringUtils.isBlank(value)) {
                error(exchange, errorCode, errorMsg);
                return null;
            }
        }
        return value;
    }

    protected void error(ExtendedExchange exchange, String errorCode, String errorMsg) {
        exchange.getMessage().setBody(OpenEipResult.error(errorCode, errorMsg));
        exchange.setInterrupted(true);
    }
}
