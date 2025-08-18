package pro.shushi.pamirs.eip.api.strategy.spi.impl;

import org.apache.camel.Exchange;
import org.apache.camel.ExtendedExchange;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.auth.OpenApiConstant;
import pro.shushi.pamirs.eip.api.strategy.spi.OpenApiIpBlackCheckApi;
import pro.shushi.pamirs.eip.api.model.EipApplication;
import pro.shushi.pamirs.eip.api.model.strategy.EipOpenIpBlacklist;
import pro.shushi.pamirs.eip.api.util.EipIpUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate.EIP_X_READ_IP_NULL;

/**
 * @author yeshenyue on 2025/5/12 10:58.
 */
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultOpenApiIpBlackCheck implements OpenApiIpBlackCheckApi {

    protected static final String READ_IP_KEY = "X-Real-IP";
    protected static final String DEFAULT_ERROR_RESP_MSG = "Access Denied";
    protected static final Integer DEFAULT_ERROR_RESP_CODE = 403;

    @Override
    public Boolean check(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        EipApplication application = (EipApplication) context.getExecutorContextValue(OpenApiConstant.OPEN_API_EIP_APPLICATION_KEY);
        if (application == null) {
            return true;
        }
        List<EipOpenIpBlacklist> ipBlackList = application.getIpBlackList();
        if (CollectionUtils.isEmpty(ipBlackList)) {
            return true;
        }

        String readIp = (String) exchange.getIn().getHeader(READ_IP_KEY);
        if (StringUtils.isBlank(readIp)) {
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, DEFAULT_ERROR_RESP_CODE);
            exchange.getMessage().setBody(EIP_X_READ_IP_NULL.getMsg());
            return false;
        }

        try {
            InetAddress address = InetAddress.getByName(readIp);
            byte[] addressBytes = address.getAddress();
            for (EipOpenIpBlacklist black : ipBlackList) {
                String illegalIp = black.getIp();
                if (StringUtils.isBlank(illegalIp)) {
                    continue;
                }

                if (readIp.equals(illegalIp)) {
                    buildResponse(exchange, black, readIp);
                    return false;
                }

                if (illegalIp.contains("/") && EipIpUtil.isInRange(addressBytes, illegalIp)) {
                    buildResponse(exchange, black, readIp);
                    return false;
                }
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private void buildResponse(ExtendedExchange exchange, EipOpenIpBlacklist black, String readIp) {
        Integer httpCode = black.getHttpCode() == null ? DEFAULT_ERROR_RESP_CODE : black.getHttpCode();
        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, httpCode);

        String result = black.getHttpResult();
        exchange.getMessage().setBody(StringUtils.isNotBlank(result) ? result : DEFAULT_ERROR_RESP_MSG);
        log.warn("黑名单,readIp:{}", readIp);
    }
}
