package pro.shushi.pamirs.eip.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.eip.api.constant.EipCircuitBreakerConstant;
import pro.shushi.pamirs.eip.api.model.CircuitBreakerRecord;
import pro.shushi.pamirs.eip.api.pmodel.CircuitBreakerRecordProxy;
import pro.shushi.pamirs.eip.api.service.EipCircuitBreakerRecordService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static pro.shushi.pamirs.eip.api.constant.EipCircuitBreakerConstant.CHANNEL_EVENTS;

/**
 * @author yeshenyue on 2025/4/17 10:45.
 */
@Slf4j
@Service
@Fun(EipCircuitBreakerRecordService.FUN_NAMESPACE)
public class EipCircuitBreakerRecordServiceImpl implements EipCircuitBreakerRecordService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Function
    public Pagination<CircuitBreakerRecord> queryPage(Pagination<CircuitBreakerRecord> page, IWrapper<CircuitBreakerRecord> queryWrapper) {
        saveRecord();
        return new CircuitBreakerRecordProxy().queryPage(page, queryWrapper);
    }

    @Override
    @Function
    public void pushRecord(CircuitBreakerRecord record) {
        String recordJson = JsonUtils.toJSONString(record);
        stringRedisTemplate.opsForList().rightPush(CHANNEL_EVENTS, recordJson);
    }


    @Override
    @Function
    public List<CircuitBreakerRecord> findActiveRecords() {
        return Models.data().queryListByWrapper(Pops.<CircuitBreakerRecord>lambdaQuery()
                .from(CircuitBreakerRecord.MODEL_MODEL)
                .gt(CircuitBreakerRecord::getEndTime, new Date())
        );
    }

    @Override
    @Function
    public Boolean findActiveRecordByInterfaceName(String interfaceName) {
        return Models.data().count(Pops.<CircuitBreakerRecord>lambdaQuery()
                .from(CircuitBreakerRecord.MODEL_MODEL)
                .gt(CircuitBreakerRecord::getEndTime, new Date())
                .eq(CircuitBreakerRecord::getInterfaceName, interfaceName)
        ) > 0;
    }

    @Override
    @Function
    public void updateEndTime(List<String> interfaceNames) {
        if (CollectionUtils.isEmpty(interfaceNames)) {
            return;
        }
        saveRecord();
        List<CircuitBreakerRecord> records = Models.data().queryListByWrapper(Pops.<CircuitBreakerRecord>lambdaQuery()
                .from(CircuitBreakerRecord.MODEL_MODEL)
                .gt(CircuitBreakerRecord::getEndTime, new Date())
                .in(CircuitBreakerRecord::getInterfaceName, interfaceNames)
        );
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        Date now = new Date();
        List<CircuitBreakerRecord> updateRecords = new ArrayList<>(records.size());
        for (CircuitBreakerRecord record : records) {
            CircuitBreakerRecord update = new CircuitBreakerRecord();
            update.setId(record.getId());
            update.setEndTime(now);
            updateRecords.add(update);
        }
        Models.data().updateBatch(updateRecords);
    }

    private static final Integer SAVE_BATCH_SIZE = 100;

    @Override
    @Function
    public void saveRecord() {
        boolean lockAcquired = Boolean.TRUE.equals(stringRedisTemplate.opsForValue()
                .setIfAbsent(EipCircuitBreakerConstant.SYNC_SAVE_RECORD_LOCK, "1", 10, TimeUnit.SECONDS));

        if (!lockAcquired) {
            log.warn("无法获取分布式锁，跳过本次同步熔断记录");
            return;
        }

        try {
            List<String> allValues;
            List<CircuitBreakerRecord> recordList = new ArrayList<>(SAVE_BATCH_SIZE);

            // 分批读取
            while (true) {
                allValues = stringRedisTemplate.opsForList().range(CHANNEL_EVENTS, 0, SAVE_BATCH_SIZE - 1);
                if (allValues == null || allValues.isEmpty()) {
                    break;
                }
                recordList.clear();

                for (String value : allValues) {
                    CircuitBreakerRecord record = JsonUtils.parseObject(value, CircuitBreakerRecord.class);
                    if (record != null) {
                        recordList.add(record);
                    } else {
                        log.error("缓存中接口熔断记录为空，跳过创建");
                    }
                }

                if (!recordList.isEmpty()) {
                    new CircuitBreakerRecord().createBatch(recordList);
                }
                stringRedisTemplate.opsForList().trim(CHANNEL_EVENTS, recordList.size(), -1);
            }
        } finally {
            stringRedisTemplate.delete(EipCircuitBreakerConstant.SYNC_SAVE_RECORD_LOCK);
        }
    }
}
