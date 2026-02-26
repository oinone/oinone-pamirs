package pro.shushi.pamirs.eip.api.strategy.limiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.model.strategy.EipOpenRateLimitPolicy;
import pro.shushi.pamirs.eip.api.strategy.limiter.api.OpenRateLimitApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import static pro.shushi.pamirs.eip.api.enmu.FlowControlEffectTypeEnum.RAPID_FAILURE;

/**
 * @author yeshenyue on 2025/4/22 14:29.
 */
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultOpenRateLimit implements OpenRateLimitApi {

    private static final ConcurrentHashMap<String, RateLimiter> LIMITER_MAP = new ConcurrentHashMap<>();

    @Override
    public void registerPolicy(EipOpenRateLimitPolicy policy) {
        String key = buildKey(policy);
        RateLimiterConfig config = buildConfig(policy);
        RateLimiter limiter = RateLimiter.of(key, config);
        LIMITER_MAP.put(key, limiter);
    }

    @Override
    public void unregisterPolicy(String appKey, String interfaceName) {
        String key = buildKey(appKey, interfaceName);
        LIMITER_MAP.remove(key);
    }

    @Override
    public boolean tryAcquire(String appKey, String interfaceName) {
        String key = buildKey(appKey, interfaceName);
        RateLimiter limiter = LIMITER_MAP.get(key);
        return limiter == null || limiter.acquirePermission();
    }

    @Override
    public boolean isExist(String appKey, String interfaceName) {
        String key = buildKey(appKey, interfaceName);
        return LIMITER_MAP.containsKey(key);
    }

    private static String buildKey(EipOpenRateLimitPolicy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("Rate limit policy cannot be null");
        }
        if (policy.getApplication() == null) {
            throw new IllegalArgumentException("application cannot be null");
        }
        String appKey = policy.getApplication().getAppKey();
        if (StringUtils.isBlank(appKey) || StringUtils.isBlank(policy.getInterfaceName())) {
            throw new IllegalArgumentException("applicationCode and interfaceName cannot be null");
        }
        return buildKey(appKey, policy.getInterfaceName());
    }

    private static String buildKey(String appKey, String interfaceName) {
        return appKey + "_" + interfaceName;
    }

    private static RateLimiterConfig buildConfig(EipOpenRateLimitPolicy policy) {
        if (policy == null || policy.getQps() == null || policy.getQps() <= 0) {
            throw new IllegalArgumentException("Invalid rate limit policy: qps must be positive");
        }
        Long qps = policy.getQps();
        long timeoutMs = RAPID_FAILURE.equals(policy.getFlowControlEffect()) ? 0L : policy.getTimeout();
        return RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(qps.intValue())
                .timeoutDuration(Duration.ofMillis(timeoutMs))
                .build();
    }

}
