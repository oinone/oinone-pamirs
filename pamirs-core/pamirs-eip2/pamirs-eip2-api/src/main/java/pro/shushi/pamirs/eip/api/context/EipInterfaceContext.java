package pro.shushi.pamirs.eip.api.context;

import org.apache.camel.Exchange;
import org.apache.camel.ExtendedExchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.support.ExchangeHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipExceptionParamProcessor;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.IEipParamProcessor;
import pro.shushi.pamirs.eip.api.circuitbreaker.CircuitBreaker;
import pro.shushi.pamirs.eip.api.constant.EipContextConstant;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.enmu.ExchangePatternEnum;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.exception.CircuitBreakerOpenException;
import pro.shushi.pamirs.eip.api.util.EipCamelRouteUtil;
import pro.shushi.pamirs.eip.api.util.EipInitializationUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Adamancy Zhang at 15:30 on 2021-02-24
 */
@SuppressWarnings("unused")
@Slf4j
public class EipInterfaceContext {

    private static final Map<String, IEipIntegrationInterface<?>> CONTEXT = new ConcurrentHashMap<>();

    private static final Map<String, IEipIntegrationInterface<?>> TEMPORARY_CONTEXT = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> IEipIntegrationInterface<T> getInterface(String interfaceName) {
        return (IEipIntegrationInterface<T>) CONTEXT.get(interfaceName);
    }

    public static void putInterface(IEipIntegrationInterface<?> eipInterface) {
        CONTEXT.put(eipInterface.getInterfaceName(), eipInterface);
    }

    public static void removeInterface(String interfaceName) {
        CONTEXT.remove(interfaceName);
    }

    @SuppressWarnings("unchecked")
    public static <T> IEipIntegrationInterface<T> getTemporaryInterface(String interfaceName) {
        return (IEipIntegrationInterface<T>) TEMPORARY_CONTEXT.get(interfaceName);
    }

    public static void putTemporaryInterface(IEipIntegrationInterface<?> eipInterface) {
        TEMPORARY_CONTEXT.put(eipInterface.getInterfaceName(), eipInterface);
    }

    public static void removeTemporaryInterface(String interfaceName) {
        TEMPORARY_CONTEXT.remove(interfaceName);
    }

    public static <T> IEipIntegrationInterface<T> getAnyInterface(String interfaceName) {
        IEipIntegrationInterface<T> eipInterface = getTemporaryInterface(interfaceName);
        if (eipInterface == null) {
            eipInterface = getInterface(interfaceName);
        }
        return eipInterface;
    }

    public static <T> EipResult<T> call(String interfaceName) {
        return call(interfaceName, null, null);
    }

    public static <T> EipResult<T> call(String interfaceName, Object body) {
        return call(interfaceName, null, body);
    }

    public static <T> EipResult<T> call(String interfaceName, T executorContext, Object body) {
        if (StringUtils.isBlank(interfaceName)) {
            return EipResult.error(null, "Oops!", "无效的接口名称", null);
        }
        //获取入/出接口定义
        IEipIntegrationInterface<T> eipInterface = getAnyInterface(interfaceName);
        if (eipInterface == null) {
            log.error("未找到指定接口:{}", interfaceName);
            return EipResult.error(null, "Oops!", "未找到指定接口", null);
        }
        return call(eipInterface, executorContext, body);
    }

    public static <T> EipResult<T> call(IEipIntegrationInterface<T> eipInterface) {
        return call(eipInterface, null, null);
    }

    public static <T> EipResult<T> call(IEipIntegrationInterface<T> eipInterface, Object body) {
        return call(eipInterface, null, body);
    }

