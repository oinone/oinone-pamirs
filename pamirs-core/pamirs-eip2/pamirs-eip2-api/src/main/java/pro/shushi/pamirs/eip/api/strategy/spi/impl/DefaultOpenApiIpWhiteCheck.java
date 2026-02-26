package pro.shushi.pamirs.eip.api.strategy.spi.impl;

import com.google.common.net.HttpHeaders;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedExchange;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.auth.OpenApiConstant;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.model.EipApplication;
import pro.shushi.pamirs.eip.api.strategy.spi.OpenApiIpWhiteCheckApi;
import pro.shushi.pamirs.eip.api.util.EipIpUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.net.UnknownHostException;
import java.util.Objects;

/**
 * @author yeshenyue on 2025/4/11 19:38.
 */
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultOpenApiIpWhiteCheck implements OpenApiIpWhiteCheckApi {

    private static final String DEFAULT_ERROR_RESP_MSG = "Access Denied";
    private static final Integer DEFAULT_ERROR_RESP_CODE = 403;

    @Override
    public Boolean check(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        EipApplication application = (EipApplication) context.getExecutorContextValue(OpenApiConstant.OPEN_API_EIP_APPLICATION_KEY);
        if (application == null) {
            return true;
        }

        String ipWhiteList = application.getIpWhiteList();
        Integer errorCode = application.getIpWhiteRespHttpCode() == null ? DEFAULT_ERROR_RESP_CODE : application.getIpWhiteRespHttpCode();
        String errorBody = Objects.toString(application.getIpWhiteHttpResult(), DEFAULT_ERROR_RESP_MSG);
        if (StringUtils.isBlank(ipWhiteList) || ipWhiteList.contains("0.0.0.0/0")) {
            return Boolean.TRUE;
        }

        String readIp = exchange.getIn().getHeader(HttpHeaders.X_FORWARDED_FOR).toString();
        if (StringUtils.isBlank(readIp)) {
            throw PamirsException.construct(EipExpEnumerate.EIP_IP_NULL_ERROR).errThrow();
        }

        String[] ipWhites = ipWhiteList.split(",");
        try {
            boolean ipAllowed = EipIpUtil.isIpAllowed(readIp, ipWhites);
            if (!ipAllowed) {
                log.warn("IP白名单拦截:{}", readIp);
                exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, errorCode);
                exchange.getMessage().setBody(errorBody);
                return Boolean.FALSE;
            }
        } catch (UnknownHostException e) {
            throw PamirsException.construct(EipExpEnumerate.EIP_IP_ERROR).errThrow();
        }
        return Boolean.TRUE;
    }
}
