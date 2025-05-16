package pro.shushi.pamirs.eip.api.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.cache.EipLongRedisTemplate;
import pro.shushi.pamirs.eip.api.circuitbreaker.CircuitBreakerConfig;
import pro.shushi.pamirs.eip.api.circuitbreaker.RollingWindowCounter;
import pro.shushi.pamirs.eip.api.enmu.CircuitBreakerRecoveryStrategyEnum;
import pro.shushi.pamirs.eip.api.enmu.CircuitBreakerStatusEnum;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.exception.CircuitBreakerOpenException;
import pro.shushi.pamirs.eip.api.model.CircuitBreakerRecord;
import pro.shushi.pamirs.eip.api.model.EipCircuitBreakerRule;
import pro.shushi.pamirs.eip.api.service.CircuitBreakerStateSyncService;
import pro.shushi.pamirs.eip.api.service.CircuitBreakerRecordService;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static pro.shushi.pamirs.eip.api.constant.EipCircuitBreakerConstant.*;

/**
 * 熔断器管理类，维护所有命名熔断器的状态和计数
 * @author yeshenyue on 2025/4/15 13:42.
 */
@Slf4j
@Component
public class CircuitBreakerManager {

    /**
     * 统计区间小于此值，使用本地计数器
     */
    private static final Integer STATISTICAL_DURATION_LOCAL = 10;

    /**
     * 熔断器条目，封装每个命名熔断器的配置、状态和计数器。
     */
    private static class CircuitBreakerEntry {
        // 熔断规则编码
        final String circuitBreakerRuleCode;
        // 熔断配置
        final CircuitBreakerConfig config;
        // 熔断状态
        volatile CircuitBreakerStatusEnum state;
        // 本地计数器
        final RollingWindowCounter rollingCounter;
        final EipCircuitBreakerRule rule;

        public CircuitBreakerEntry(EipCircuitBreakerRule rule) {
            CircuitBreakerConfig config = rule.buildConfig();
            this.config = config;
            this.rule = rule;
            this.state = CircuitBreakerStatusEnum.CLOSED;
            this.circuitBreakerRuleCode = rule.getCode();

            // 如果统计窗口不超过10秒，初始化本地滑动窗口计数器
            if (config.getStatisticalDuration() <= STATISTICAL_DURATION_LOCAL) {
                int windowSeconds = config.getStatisticalDuration();
                this.rollingCounter = new RollingWindowCounter(windowSeconds);
            } else {
                this.rollingCounter = null;
            }
        }
    }

    /**
     * 熔断器管理
     * key:租户+interfaceName
     */
    private static final ConcurrentHashMap<String, CircuitBreakerEntry> circuitMap = new ConcurrentHashMap<>();

    @Autowired
    @Qualifier(EipLongRedisTemplate.REDIS_TEMPLATE_BEAN_NAME)
    private EipLongRedisTemplate eipLongRedisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CircuitBreakerStateSyncService circuitBreakerStateSyncService;
    @Autowired
    private CircuitBreakerRecordService circuitBreakerRecordService;

    public CircuitBreakerConfig getConfig(String interfaceName) {
        CircuitBreakerEntry circuitBreakerEntry = circuitMap.get(interfaceName);
        if (circuitBreakerEntry == null) {
            return null;
        }
        return circuitBreakerEntry.config;
    }

    /**
     * 熔断器本地注册
     */
    public void registerCircuitBreaker(String interfaceName, EipCircuitBreakerRule circuitBreakerRule) {
        CircuitBreakerEntry circuitBreakerEntry = new CircuitBreakerEntry(circuitBreakerRule);
        circuitMap.put(interfaceName, circuitBreakerEntry);
    }

    /**
     * 熔断器注销
     */
    public void unregister(String interfaceName) {
        if (!circuitMap.containsKey(interfaceName)) {
            return;
        }
        // 注销远程熔断状态
        eipLongRedisTemplate.delete(getStateKey(interfaceName));
        // 清除计数器
        resetCounts(interfaceName, circuitMap.get(interfaceName));
        // 注销本地熔断器
        circuitMap.remove(interfaceName);
    }

