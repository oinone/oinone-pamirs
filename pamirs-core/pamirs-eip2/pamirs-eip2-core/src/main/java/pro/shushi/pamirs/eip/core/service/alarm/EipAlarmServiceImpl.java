package pro.shushi.pamirs.eip.core.service.alarm;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.eip.api.model.alarm.EipAlarmRule;
import pro.shushi.pamirs.eip.api.model.alarm.EipAlarmRuleRelInterface;
import pro.shushi.pamirs.eip.api.model.alarm.EipAlarmStat;
import pro.shushi.pamirs.eip.api.service.alarm.EipAlarmService;
import pro.shushi.pamirs.eip.api.strategy.cache.EipLongRedisTemplate;
import pro.shushi.pamirs.eip.core.manager.EipAlarmNotifyManager;
import pro.shushi.pamirs.framework.common.config.TtlAsyncTaskExecutor;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.util.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * EipAlarmServiceImpl
 *
 * @author yakir on 2026/04/08 14:45.
 */
@Slf4j
@Order
@Component
@SPI.Service
public class EipAlarmServiceImpl implements EipAlarmService {

    @Autowired
    @Qualifier(EipLongRedisTemplate.REDIS_TEMPLATE_BEAN_NAME)
    private EipLongRedisTemplate eipLongRedisTemplate;

