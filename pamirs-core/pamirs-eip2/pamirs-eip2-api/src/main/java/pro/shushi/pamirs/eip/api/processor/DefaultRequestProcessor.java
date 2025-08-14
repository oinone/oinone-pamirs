package pro.shushi.pamirs.eip.api.processor;

import org.apache.camel.*;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.component.http.HttpEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.spi.Language;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.core.common.URLHelper;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.auth.basic.enumeration.EipBasicAuthParameter;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.extpoint.EipInterfaceUriSpi;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.eip.api.util.EipHelper;
import pro.shushi.pamirs.eip.api.util.EipLogUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class DefaultRequestProcessor extends AbstractEipIntegrationInterfaceProcessor<SuperMap> implements IEipProcessor<IEipIntegrationInterface<SuperMap>> {

    private final String pagingEnabledKey;

    private final String pagingSizeKey;

    private final String pagingStartPageKey;

    private final String pagingEndPageKey;

    private final String pagingCurrentPageKey;

    private final String pagingOffsetKey;

    private final String requestParamStoreKey;

    private final Expression expression;

    public DefaultRequestProcessor(IEipIntegrationInterface<SuperMap> eipInterface) {
        super(eipInterface);
        this.pagingEnabledKey = IEipContext.PAGING_PREFIX + eipInterface.getInterfaceName() + IEipContext.PAGING_ENABLED_SUFFIX;
        this.pagingSizeKey = IEipContext.PAGING_PREFIX + eipInterface.getInterfaceName() + IEipContext.PAGING_SIZE_SUFFIX;
        this.pagingStartPageKey = IEipContext.PAGING_PREFIX + eipInterface.getInterfaceName() + IEipContext.PAGING_START_PAGE_SUFFIX;
        this.pagingEndPageKey = IEipContext.PAGING_PREFIX + eipInterface.getInterfaceName() + IEipContext.PAGING_END_PAGE_SUFFIX;
        this.pagingCurrentPageKey = IEipContext.PAGING_PREFIX + eipInterface.getInterfaceName() + IEipContext.PAGING_CURRENT_PAGE_SUFFIX;
        this.pagingOffsetKey = IEipContext.PAGING_PREFIX + eipInterface.getInterfaceName() + IEipContext.PAGING_OFFSET_SUFFIX;
        this.requestParamStoreKey = IEipContext.REQUEST_STORE_PREFIX + eipInterface.getInterfaceName();
        if (eipInterface.getIsDynamic()) {
            expression = createExpression(eipInterface.getUri());
        } else {
            expression = null;
        }
    }

    @Override
    public void processor(ExtendedExchange exchange) throws Exception {
        IEipIntegrationInterface<SuperMap> integrationInterface = getApi();
        Message message = exchange.getMessage();
        Object body = message.getBody();

        // 获取执行器上下文
        IEipContext<SuperMap> context = EipInterfaceContext.getExecutorContext(exchange);

        // 获取请求参数处理器
        IEipParamProcessor<SuperMap> paramProcessor = integrationInterface.getRequestParamProcessor();

        // 序列化入参
        SuperMap interfaceContext;
        if (body instanceof SuperMap) {
            interfaceContext = (SuperMap) body;
        } else {
            interfaceContext = paramProcessor.getSerializable().serializable(body);
        }

        // 更新执行器上下文
        context = refreshExecutorContext(exchange, context, interfaceContext);

        // 生成请求执行日志
        EipLog eipLog = null;
        if (integrationInterface.getIsEnabledLog()) {
            // 预留请求参数日志，当发生异常时，该日志会进行存储，正常状态下，会重新创建
            eipLog = EipLogUtil.createEipLog(context, exchange);
        }

        // 获取认证处理器
        IEipAuthenticationProcessor<SuperMap> authenticationProcessor = paramProcessor.getAuthenticationProcessor();
        boolean needAuthentication = authenticationProcessor != null;

        // 请求认证
        if (needAuthentication && !authenticationProcessor.authentication(context, exchange)) {

            body = EipResult.error(context,
                    StringHelper.valueOf(context.getExecutorContextValue(IEipContext.DEFAULT_ERROR_CODE_KEY)),
                    StringHelper.valueOf(context.getExecutorContextValue(IEipContext.DEFAULT_ERROR_MESSAGE_KEY)),
                    exchange.getMessage().getBody());

            exchange.getMessage().setBody(body);

            if (eipLog != null) {
                EipLogUtil.failure(context, eipLog, exchange);
            }

            exchange.setInterrupted(Boolean.TRUE);

            return;
        }

        // 更新执行器上下文
        context = EipInterfaceContext.getExecutorContext(exchange);
        interfaceContext = context.getInterfaceContext();

        // 启用分页时存储入参
        Boolean isNeedPaging = (Boolean) context.getExecutorContextValue(pagingEnabledKey);
        // 首次请求时，补充分页启用参数
        if (isNeedPaging == null) {
            IEipPaging<SuperMap> paging = integrationInterface.getPaging();
            isNeedPaging = paging != null;
            if (isNeedPaging) {
                // 初始化分页参数
                initPaging(context, paging);
            }
        }
        if (isNeedPaging) {
            context.putExecutorContextValue(requestParamStoreKey, interfaceContext);
        } else {
            context.putExecutorContextValue(pagingEnabledKey, Boolean.FALSE);
        }

        // 参数转换
        EipHelper.paramConvert(context, paramProcessor, exchange);

        //增量处理
        IEipIncrementalProcessor<SuperMap> incrementalProcessor = integrationInterface.getIncrementalProcessor();
        if (incrementalProcessor != null) {
            EipHelper.incrementalProcess(context, incrementalProcessor, exchange);
        }

        // 更新执行器上下文
        context = EipInterfaceContext.getExecutorContext(exchange);
        interfaceContext = context.getInterfaceContext();

        // 交换对象属性处理
        exchangePropertiesParamProcessor(exchange, context);

        // url动态参数处理
        urlDynamicParamProcessor(exchange, context);

        // url查询参数处理
        urlQueryParamProcessor(exchange, context, isNeedPaging);

        // http请求头参数处理
        httpHeaderParamProcessor(exchange, context);

//        // 验签
//        if (needAuthentication) {
//            authenticationProcessor.signature(context, exchange);
//
//            context = EipInterfaceContext.getExecutorContext(exchange);
//            interfaceContext = context.getInterfaceContext();
//        }

        // 反序列化出参
        String finalResultKey = paramProcessor.getFinalResultKey();
        if (StringUtils.isBlank(finalResultKey)) {
            // 使用反序列化方式拿到最终结果
            body = paramProcessor.getDeserialization().deserialization(interfaceContext);
        } else {
            // 根据指定最终结果的键值拿到出参结果
            body = context.getInterfaceContextValue(finalResultKey);
        }

        // 最终出参转换
        body = paramProcessor.getInOutConverter().exchangeObject(exchange, body);

        if (log.isDebugEnabled()) {
            log.debug("集成接口请求入参:{}", JsonUtils.toJSONString(body));
        }

        // 设置出参
        message.setBody(body);

        if (eipLog != null) {
            // 更新真实请求数据日志
            EipLogUtil.updateRequestTargetData(eipLog, exchange);
        }
    }

    private void initPaging(IEipContext<SuperMap> context, IEipPaging<SuperMap> paging) {
        context.putExecutorContextValue(pagingEnabledKey, Boolean.TRUE);
        Integer startPage = paging.getStartPage();
        if (context.getExecutorContextValue(pagingSizeKey) == null) {
            context.putExecutorContextValue(pagingSizeKey, paging.getPageSize());
        }
        if (context.getExecutorContextValue(pagingStartPageKey) == null) {
            context.putExecutorContextValue(pagingStartPageKey, startPage);
        }
        if (context.getExecutorContextValue(pagingEndPageKey) == null) {
            context.putExecutorContextValue(pagingEndPageKey, paging.getEndPage());
        }
        if (context.getExecutorContextValue(pagingCurrentPageKey) == null) {
            context.putExecutorContextValue(pagingCurrentPageKey, startPage);
        }
        if (context.getExecutorContextValue(pagingOffsetKey) == null) {
            context.putExecutorContextValue(pagingOffsetKey, 0);
        }
    }

    @SuppressWarnings("unchecked")
    protected void exchangePropertiesParamProcessor(ExtendedExchange exchange, IEipContext<SuperMap> context) {
        Map<String, Object> exchangeProperties = (Map<String, Object>) context.getInterfaceContextValue(IEipContext.EXCHANGE_PROPERTY_KEY);
        if (exchangeProperties != null) {
            for (Map.Entry<String, Object> entry : exchangeProperties.entrySet()) {
                exchange.setProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void urlDynamicParamProcessor(Exchange exchange, IEipContext<SuperMap> context) {
        IEipIntegrationInterface<?> integrationInterface = getApi();
        String uri;
        if (integrationInterface.getIsDynamic()) {
            uri = expression.evaluate(exchange, String.class);
        } else {
            uri = integrationInterface.getUri();
            Map<String, Object> urlParams = (Map<String, Object>) context.getInterfaceContextValue(IEipContext.URL_DYNAMIC_PARAMS_KEY);
            if (urlParams != null) {
                uri = context.getApi().getUri();
                for (Map.Entry<String, Object> entry : urlParams.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (ObjectUtils.isEmpty(value)) {
                        log.error("path参数获取为空，key:{}", entry.getValue());
                    } else if (!(value instanceof String)) {
                        value = String.valueOf(value);
                    }
                    uri = uri.replaceAll("\\$\\{" + key + "}", (String) value);
                    uri = uri.replaceAll("\\{" + key + "}", (String) value);
                }
            }
        }
        if (StringUtils.isNotBlank(uri)) {
            uri = Spider.getDefaultExtension(EipInterfaceUriSpi.class).computeUri(uri, exchange, context);//扩展点
            URI httpUri = null;
            Endpoint endpoint = exchange.getContext().hasEndpoint(uri);
            if (endpoint == null) {
                try {
                    httpUri = new URI(Objects.requireNonNull(URLHelper.encode(uri)));
                } catch (URISyntaxException ignored) {
                }
            } else {
                if (endpoint instanceof HttpEndpoint) {
                    httpUri = ((HttpEndpoint) endpoint).getHttpUri();
                }
            }
            if (httpUri != null) {
                if (context.getInterfaceContextValue(EipBasicAuthParameter.SCHEMA.getTarget()) == null) {
                    context.putInterfaceContextValue(EipBasicAuthParameter.SCHEMA.getTarget(), httpUri.getScheme());
                }
                if (context.getInterfaceContextValue(EipBasicAuthParameter.HOST.getTarget()) == null) {
                    context.putInterfaceContextValue(EipBasicAuthParameter.HOST.getTarget(), httpUri.getHost());
                }
                if (context.getInterfaceContextValue(EipBasicAuthParameter.PORT.getTarget()) == null) {
                    context.putInterfaceContextValue(EipBasicAuthParameter.PORT.getTarget(), httpUri.getPort());
                }
                if (context.getInterfaceContextValue(EipBasicAuthParameter.PATH.getTarget()) == null) {
                    context.putInterfaceContextValue(EipBasicAuthParameter.PATH.getTarget(), httpUri.getPath());
                }
                exchange.getMessage().setHeader(Exchange.HTTP_URI, uri);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void urlQueryParamProcessor(Exchange exchange, IEipContext<SuperMap> context, Boolean isReplace) {
        Map<String, Object> urlParams = (Map<String, Object>) context.getInterfaceContextValue(IEipContext.URL_QUERY_PARAMS_KEY);
        if (urlParams != null) {
            StringBuilder httpQueryBuilder;
            if (isReplace) {
                httpQueryBuilder = new StringBuilder();
            } else {
                String currentHttpQuery = exchange.getMessage().getHeader(Exchange.HTTP_QUERY, String.class);
                if (StringUtils.isBlank(currentHttpQuery)) {
                    httpQueryBuilder = new StringBuilder();
                } else {
                    httpQueryBuilder = new StringBuilder(currentHttpQuery);
                }
            }
            String httpQuery = httpQueryBuilder.append(URLHelper.getRequestParameterString(urlParams))
                    .toString();
            if (StringUtils.isNotBlank(httpQuery)) {
                exchange.getMessage().setHeader(Exchange.HTTP_QUERY, httpQuery);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void httpHeaderParamProcessor(Exchange exchange, IEipContext<SuperMap> context) {
        Map<String, String> headers = (Map<String, String>) context.getInterfaceContextValue(IEipContext.HEADER_PARAMS_KEY);
        if (headers != null) {
            Message message = exchange.getMessage();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                message.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    private Expression createExpression(String uri) {
        List<Expression> list = new ArrayList<>();

        ModelCamelContext camelContext = getApi().getContext().getCamelContext();

        // make sure to parse property placeholders
        uri = camelContext.resolvePropertyPlaceholders(uri);

        String[] parts = EipHelper.safeSplitRaw(uri);
        for (String part : parts) {
            // the part may have optional language to use, so you can mix
            // languages
            String value = org.apache.camel.util.StringHelper.after(part, "language:");
            if (value != null) {
                String before = org.apache.camel.util.StringHelper.before(value, ":");
                String after = org.apache.camel.util.StringHelper.after(value, ":");
                if (before != null && after != null) {
                    // maybe its a language, must have language: as prefix
                    try {
                        Language partLanguage = camelContext.resolveLanguage(before);
                        if (partLanguage != null) {
                            Expression exp = partLanguage.createExpression(after);
                            list.add(exp);
                            continue;
                        }
                    } catch (NoSuchLanguageException e) {
                        // ignore
                    }
                }
            }

            // fallback and use simple language
            Language lan = camelContext.resolveLanguage("simple");
            Expression exp = lan.createExpression(part);
            list.add(exp);
        }

        Expression exp;
        if (list.size() == 1) {
            exp = list.get(0);
        } else {
            exp = ExpressionBuilder.concatExpression(list);
        }

        return exp;
    }
}