    /**
     * 节点状态更新
     */
    public void updateState(String interfaceName, CircuitBreakerStatusEnum state) {
        CircuitBreakerEntry circuitBreakerEntry = circuitMap.get(interfaceName);
        if (circuitBreakerEntry == null) {
            log.error("熔断状态更新失败，未查询到熔断器：{}", interfaceName);
            return;
        }
        circuitBreakerEntry.state = state;
    }

    /**
     * 在执行实际调用前检查熔断器状态，决定是否允许此次调用通过；
     * 如果熔断器处于OPEN且未到半开时间，则拒绝调用；
     * 如果到达半开探测点，则尝试切换到HALF_OPEN。
     */
    public void beforeCall(String interfaceName) {
        CircuitBreakerEntry entry = circuitMap.get(interfaceName);
        // 未查询到熔断器则不熔断
        if (entry == null) {
            return;
        }

        if (CircuitBreakerStatusEnum.OPEN.equals(entry.state)) {
            // 检查Redis中状态键是否已过期，判断是否可以进入半开
            String stateKey = getStateKey(interfaceName);
            Boolean exist = stringRedisTemplate.hasKey(stateKey);

            if (Boolean.TRUE.equals(exist)) {
                // key存在，目前处于熔断状态
                throw new CircuitBreakerOpenException(EipExpEnumerate.EIP_CB_STATUS_OPEN, interfaceName);
            } else {
                if (CircuitBreakerRecoveryStrategyEnum.SINGLE_PROBE.equals(entry.config.getRecoveryStrategy())) {
                    // 尝试进入半开状态
                    boolean halfOpenAcquired = tryEnterHalfOpen(interfaceName, entry);
                    if (!halfOpenAcquired) {
                        throw new CircuitBreakerOpenException(EipExpEnumerate.EIP_CB_STATUS_OPEN, interfaceName);
                    }
                } else {
                    throw PamirsException.construct(EipExpEnumerate.EIP_CB_NOT_FUSE_RECOVERY_POLICY).errThrow();
                }
            }
        } else if (CircuitBreakerStatusEnum.HALF_OPEN.equals(entry.state)) {
            throw new CircuitBreakerOpenException(EipExpEnumerate.EIP_CB_STATUS_HALF_OPEN, interfaceName);
        }
    }

    /**
     * 在调用完成后，根据结果更新计数器和状态。
     */
    public void afterCall(String interfaceName, boolean success, boolean slow) {
        CircuitBreakerEntry entry = circuitMap.get(interfaceName);
        if (entry == null) {
            return;
        }

        // 半开状态需要更新
        CircuitBreakerStatusEnum prevState = entry.state;
        if (CircuitBreakerStatusEnum.HALF_OPEN.equals(prevState)) {
            boolean isClose = success && !slow;
            // 解除半熔断状态
            disarmHalfOpenStatus(interfaceName, entry);
            if (isClose) {
                closeCircuit(interfaceName, entry);
            } else {
                openCircuit(interfaceName, entry);
            }

            // 通知其他节点状态变更
            if (entry.config.getStatisticalDuration() > STATISTICAL_DURATION_LOCAL) {
                CircuitBreakerStatusEnum newState = isClose ? CircuitBreakerStatusEnum.CLOSED : CircuitBreakerStatusEnum.OPEN;
                circuitBreakerStateSyncService.syncState(interfaceName, newState);
            }
        }

        // 已经是熔断状态，不继续处理
        if (Boolean.FALSE.equals(CircuitBreakerStatusEnum.CLOSED.equals(prevState))) {
            return;
        }

        if (entry.config.getStatisticalDuration() > STATISTICAL_DURATION_LOCAL) {
            // Redis计数统计
            handleRedisCounting(interfaceName, entry, success, slow);
        } else {
            // 使用本地滑动窗口统计
            RollingWindowCounter counter = entry.rollingCounter;
            if (counter != null) {
                boolean thresholdExceeded;
                synchronized (counter) {
                    counter.recordEvent(success, slow);
                    // 若总调用次数未达到最小要求，直接返回（无需熔断判断）
                    if (counter.getTotalCount() < entry.config.getMinRequestCount()) {
                        return;
                    }

                    // 计算当前失败率和慢调用率
                    double failureRate = counter.getFailCount() * 100.0 / counter.getTotalCount();
                    double slowRate = counter.getSlowCount() * 100.0 / counter.getTotalCount();

                    Integer failureThreshold = entry.config.getFailureRateThreshold();
                    Integer slowThreshold = entry.config.getSlowCallRateThreshold();
                    boolean failureExceeded = failureThreshold != null && failureRate >= failureThreshold;
                    boolean slowExceeded = slowThreshold != null && slowRate >= slowThreshold;

                    thresholdExceeded = failureExceeded || slowExceeded;

                    // 其它线程打开熔断，不继续执行
                    if (CircuitBreakerStatusEnum.OPEN.equals(entry.state)) {
                        return;
                    }

                    if (thresholdExceeded) {
                        updateState(interfaceName, CircuitBreakerStatusEnum.OPEN);
                    }
                }

                // 打开熔断
                if (thresholdExceeded) {
                    openCircuit(interfaceName, entry);
                }
            }
        }
    }

