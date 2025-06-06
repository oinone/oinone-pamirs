package pro.shushi.pamirs.eip.api.processor;

import org.apache.camel.Exchange;
import org.apache.camel.ExtendedExchange;
import org.apache.camel.Message;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.core.common.enmu.EncryptTypeEnum;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.auth.OpenApiConstant;
import pro.shushi.pamirs.eip.api.auth.PamirsTenantAuthenticationProcessor;
import pro.shushi.pamirs.eip.api.auth.api.OpenApiIpBlackCheckApi;
import pro.shushi.pamirs.eip.api.auth.api.OpenApiIpWhiteCheckApi;
import pro.shushi.pamirs.eip.api.config.PamirsEipOpenApiProperties;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.entity.openapi.OpenEipResult;
import pro.shushi.pamirs.eip.api.limiter.api.OpenRateLimitApi;
import pro.shushi.pamirs.eip.api.model.EipApplication;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.eip.api.util.EipHelper;
import pro.shushi.pamirs.eip.api.util.EipLogUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DefaultOpenInterfaceProcessor extends AbstractOpenInterfaceProcessor<SuperMap> implements IEipProcessor<IEipOpenInterface<SuperMap>> {

    public DefaultOpenInterfaceProcessor(IEipOpenInterface<SuperMap> openInterface) {
        super(openInterface);
    }

    @Override
    protected boolean useEipUserAuthentication() {
        return true;
    }

    @Override
    public void processor(ExtendedExchange exchange) throws Exception {
        IEipOpenInterface<SuperMap> openInterface = getApi();
        exchange.setProperty(OpenApiConstant.EIP_OPEN_INTERFACE, openInterface);

        EipApplication eipApplication = fetchEipApplication(exchange);

        // 请求预处理处理器
        requestDecryptProcessor(openInterface, eipApplication, exchange);

        Message message = exchange.getMessage();
        Object body = message.getBody();

        // 序列化入参
        SuperMap interfaceContext = openInterface.getSerializable().serializable(body);

        // 获取执行器上下文
        IEipContext<SuperMap> context = openInterface.getContextSupplier().get(openInterface, new SuperMap(), interfaceContext);

        if (eipApplication != null) {
            context.putExecutorContextValue(OpenApiConstant.OPEN_API_EIP_APPLICATION_KEY, eipApplication);
        }

        // 设置执行器上下文
        EipInterfaceContext.setExecutorContext(exchange, context);

        // 生成请求执行日志
        EipLog eipLog = null;
        if (openInterface.getIsEnabledLog()) {
            eipLog = EipLogUtil.createEipLog(context, exchange);
        }

        // url查询参数处理
        urlQueryParamProcessor(exchange, context);

        // http请求头参数处理
        httpHeaderParamProcessor(exchange, context);

        // 获取认证处理器
        IEipAuthenticationProcessor<SuperMap> authenticationProcessor = openInterface.getAuthenticationProcessor();
        boolean needAuthentication = authenticationProcessor != null;

        // 请求认证
        if (!BeanDefinitionUtils.getBean(PamirsTenantAuthenticationProcessor.class).authentication(context, exchange) || (needAuthentication && !authenticationProcessor.authentication(context, exchange))) {

            body = openInterface.getInOutConverter().exchangeObject(exchange, exchange.getMessage().getBody());

            message.setBody(body);

            if (eipLog != null) {
                EipLogUtil.failure(context, eipLog, exchange);
            }

            return;
        }

        // 重新获取上下文
        context = EipInterfaceContext.getExecutorContext(exchange);

        // 白名单校验
        Boolean whiteCheck = Spider.getDefaultExtension(OpenApiIpWhiteCheckApi.class).check(context, exchange);
        if (Boolean.FALSE.equals(whiteCheck)) {
            return;
        }

        // 黑名单校验
        Boolean blackCheck = Spider.getDefaultExtension(OpenApiIpBlackCheckApi.class).check(context, exchange);
        if (Boolean.FALSE.equals(blackCheck)) {
            return;
        }

        // 限流判断
        String interfaceName = openInterface.getInterfaceName();
        String appKey = (String) context.getExecutorContextValue(OpenApiConstant.OPEN_API_APP_KEY_KEY);
        if (StringUtils.isNotBlank(appKey)) {
            boolean checkRateLimit = Spider.getDefaultExtension(OpenRateLimitApi.class).tryAcquire(appKey, interfaceName);
            if (!checkRateLimit) {
                throw PamirsException.construct(EipExpEnumerate.EIP_RATE_LIMIT_TIP).errThrow();
            }
        }

        if (eipLog != null) {

            body = message.getBody();

            message.setBody(context.getInterfaceContext());

            // 更新真实请求数据日志
            EipLogUtil.updateRequestTargetData(eipLog, exchange);

            message.setBody(body);
        }


        // 获取请求参数处理器
        IEipOpenParamProcessor<SuperMap> requestParamProcessor = openInterface.getRequestParamProcessor();
        if (requestParamProcessor != null) {

            // 入参转换
            EipHelper.paramConvert(context, requestParamProcessor, exchange);

            // 更新执行器上下文
            context = EipInterfaceContext.getExecutorContext(exchange);

        }

        // 处理开放接口请求结果
        IEipConverter<SuperMap> converter = openInterface.getConverter();
        if (converter == null) {
            // 无请求处理时，提供统一的默认返回值
            body = OpenEipResult.success(null);
        } else {
            // 有请求处理时，进行处理
            converter.convert(context, exchange);

            // 获取响应参数处理器
            IEipOpenParamProcessor<SuperMap> responseParamProcessor = openInterface.getResponseParamProcessor();
            if (responseParamProcessor != null) {

                // 出参转换
                EipHelper.paramConvert(context, responseParamProcessor, exchange);

                // 更新执行器上下文
                context = EipInterfaceContext.getExecutorContext(exchange);

            }

            // 获取最终结果键值
            String finalResultKey = openInterface.getFinalResultKey();
            if (StringUtils.isNotBlank(finalResultKey)) {

                // 根据指定最终结果的键值拿到出参结果
                body = context.getInterfaceContextValue(finalResultKey);
            }
        }

        // 最终出参转换
        body = openInterface.getInOutConverter().exchangeObject(exchange, body);

        // 响应预处理处理器
        body = responseEncryptionProcessor(exchange, openInterface, body, context);

        // 设置出参
        message.setBody(body);

        if (eipLog != null) {
            EipLogUtil.updateResponseData(eipLog, exchange);
            EipLogUtil.success(context, eipLog);
        }
    }

    /**
     * 响应预处理处理器,优先使用接口的响应处理器
     */
    private Object responseEncryptionProcessor(ExtendedExchange exchange, IEipOpenInterface<SuperMap> openInterface,
                                               Object body, IEipContext<SuperMap> context) {
        EipApplication eipApplication = (EipApplication) context.getExecutorContextValue(OpenApiConstant.OPEN_API_EIP_APPLICATION_KEY);
        IEipEncryptionProcessor responseEncryptionProcessor = openInterface.getResponseEncryptionProcessor();
        if (responseEncryptionProcessor != null) {
            body = responseEncryptionProcessor.processor(exchange, body);
        } else if (eipApplication != null && eipApplication.getResponseEncryptionProcessor() != null) {
            body = eipApplication.getResponseEncryptionProcessor().processor(exchange, body);
        }
        return body;
    }

    /**
     * 请求预处理处理,优先使用接口的解密处理器
     */
    private void requestDecryptProcessor(IEipOpenInterface<SuperMap> openInterface,
                                         EipApplication eipApplication, ExtendedExchange exchange) {
        IEipDecryptProcessor requestDecryptProcessor = openInterface.getRequestDecryptProcessor();
        if (requestDecryptProcessor != null) {
            requestDecryptProcessor.processor(exchange);
        } else if (eipApplication != null && eipApplication.getRequestDecryptProcessor() != null) {
            eipApplication.getRequestDecryptProcessor().processor(exchange);
        }
    }

    @SuppressWarnings("unchecked")
    protected void urlQueryParamProcessor(Exchange exchange, IEipContext<SuperMap> context) {
        String httpQuery = exchange.getMessage().getHeader(Exchange.HTTP_QUERY, String.class);
        if (StringUtils.isEmpty(httpQuery)) {
            return;
        }
        // FIXME: zbh 20240312 此处应放在执行器上下文，不在接口上下文传递
        Map<String, Object> urlParams = (Map<String, Object>) context.getInterfaceContextValue(IEipContext.URL_QUERY_PARAMS_KEY);
        if (urlParams == null) {
            urlParams = new SuperMap();
            context.putInterfaceContextValue(IEipContext.URL_QUERY_PARAMS_KEY, urlParams);
        }
        Map<String, Object> urlQueryParams = (Map<String, Object>) context.getExecutorContextValue(IEipContext.URL_QUERY_PARAMS_KEY);
        if (urlQueryParams == null) {
            urlQueryParams = new SuperMap();
            context.putExecutorContextValue(IEipContext.URL_QUERY_PARAMS_KEY, urlQueryParams);
        }
        List<NameValuePair> nameValuePairs = URLEncodedUtils.parse(httpQuery, StandardCharsets.UTF_8);
        for (NameValuePair nameValuePair : nameValuePairs) {
            String key = nameValuePair.getName();
            String value = nameValuePair.getValue();
            urlParams.put(key, value);
            urlQueryParams.put(key, value);
        }
    }

    @SuppressWarnings("unchecked")
    private void httpHeaderParamProcessor(ExtendedExchange exchange, IEipContext<SuperMap> context) {
        Map<String, Object> headers = exchange.getMessage().getHeaders();
        if (MapUtils.isEmpty(headers)) {
            return;
        }
        Map<String, String> headerParameters = (Map<String, String>) context.getExecutorContextValue(IEipContext.HEADER_PARAMS_KEY);
        if (headerParameters == null) {
            headerParameters = new HashMap<>(headers.size());
            context.putExecutorContextValue(IEipContext.HEADER_PARAMS_KEY, headerParameters);
        }
        Map<String, String> dynamicParameters = (Map<String, String>) context.getExecutorContextValue(IEipContext.URL_DYNAMIC_PARAMS_KEY);
        if (dynamicParameters == null) {
            dynamicParameters = new HashMap<>(headers.size());
            context.putExecutorContextValue(IEipContext.URL_DYNAMIC_PARAMS_KEY, dynamicParameters);
        }
        for (Map.Entry<String, Object> headerEntry : headers.entrySet()) {
            Object value = headerEntry.getValue();
            if (value instanceof String) {
                String key = headerEntry.getKey();
                String stringValue = (String) value;
                headerParameters.put(key, stringValue);
                dynamicParameters.put(key, stringValue);
            }
        }
    }

    private EipApplication fetchEipApplication(ExtendedExchange exchange) {
        String token = (String) exchange.getMessage().getHeader(OpenApiConstant.ACCESS_TOKEN_KEY);
        if (token == null) {
            return null;
        }
        try {
            PamirsEipOpenApiProperties openApiProperties = BeanDefinitionUtils.getBean(PamirsEipOpenApiProperties.class);
            Key secret = EncryptHelper.getSecretKeySpec(EncryptTypeEnum.AES.getValue(), openApiProperties.getRoute().getAesKey());
            String decryptToken = EncryptHelper.decryptByKey(secret, token);
            String appKey = decryptToken.substring(0, 32);
            EipApplication eipApplication = new EipApplication().setAppKey(appKey).queryOne();
            if (eipApplication != null) {
                eipApplication.fieldQuery(EipApplication::getIpBlackList);
                exchange.setProperty(OpenApiConstant.EIP_APPLICATION_KEY, eipApplication);
            }
            return eipApplication;
        } catch (Exception e) {
            return null;
        }
    }
}
