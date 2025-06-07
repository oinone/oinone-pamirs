package pro.shushi.pamirs.eip.api.circuitbreaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.manager.CircuitBreakerManager;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author yeshenyue on 2025/4/15 10:15.
 */
@Slf4j
@Component
public class CircuitBreaker {

    @Autowired
    private CircuitBreakerManager circuitBreakerManager;

    public <T> EipResult<T> execute(String interfaceName, Object executorContext, Supplier<EipResult<T>> supplier) {
        CircuitBreakerConfig config = circuitBreakerManager.getConfig(interfaceName);
        if (config == null) {
            return supplier.get();
        }

        EipResult<T> result;
        long startTime = System.currentTimeMillis();
        boolean success = false;
        boolean slow = false;

        // 熔断状态判断
        circuitBreakerManager.beforeCall(interfaceName);

        try {
            // 请求调用&结果解析
            result = supplier.get();
            // 为空不代表失败，按成功处理
            success = result == null || Boolean.TRUE.equals(result.getSuccess());
        } finally {
            long duration = calculateDuration(executorContext, startTime);
            log.info("调用时间：{}", duration);
            if (config.getSlowCallResponseTime() != null) {
                slow = duration >= config.getSlowCallResponseTime();
            }
            circuitBreakerManager.afterCall(interfaceName, success, slow);
        }
        return result;
    }

    private Long calculateDuration(Object executorContext, long startTime) {
        if (executorContext instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> contextMap = (Map<String, Object>) executorContext;
            Object eipLogObject = MapHelper.getIteration(contextMap, IEipContext.LOG_INVOKE_MILLI_SECOND_KEY);
            if (eipLogObject instanceof Long) {
                return (Long) eipLogObject;
            }
        }
        return System.currentTimeMillis() - startTime;
    }

}