    /**
     * 解除缓存半熔断状态
     */
    private void disarmHalfOpenStatus(String interfaceName, CircuitBreakerEntry entry) {
        String stateKey = getStateKey(interfaceName);
        stringRedisTemplate.delete(stateKey);
        resetCounts(interfaceName, entry);
    }

    /**
     * 打开熔断
     */
    private void openCircuit(String interfaceName, CircuitBreakerEntry entry) {
        long ttlSeconds = entry.config.getWaitDurationInOpenState();
        if (ttlSeconds <= 0) {
            throw new CircuitBreakerOpenException(EipExpEnumerate.EIP_CB_TTL_WAIT_DURATION_ERROR, interfaceName);
        }

        Instant endTime = Instant.now().plus(Duration.ofSeconds(ttlSeconds));
        String stateKey = getStateKey(interfaceName);
        Boolean set = stringRedisTemplate.opsForValue().setIfAbsent(
                stateKey, CircuitBreakerStatusEnum.OPEN.value(), ttlSeconds, TimeUnit.SECONDS);

        updateState(interfaceName, CircuitBreakerStatusEnum.OPEN);
        if (Boolean.TRUE.equals(set)) {
            // 重置计数器
            resetCounts(interfaceName, entry);
            // 推送到Redis，熔断记录异步落库
            pushRecord(interfaceName, entry, Date.from(endTime));
        }
    }

    /**
     * 关闭熔断器
     */
    private void closeCircuit(String interfaceName, CircuitBreakerEntry entry) {
        String stateKey = getStateKey(interfaceName);
        stringRedisTemplate.delete(stateKey);
        resetCounts(interfaceName, entry);
        updateState(interfaceName, CircuitBreakerStatusEnum.CLOSED);
    }

    /**
     * 尝试进入半开状态
     */
    private boolean tryEnterHalfOpen(String interfaceName, CircuitBreakerEntry entry) {
        String stateKey = getStateKey(interfaceName);
        long ttlSeconds = entry.config.getWaitDurationInOpenState();

        Boolean set = stringRedisTemplate.opsForValue().setIfAbsent(
                stateKey, CircuitBreakerStatusEnum.HALF_OPEN.value(), ttlSeconds, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(set)) {
            updateState(interfaceName, CircuitBreakerStatusEnum.HALF_OPEN);
            circuitBreakerStateSyncService.syncState(interfaceName, entry.state);
            return true;
        }
        return false;
    }

