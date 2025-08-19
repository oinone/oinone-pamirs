package pro.shushi.pamirs.eip.api.strategy.circuitbreaker;

import pro.shushi.pamirs.eip.api.enmu.CircuitBreakerRecoveryStrategyEnum;

/**
 * @author yeshenyue on 2025/4/15 10:24.
 */
public class CircuitBreakerConfig {

    /**
     * 统计窗口时长-秒
     */
    private final Integer statisticalDuration;

    /**
     * 慢调用判定阈值-毫秒
     */
    private final Long slowCallResponseTime;

    /**
     * 慢调用率阈值-百分比
     */
    private final Integer slowCallRateThreshold;

    /**
     * 异常熔断-失败率阈值-百分比
     */
    private final Integer failureRateThreshold;

    /**
     * 最小调用数阈值，只有当统计窗口内总请求数达到该值才计算熔断条件。
     */
    private final Integer minRequestCount;

    /**
     * 熔断打开状态保持时长-秒
     */
    private final Integer waitDurationInOpenState;

    /**
     * 熔断恢复策略
     */
    private final CircuitBreakerRecoveryStrategyEnum recoveryStrategy;

    private CircuitBreakerConfig(Builder builder) {
        this.statisticalDuration = builder.statisticalDuration;
        this.slowCallResponseTime = builder.slowCallResponseTime;
        this.failureRateThreshold = builder.failureRateThreshold;
        this.slowCallRateThreshold = builder.slowCallRateThreshold;
        this.minRequestCount = builder.minRequestCount;
        this.waitDurationInOpenState = builder.waitDurationInOpenState;
        this.recoveryStrategy = builder.recoveryStrategy;
    }

    public Integer getStatisticalDuration() {
        return statisticalDuration;
    }

    public Long getSlowCallResponseTime() {
        return slowCallResponseTime;
    }

    public Integer getSlowCallRateThreshold() {
        return slowCallRateThreshold;
    }

    public Integer getFailureRateThreshold() {
        return failureRateThreshold;
    }

    public Integer getMinRequestCount() {
        return minRequestCount;
    }

    public Integer getWaitDurationInOpenState() {
        return waitDurationInOpenState;
    }

    public CircuitBreakerRecoveryStrategyEnum getRecoveryStrategy() {
        return recoveryStrategy;
    }

    /**
     * Builder 用于构建 CircuitBreakerConfig 实例。
     */
    public static class Builder {
        private Integer statisticalDuration;
        private Long slowCallResponseTime;
        private Integer failureRateThreshold;
        private Integer slowCallRateThreshold;
        private Integer minRequestCount;
        private Integer waitDurationInOpenState;
        private CircuitBreakerRecoveryStrategyEnum recoveryStrategy;

        public Builder statisticalDuration(Integer seconds) {
            this.statisticalDuration = seconds;
            return this;
        }

        public Builder slowCallResponseTime(Long millis) {
            this.slowCallResponseTime = millis;
            return this;
        }

        public Builder failureRateThreshold(Integer percentage) {
            this.failureRateThreshold = percentage;
            return this;
        }

        public Builder slowCallRateThreshold(Integer percentage) {
            this.slowCallRateThreshold = percentage;
            return this;
        }

        public Builder minRequestCount(Integer minCalls) {
            this.minRequestCount = minCalls;
            return this;
        }

        public Builder waitDurationInOpenState(Integer seconds) {
            this.waitDurationInOpenState = seconds;
            return this;
        }

        public Builder recoveryStrategy(CircuitBreakerRecoveryStrategyEnum recoveryStrategy) {
            this.recoveryStrategy = recoveryStrategy;
            return this;
        }

        public CircuitBreakerConfig build() {
            return new CircuitBreakerConfig(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