    public static <T> EipResult<T> call(IEipIntegrationInterface<T> eipInterface, T executorContext, Object body) {
        //获取CamelContext并检查启用状态
        EipCamelContext camelContext = EipCamelContext.getContext();
        try {
            if (!camelContext.getIsEnabled()) {
                camelContext.start();
            }
        } catch (Exception e) {
            return EipResult.error(null, "Oops!", e.getMessage(), e);
        }

        //获取生产者
        ProducerTemplate producerTemplate = camelContext.getProducerTemplate();

        //创建执行上下文
        IEipContext<T> context = eipInterface.getContextSupplier().get(eipInterface, executorContext, eipInterface.getRequestParamProcessor().getSerializable().serializable(body));

        //创建交换消息对象
        AtomicReference<Exchange> exchange = new AtomicReference<>(createExchange(camelContext, context));

        //获取请求参数处理器
        IEipParamProcessor<T> paramProcessor = eipInterface.getRequestParamProcessor();

        //请求参数处理
        try {
            paramProcessor.getProcessor().process(exchange.get());
        } catch (Exception e) {
            if (e instanceof PamirsException) {
                PamirsException exception = (PamirsException) e;
                return EipResult.error(getExecutorContext(exchange.get()), StringHelper.valueOf(exception.getCode()), exception.getMessage(), e);
            }
            return EipResult.error(getExecutorContext(exchange.get()), "Oops!", e.getMessage(), e);
        }

        //如果消息发生中断，则直接返回
        EipResult<T> result = verificationInterrupted(exchange.get());
        if (result != null) {
            return result;
        }

        CircuitBreaker circuitBreaker = BeanDefinitionUtils.getBean(CircuitBreaker.class);
        if (circuitBreaker == null) {
            exchange.set(producerTemplate.send(eipInterface.getUri(), exchange.get()));
            result = verificationInterrupted(exchange.get());
        } else {
            try {
                result = circuitBreaker.execute(eipInterface.getInterfaceName(), executorContext, () -> {
                    //将消息发送到接口指定路由，并交由Camel执行
                    exchange.set(producerTemplate.send(eipInterface.getUri(), exchange.get()));
                    // 结果处理
                    return verificationInterrupted(exchange.get());
                });
            } catch (CircuitBreakerOpenException e) {
                log.warn("接口进入熔断状态，msg:{}", e.getMessage());
                return EipResult.error(getExecutorContext(exchange.get()), CircuitBreakerOpenException.ERROR_CODE, e.getMessage(), e);
            }
        }

        //如果消息发生中断，则直接返回
        if (result != null) {
            return result;
        }

        //获取异常参数处理器
        IEipExceptionParamProcessor<T> exceptionParamProcessor = eipInterface.getExceptionParamProcessor();

        //异常参数处理
        try {
            exceptionParamProcessor.getProcessor().process(exchange.get());
        } catch (Exception e) {
            if (e instanceof PamirsException) {
                PamirsException exception = (PamirsException) e;
                return EipResult.error(getExecutorContext(exchange.get()), StringHelper.valueOf(exception.getCode()), exception.getMessage(), e);
            }
            return EipResult.error(getExecutorContext(exchange.get()), "Oops!", e.getMessage(), e);
        }

        //如果消息发生中断，则直接返回
        result = verificationInterrupted(exchange.get());
        if (result != null) {
            return result;
        }

        //获取响应参数处理器
        paramProcessor = eipInterface.getResponseParamProcessor();

        //响应参数处理
        try {
            paramProcessor.getProcessor().process(exchange.get());
        } catch (Exception e) {
            if (e instanceof PamirsException) {
                PamirsException exception = (PamirsException) e;
                return EipResult.error(getExecutorContext(exchange.get()), StringHelper.valueOf(exception.getCode()), exception.getMessage(), e);
            }
            return EipResult.error(getExecutorContext(exchange.get()), "Oops!", e.getMessage(), e);
        }

        //如果消息发生中断，则直接返回
        result = verificationInterrupted(exchange.get());
        if (result != null) {
            return result;
        }

        //返回响应结果
        return EipResult.success(EipInterfaceContext.getExecutorContext(exchange.get()), exchange.get().getMessage().getBody());
    }

    @Deprecated
    public static Object sendBody(String endpointUri, ExchangePatternEnum exchangePattern, Object body) throws Exception {
        //获取CamelContext并检查启用状态
        EipCamelContext camelContext = EipCamelContext.getContext();
        if (!camelContext.getIsEnabled()) {
            camelContext.start();
        }

        //获取生产者
        ProducerTemplate producerTemplate = camelContext.getProducerTemplate();
        return producerTemplate.sendBody(endpointUri, exchangePattern.getExchangePattern(), body);
    }

    @SuppressWarnings("unchecked")
    public static <T> IEipContext<T> getExecutorContext(Exchange exchange) {
        return (IEipContext<T>) exchange.getProperty(EipContextConstant.CONTEXT_KEY);
    }

    public static <T> void setExecutorContext(Exchange exchange, IEipContext<T> context) {
        exchange.setProperty(EipContextConstant.CONTEXT_KEY, context);
    }

    public static void routeInitialization(EipCamelContext context) {
        EipInitializationUtil util = EipInitializationUtil.newInstance(context);
        for (Map.Entry<String, IEipIntegrationInterface<?>> entry : EipInterfaceContext.CONTEXT.entrySet()) {
            IEipIntegrationInterface<?> eipInterface = entry.getValue();
            util.from(EipFunctionConstant.EMPTY.apply(context, eipInterface.getInterfaceName()))
                    .<EipCamelRouteUtil>to(eipInterface)
                    .end();
        }
    }

    private static <T> Exchange createExchange(EipCamelContext camelContext, IEipContext<T> context) {
        Exchange exchange = ExchangeBuilder.anExchange(camelContext.getCamelContext())
                .withProperty(EipContextConstant.CONTEXT_KEY, context)
                .withPattern(context.getApi().getExchangePattern().getExchangePattern())
                .withBody(context.getInterfaceContext())
                .build();

        exchange.getMessage().setHeader(Exchange.CHARSET_NAME, StandardCharsets.UTF_8.name());
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return exchange;
    }

    @SuppressWarnings("unchecked")
    private static <T> EipResult<T> verificationInterrupted(Exchange exchange) {
        ExtendedExchange extendedExchange = (ExtendedExchange) exchange;
        //当Camel进行异常处理后
        if (extendedExchange.isInterrupted()
                || ExchangeHelper.isFailureHandled(exchange)
                || extendedExchange.isRouteStop()
                || extendedExchange.isErrorHandlerHandled()) {
            Object body = exchange.getMessage().getBody();
            if (body instanceof EipResult) {
                return (EipResult<T>) body;
            } else {
                return EipResult.error(getExecutorContext(exchange), StringHelper.valueOf(getExecutorContext(exchange).getExecutorContextValue(IEipContext.DEFAULT_ERROR_CODE_KEY)),
                        StringHelper.valueOf(getExecutorContext(exchange).getExecutorContextValue(IEipContext.DEFAULT_ERROR_MESSAGE_KEY)), body);
            }
        }
        //当Camel未进行异常处理时
        Exception exception = exchange.getException();
        if (exception == null) {
            exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        }
        if (exception != null) {
            return EipResult.error(getExecutorContext(exchange), "Oops!", exception.getMessage(), exception);
        }
        return null;
    }
}