    private void handleRedisCounting(String interfaceName, CircuitBreakerEntry entry, boolean success, boolean slow) {
        // 构造各计数键
        String totalKey = getCountTotalKey(interfaceName);
        String failKey = getCountFailKey(interfaceName);
        String slowKey = getCountSlowKey(interfaceName);
        long windowMillis = entry.config.getStatisticalDuration();

        // 初始化Redis计数值
        Long totalCount = eipLongRedisTemplate.opsForValue().increment(totalKey);
        if (totalCount != null && totalCount == 1) {
            eipLongRedisTemplate.expire(totalKey, windowMillis, TimeUnit.SECONDS);
            eipLongRedisTemplate.opsForValue().setIfAbsent(failKey, 0L, windowMillis, TimeUnit.SECONDS);
            eipLongRedisTemplate.opsForValue().setIfAbsent(slowKey, 0L, windowMillis, TimeUnit.SECONDS);
        }

        Long failCount = null;
        Long slowCount = null;
        if (Boolean.FALSE.equals(success)) {
            failCount = eipLongRedisTemplate.opsForValue().increment(failKey);
            if (failCount != null && failCount == 1) {
                eipLongRedisTemplate.expire(failKey, windowMillis, TimeUnit.SECONDS);
            }
        }

        if (slow) {
            slowCount = eipLongRedisTemplate.opsForValue().increment(slowKey);
            if (slowCount != null && slowCount == 1) {
                eipLongRedisTemplate.expire(slowKey, windowMillis, TimeUnit.SECONDS);
            }
        }

        // 获取当前计数值，如果某项未获取则从Redis读
        if (failCount == null) {
            failCount = getCountOrZero(failKey);
        }
        if (slowCount == null) {
            slowCount = getCountOrZero(slowKey);
        }
        totalCount = (totalCount != null ? totalCount : getCountOrZero(totalKey));

        // 未达到最小调用数要求则不触发判断
        if (totalCount < entry.config.getMinRequestCount()) {
            return;
        }

        // 计算比例
        double failureRate = (totalCount != 0 ? failCount * 100.0 / totalCount : 0.0);
        double slowRate = (totalCount != 0 ? slowCount * 100.0 / totalCount : 0.0);
        boolean exceedFail = false;
        boolean exceedSlow = false;

        if (entry.config.getFailureRateThreshold() != null) {
            exceedFail = failureRate >= entry.config.getFailureRateThreshold();
        }
        if (entry.config.getSlowCallRateThreshold() != null) {
            exceedSlow = slowRate >= entry.config.getSlowCallRateThreshold();
        }

        // 打开熔断，并通知zk
        if ((exceedFail || exceedSlow) && entry.state == CircuitBreakerStatusEnum.CLOSED) {
            openCircuit(interfaceName, entry);
            circuitBreakerStateSyncService.syncState(interfaceName, CircuitBreakerStatusEnum.OPEN);
        }
    }

    private Long getCountOrZero(String key) {
        Long val = eipLongRedisTemplate.opsForValue().get(key);
        return val == null ? 0L : val;
    }

    /**
     * 重置指定熔断器的统计计数数据。用于在熔断打开或关闭时清除旧的统计。
     */
    private void resetCounts(String interfaceName, CircuitBreakerEntry entry) {
        // 清空本地滑动窗口计数
        if (entry.rollingCounter != null) {
            synchronized (entry.rollingCounter) {
                entry.rollingCounter.reset();
            }
        }
        // 删除Redis中的计数键
        eipLongRedisTemplate.delete(getCountTotalKey(interfaceName));
        eipLongRedisTemplate.delete(getCountFailKey(interfaceName));
        eipLongRedisTemplate.delete(getCountSlowKey(interfaceName));
    }

    /**
     * 熔断记录缓存到Redis，定时任务异步落库
     */
    private void pushRecord(String interfaceName, CircuitBreakerEntry entry, Date endTime) {
        CircuitBreakerRecord record = new CircuitBreakerRecord();
        record.setInterfaceName(interfaceName);
        record.setCircuitBreakerRuleCode(entry.circuitBreakerRuleCode);
        record.setStartTime(new Date());
        record.setEndTime(endTime);
        record.setCircuitBreakerRule(entry.rule);
        record.setCircuitBreakerRuleName(entry.rule.getRuleName());
        circuitBreakerRecordService.pushRecord(record);
    }
}