    @Autowired
    @Qualifier("stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private EipAlarmNotifyManager eipAlarmNotifyManager;

    private static final int BUCKET_SIZE = 10;
    private static final String CACHE_PREFIX = "pamirs:eip:alarm:";
    private static final String CACHE_TOTAL = "total";
    private static final String CACHE_FAIL = "fail";
    private static final String CACHE_SUPPRESS = "suppress";

    private static final EipAlarmRule emptyRule = new EipAlarmRule();

    private final LoadingCache<String, EipAlarmRule> alarmRuleCache = Caffeine.<String, EipAlarmRule>newBuilder()
            .maximumSize(1000)
            .build(this::loadRule);

    private EipAlarmRule loadRule(String interfaceName) {

        if (StringUtils.isBlank(interfaceName)) {
            return emptyRule;
        }
        IWrapper<EipAlarmRuleRelInterface> relQw = Pops.<EipAlarmRuleRelInterface>lambdaQuery()
                .from(EipAlarmRuleRelInterface.MODEL_MODEL)
                .eq(EipAlarmRuleRelInterface::getInterfaceName, interfaceName)
                .last("LIMIT 1");
        EipAlarmRuleRelInterface ruleRel = new EipAlarmRuleRelInterface().queryOneByWrapper(relQw);

        if (null == ruleRel) {
            return emptyRule;
        }

        String ruleTechName = ruleRel.getRuleTechName();

        IWrapper<EipAlarmRule> ruleQw = Pops.<EipAlarmRule>lambdaQuery()
                .from(EipAlarmRule.MODEL_MODEL)
                .eq(EipAlarmRule::getTechName, ruleTechName)
                .eq(EipAlarmRule::getEnabled, true);

        EipAlarmRule rule = new EipAlarmRule().queryOneByWrapper(ruleQw);
        if (null == rule) {
            return emptyRule;
        }
        log.info("Load Rule:[{}]", rule.getName());
        return rule;
    }

    @Override
    public boolean alarm(EipLog eipLog, IEipContext<SuperMap> context) {
        try {
            String interfaceName = eipLog.getInterfaceName();
            EipAlarmRule rule = alarmRuleCache.get(interfaceName);
            if (null == rule || emptyRule == rule) {
                return true;
            }

            Date invokeEndDate = eipLog.getInvokeEndDate();
            long bucketKey = buildBucketKey(invokeEndDate.getTime());

            String key = buildKey(interfaceName, bucketKey);
            eipLongRedisTemplate.opsForHash().increment(key, CACHE_TOTAL, 1);
            eipLongRedisTemplate.expire(key, rule.getTimeWindow() + (BUCKET_SIZE * 2), TimeUnit.SECONDS);

            boolean isFailed = !Boolean.TRUE.equals(eipLog.getIsSuccess());
            if (isFailed) {
                eipLongRedisTemplate.opsForHash().increment(key, CACHE_FAIL, 1);

                if (!isSuppressed(interfaceName)) {


                    EipAlarmStat stat = queryWindow(interfaceName, rule.getTimeWindow());
                    stat.setRuleName(rule.getName());
                    stat.setInterfaceTechName(interfaceName);
                    stat.setRuleTechName(rule.getTechName());
                    if (isTriggered(rule, stat)) {
                        if (trySuppress(interfaceName, rule.getRepeatInterval())) {

                            log.info("total: [{}]", stat.getTotalSum());
                            log.info("fail: [{}]", stat.getFailSum());
                            log.warn("alarm :[{}]", interfaceName);

                            TtlAsyncTaskExecutor.getExecutorService().submit(() -> {
                                String iftName = Optional.ofNullable(context.getApi())
                                        .map(_api -> (EipIntegrationInterface) _api)
                                        .map(EipIntegrationInterface::getName)
                                        .orElse("");
                                stat.setInterfaceName(iftName);
                                eipAlarmNotifyManager.send(rule, stat);
                            });
                        }
                    }
                }
            }

            return true;
        } catch (Throwable throwable) {
            log.error("Alarm Error", throwable);
            return false;
        }
    }

    @Override
    public void clearRuleCache() {
        alarmRuleCache.cleanUp();
        alarmRuleCache.invalidateAll();
    }

    public EipAlarmStat queryWindow(String interfaceName, int timeWindow) {

        long now = System.currentTimeMillis();
        long windowStart = (now / 1000) - timeWindow;

        long firstBucket = (windowStart / BUCKET_SIZE) * BUCKET_SIZE;
        long lastBucket = buildBucketKey(now);

        log.info("Bucket Range: [{}-{}]", firstBucket, lastBucket);

        List<String> keys = new ArrayList<>();
        for (long bucket = firstBucket; bucket <= lastBucket; bucket += BUCKET_SIZE) {
            keys.add(buildKey(interfaceName, bucket));
        }

        List<Object> results = eipLongRedisTemplate.executePipelined(
                (RedisCallback<Object>) connection -> {
                    for (String key : keys) {
                        connection.hMGet(key.getBytes(),
                                CACHE_TOTAL.getBytes(),
                                CACHE_FAIL.getBytes()
                        );
                    }
                    return null;
                }
        );

        long totalSum = 0;
        long failSum = 0;

        for (Object result : results) {
            List<Object> values = (List<Object>) result;
            if (values == null) {
                continue;
            }
            totalSum += Optional.ofNullable(values.get(0)).map(String::valueOf).map(NumberUtils::longValue).orElse(0L);
            failSum += Optional.ofNullable(values.get(1)).map(String::valueOf).map(NumberUtils::longValue).orElse(0L);
        }

        EipAlarmStat stat = EipAlarmStat.of();
        stat.setStart(firstBucket);
        stat.setEnd(lastBucket);
        stat.setTotalSum(totalSum);
        stat.setFailSum(failSum);

        return stat;
    }

    public boolean isSuppressed(String interfaceName) {
        String key = buildSuppressKey(interfaceName);
        return stringRedisTemplate.hasKey(key);
    }

    public boolean trySuppress(String interfaceName, int repeatInterval) {
        String key = buildSuppressKey(interfaceName);
        Boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(
                key,
                String.valueOf(System.currentTimeMillis()),
                repeatInterval,
                TimeUnit.MINUTES
        );

        return Boolean.TRUE.equals(acquired);
    }

    private boolean isTriggered(EipAlarmRule rule, EipAlarmStat stat) {

        switch (rule.getMetricType()) {
            case FAILURE_COUNT:
                return stat.getFailSum() >= rule.getThreshold();

            case FAILURE_RATE:
                long total = stat.getTotalSum();
                long fail = stat.getFailSum();

                if (total == 0) {
                    return false;
                }
                return total >= rule.getMinCallCount()
                        && BigDecimal.valueOf(fail)
                        .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .compareTo(BigDecimal.valueOf(rule.getThreshold())) >= 0;

            default:
                return false;
        }
    }


    private String buildKey(String interfaceName, long bucketKey) {
        return CACHE_PREFIX + interfaceName + ":" + bucketKey;
    }

    private String buildSuppressKey(String interfaceName) {
        return CACHE_PREFIX + CACHE_SUPPRESS + ":" + interfaceName;
    }

    private long buildBucketKey(long timestampMillis) {
        long timestampSeconds = timestampMillis / 1000;
        return (timestampSeconds / BUCKET_SIZE) * BUCKET_SIZE;
    }
}
