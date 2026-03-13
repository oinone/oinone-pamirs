package pro.shushi.pamirs.eip.api.strategy.circuitbreaker;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.strategy.manager.CircuitBreakerManager;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

/**
 * @author yeshenyue on 2025/4/15 10:15.
 */
@Slf4j
@Component
public class CircuitBreaker {

    @Autowired
    private CircuitBreakerManager circuitBreakerManager;

    public <T> Pair<Exchange, EipResult<T>> execute(ProducerTemplate producerTemplate, IEipContext<T> context, IEipIntegrationInterface<T> eipInterface, Exchange exchange) {
        String interfaceName = eipInterface.getInterfaceName();
        CircuitBreakerConfig config = circuitBreakerManager.getConfig(interfaceName);
        if (config == null) {
            exchange = producerTemplate.send(eipInterface.getUri(), exchange);
            return Pair.of(exchange, EipInterfaceContext.verificationInterrupted(exchange));
        }

        long startTime = System.currentTimeMillis();
        boolean success = false;
        boolean slow = false;

        // 熔断状态判断
        circuitBreakerManager.beforeCall(interfaceName);

        try {
            exchange = producerTemplate.send(eipInterface.getUri(), exchange);
            EipResult<T> result = EipInterfaceContext.verificationInterrupted(exchange);
            success = result == null || result.getSuccess();
            return Pair.of(exchange, result);
        } finally {
            long duration = calculateDuration(context, startTime);
            log.info("Call time: {}", duration);
            if (config.getSlowCallResponseTime() != null) {
                slow = duration >= config.getSlowCallResponseTime();
            }
            circuitBreakerManager.afterCall(interfaceName, success, slow);
        }
    }

    private Long calculateDuration(IEipContext<?> context, long startTime) {
        Object invokeTime = context.getExecutorContextValue(IEipContext.LOG_INVOKE_MILLI_SECOND_KEY);
        if (invokeTime instanceof Long) {
            return (Long) invokeTime;
        }
        return System.currentTimeMillis() - startTime;
    }

}
