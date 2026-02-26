package pro.shushi.pamirs.eip.api.strategy.cache.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.eip.api.strategy.cache.EipLogCountCacheApi;
import pro.shushi.pamirs.eip.api.strategy.cache.LogCountRedisTemplate;
import pro.shushi.pamirs.eip.api.strategy.constant.EipLogCountCacheConstant;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.*;

/**
 * @author yeshenyue on 2025/4/10 15:55.
 */
@Order
@Component
@SPI.Service
public class DefaultEipLogCountCache implements EipLogCountCacheApi {

    @Autowired
    @Qualifier(LogCountRedisTemplate.LOG_COUNT_REDIS_TEMPLATE_NAME)
    private LogCountRedisTemplate redisTemplate;

    @Override
    public void addLogCount(EipLog eipLog) {
        if (eipLog == null || StringUtils.isBlank(eipLog.getInterfaceName())) {
            return;
        }

        String interfaceName = eipLog.getInterfaceName();
        InterfaceTypeEnum interfaceType = eipLog.getInterfaceType();
        if (Boolean.TRUE.equals(eipLog.getIsSuccess())) {
            redisTemplate.incrementCallCount(interfaceType, interfaceName, EipLogCountCacheConstant.SUCCESS);

            long invokeTime = eipLog.getInvokeEndDate().getTime() - eipLog.getInvokeDate().getTime();
            if (invokeTime < 100L) {
                redisTemplate.incrementCallCount(interfaceType, interfaceName, EipLogCountCacheConstant.ULTRA_FAST);
            } else if (invokeTime < 300L) {
                // 100 ≤ t < 300 ms
                redisTemplate.incrementCallCount(interfaceType, interfaceName, EipLogCountCacheConstant.VERY_FAST);
            } else if (invokeTime < 500L) {
                // 300 ≤ t < 500 ms
                redisTemplate.incrementCallCount(interfaceType, interfaceName, EipLogCountCacheConstant.FAST);
            } else if (invokeTime < 1000L) {
                // 500 ≤ t < 1000 ms
                redisTemplate.incrementCallCount(interfaceType, interfaceName, EipLogCountCacheConstant.MODERATE);
            } else if (invokeTime < 3000L) {
                // 1s ≤ t < 3s
                redisTemplate.incrementCallCount(interfaceType, interfaceName, EipLogCountCacheConstant.SLOW);
            } else if (invokeTime < 8000L) {
                // 3s ≤ t < 8s
                redisTemplate.incrementCallCount(interfaceType, interfaceName, EipLogCountCacheConstant.VERY_SLOW);
            } else if (invokeTime < 30000L) {
                // 8s ≤ t < 30s
                redisTemplate.incrementCallCount(interfaceType, interfaceName, EipLogCountCacheConstant.SLOWEST);
            } else {
                // t ≥ 30s（30000 ms）
                redisTemplate.incrementCallCount(interfaceType, interfaceName, EipLogCountCacheConstant.TIMEOUT);
            }
        } else {
            redisTemplate.incrementCallCount(interfaceType, interfaceName, EipLogCountCacheConstant.FAIL);
        }
    }

    @Override
    public Map<String, Long> getCallCount(InterfaceTypeEnum interfaceType, List<String> interfaceNameList) {
        if (CollectionUtils.isEmpty(interfaceNameList)) {
            return Collections.emptyMap();
        }

        // 收集需要查询的缓存key
        EipLogCountCacheConstant[] constants = EipLogCountCacheConstant.values();
        List<String> keyList = new ArrayList<>(interfaceNameList.size() * constants.length);
        for (String interfaceName : interfaceNameList) {
            for (EipLogCountCacheConstant constant : constants) {
                keyList.add(constant.getKeyPrefix(interfaceType, interfaceName));
            }
        }

        List<Long> values = redisTemplate.opsForValue().multiGet(keyList);
        Map<String, Long> result = new HashMap<>(keyList.size());
        if (values != null) {
            for (int i = 0; i < keyList.size(); i++) {
                Long countValue = values.get(i);
                result.put(keyList.get(i), countValue != null ? countValue : 0L);
            }
        }
        return result;
    }

    @Override
    public void clear(InterfaceTypeEnum interfaceType, String interfaceName) {
        EipLogCountCacheConstant[] constants = EipLogCountCacheConstant.values();
        List<String> keyList = new ArrayList<>(constants.length);
        for (EipLogCountCacheConstant constant : constants) {
            keyList.add(constant.getKeyPrefix(interfaceType, interfaceName));
        }
        redisTemplate.delete(keyList);
    }
}
