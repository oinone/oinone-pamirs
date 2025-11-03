package pro.shushi.pamirs.eip.api.strategy.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.eip.api.config.PamirsEipProperties;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.AbstractSingleInterface;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.model.statistics.EipLogCount;
import pro.shushi.pamirs.eip.api.strategy.cache.EipLogCountCacheApi;
import pro.shushi.pamirs.eip.api.strategy.constant.EipLogCountCacheConstant;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogCountService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author yeshenyue on 2025/4/10 10:25.
 */
@Service
@Fun(EipLogCountService.FUN_NAMESPACE)
public class EipLogCountServiceImpl implements EipLogCountService {

    private static final EipLogCount EMPTY_EIP_LOG_COUNT = new EipLogCount();

    @Autowired
    private PamirsEipProperties pamirsEipProperties;

    @Override
    @Function
    public void fillEipIntegrationInterfaceLogCount(List<EipIntegrationInterface> eipIntegrationInterfaceList) {
        if (CollectionUtils.isEmpty(eipIntegrationInterfaceList)) {
            return;
        }
        fillLogCount(eipIntegrationInterfaceList, InterfaceTypeEnum.INTEGRATION);
    }

    @Override
    @Function
    public void fillEipOpenInterfaceLogCount(List<EipOpenInterface> eipOpenInterfaceList) {
        if (CollectionUtils.isEmpty(eipOpenInterfaceList)) {
            return;
        }
        fillLogCount(eipOpenInterfaceList, InterfaceTypeEnum.OPEN);
    }

    @Override
    @Function
    public List<EipLogCount> queryListByInterfaceName(InterfaceTypeEnum interfaceType, List<String> interfaceNameList) {
        if (CollectionUtils.isEmpty(interfaceNameList)) {
            return Collections.emptyList();
        }
        return Models.data().queryListByWrapper(Pops.<EipLogCount>lambdaQuery()
                .from(EipLogCount.MODEL_MODEL)
                .eq(EipLogCount::getInterfaceType, interfaceType)
                .in(EipLogCount::getInterfaceName, interfaceNameList)
        );
    }

    /**
     * 填充集成/开放接口日志统计值
     */
    private <T extends AbstractSingleInterface> void fillLogCount(List<T> interfaceList, InterfaceTypeEnum interfaceType) {
        // 1.获取技术名称列表
        List<String> interfaceNames = interfaceList.stream().map(T::getInterfaceName).collect(Collectors.toList());

        // 2.根据技术名称查询统计日志
        List<EipLogCount> eipLogCounts = queryListByInterfaceName(interfaceType, interfaceNames);
        Map<String, EipLogCount> eipLogCountMap;
        if (CollectionUtils.isEmpty(eipLogCounts)) {
            eipLogCountMap = Collections.emptyMap();
        } else {
            eipLogCountMap = eipLogCounts.stream().collect(Collectors.toMap(EipLogCount::getInterfaceName, i -> i));
        }

        // 3.获取缓存日志统计值
        Map<String, Long> cacheCountMap = CommonApiFactory.getApi(EipLogCountCacheApi.class)
                .getCallCount(interfaceType, interfaceNames);

        // 4.组装
        for (T singleInterface : interfaceList) {
            EipLogCount eipLogCount = eipLogCountMap.get(singleInterface.getInterfaceName());
            if (eipLogCount == null) {
                // 填充0
                eipLogCount = EMPTY_EIP_LOG_COUNT;
            }
            // 填充值
            fillCallCount(interfaceType, singleInterface, eipLogCount, cacheCountMap);
        }
    }

    private static <T extends AbstractSingleInterface> void fillCallCount(
            InterfaceTypeEnum interfaceType, T singleInterface, EipLogCount eipLogCount, Map<String, Long> cacheCountMap) {

        String interfaceName = singleInterface.getInterfaceName();

        // 从缓存获取
        Long successCount = fetchCatchValue(cacheCountMap, interfaceName, interfaceType, EipLogCountCacheConstant.SUCCESS);
        Long failCount = fetchCatchValue(cacheCountMap, interfaceName, interfaceType, EipLogCountCacheConstant.FAIL);
        Long ultraFastCount = fetchCatchValue(cacheCountMap, interfaceName, interfaceType, EipLogCountCacheConstant.ULTRA_FAST);
        Long veryFastCount = fetchCatchValue(cacheCountMap, interfaceName, interfaceType, EipLogCountCacheConstant.VERY_FAST);
        Long fastCount = fetchCatchValue(cacheCountMap, interfaceName, interfaceType, EipLogCountCacheConstant.FAST);
        Long moderateCount = fetchCatchValue(cacheCountMap, interfaceName, interfaceType, EipLogCountCacheConstant.MODERATE);
        Long slowCount = fetchCatchValue(cacheCountMap, interfaceName, interfaceType, EipLogCountCacheConstant.SLOW);
        Long verySlowCount = fetchCatchValue(cacheCountMap, interfaceName, interfaceType, EipLogCountCacheConstant.VERY_SLOW);
        Long slowestCount = fetchCatchValue(cacheCountMap, interfaceName, interfaceType, EipLogCountCacheConstant.SLOWEST);
        Long timeoutCount = fetchCatchValue(cacheCountMap, interfaceName, interfaceType, EipLogCountCacheConstant.TIMEOUT);

        // 缓存+DB
        successCount += eipLogCount.getSuccessCallCount();
        failCount += eipLogCount.getFailCallCount();

        singleInterface.setCallCount(successCount + failCount);
        singleInterface.setSuccessCallCount(successCount);
        singleInterface.setFailCallCount(failCount);
        singleInterface.setUltraFastCall(ultraFastCount + eipLogCount.getUltraFastCall());
        singleInterface.setVeryFastCall(veryFastCount + eipLogCount.getVeryFastCall());
        singleInterface.setFastCall(fastCount + eipLogCount.getFastCall());
        singleInterface.setModerateCall(moderateCount + eipLogCount.getModerateCall());
        singleInterface.setSlowCall(slowCount + eipLogCount.getSlowCall());
        singleInterface.setVerySlowCall(verySlowCount + eipLogCount.getVerySlowCall());
        singleInterface.setSlowestCall(slowestCount + eipLogCount.getSlowestCall());
        singleInterface.setTimeoutCall(timeoutCount + eipLogCount.getTimeoutCall());
    }

    private static Long fetchCatchValue(Map<String, Long> cacheCountMap, String interfaceName,
                                        InterfaceTypeEnum interfaceType, EipLogCountCacheConstant cacheConstant) {
        return cacheCountMap.getOrDefault(cacheConstant.getKeyPrefix(interfaceType, interfaceName), 0L);
    }

    @Override
    @Function
    public void syncEipLogCount() {
        Long logAllCount = new EipLog().count();
        if (logAllCount == null || logAllCount == 0L) {
            return;
        }

        // 获取所有的接口名称
        Set<String> interfaceNameAllList = getInterfaceNames(EipIntegrationInterface.MODEL_MODEL);
        Set<String> openInterfaceNameAllList = getInterfaceNames(EipOpenInterface.MODEL_MODEL);

        // 同步接口统计日志
        processInterfaceLogs(interfaceNameAllList, InterfaceTypeEnum.INTEGRATION);
        processInterfaceLogs(openInterfaceNameAllList, InterfaceTypeEnum.OPEN);
    }

    private Set<String> getInterfaceNames(String model) {
        return Models.data().queryListByWrapper(Pops.<AbstractSingleInterface>lambdaQuery()
                .from(model)
                .isNotNull(AbstractSingleInterface::getInterfaceName)
                .groupBy(AbstractSingleInterface::getInterfaceName)
                .select(AbstractSingleInterface::getInterfaceName)
        ).stream().map(AbstractSingleInterface::getInterfaceName).collect(Collectors.toSet());
    }

    private void processInterfaceLogs(Set<String> interfaceNames, InterfaceTypeEnum interfaceType) {
        for (String interfaceName : interfaceNames) {
            long allCount = new EipLog().count(Pops.<EipLog>lambdaQuery()
                    .from(EipLog.MODEL_MODEL)
                    .eq(EipLog::getInterfaceName, interfaceName)
                    .eq(EipLog::getInterfaceType, interfaceType));

            EipLogCount logCount = new EipLogCount();
            logCount.setInterfaceName(interfaceName);
            logCount.setInterfaceType(interfaceType);

            long[] callTimes = new long[8];

            LambdaQueryWrapper<EipLog> query = Pops.<EipLog>lambdaQuery().from(EipLog.MODEL_MODEL)
                    .select(EipLog::getInvokeDate, EipLog::getInvokeEndDate)
                    .isNotNull(EipLog::getInvokeEndDate)
                    .isNotNull(EipLog::getInvokeDate)
                    .eq(EipLog::getInterfaceName, interfaceName)
                    .eq(EipLog::getInterfaceType, interfaceType)
                    .eq(EipLog::getIsSuccess, true);

            AtomicLong successCount = new AtomicLong(0);
            processEipLogPagedData(query, eipLogList -> {
                successCount.addAndGet(eipLogList.size());
                for (EipLog eipLog : eipLogList) {
                    long invokeTime = eipLog.getInvokeEndDate().getTime() - eipLog.getInvokeDate().getTime();
                    updateCallTimes(invokeTime, callTimes);
                }
            });

            long successTotal = successCount.get();
            long failTotal = allCount - successTotal;

            // 删除缓存
            CommonApiFactory.getApi(EipLogCountCacheApi.class).clear(interfaceType, interfaceName);

            logCount.setSuccessCallCount(successTotal);
            logCount.setFailCallCount(failTotal);
            logCount.setUltraFastCall(callTimes[0]);
            logCount.setVeryFastCall(callTimes[1]);
            logCount.setFastCall(callTimes[2]);
            logCount.setModerateCall(callTimes[3]);
            logCount.setSlowCall(callTimes[4]);
            logCount.setVerySlowCall(callTimes[5]);
            logCount.setSlowestCall(callTimes[6]);
            logCount.setTimeoutCall(callTimes[7]);
            logCount.createOrUpdate();
        }
    }

    private void updateCallTimes(long invokeTime, long[] callTimes) {
        if (invokeTime < 100L) {
            callTimes[0]++;
        } else if (invokeTime < 300L) {
            callTimes[1]++;
        } else if (invokeTime < 500L) {
            callTimes[2]++;
        } else if (invokeTime < 1000L) {
            callTimes[3]++;
        } else if (invokeTime < 3000L) {
            callTimes[4]++;
        } else if (invokeTime < 8000L) {
            callTimes[5]++;
        } else if (invokeTime < 30000L) {
            callTimes[6]++;
        } else {
            callTimes[7]++;
        }
    }

    private void processEipLogPagedData(IWrapper<EipLog> query, Consumer<List<EipLog>> consumer) {
        Pagination<EipLog> pageRequest = new Pagination<>();
        pageRequest.setSize(pamirsEipProperties.getLogCountMaxPageSize());
        pageRequest.setSortable(false);
        pageRequest.setCurrentPage(1);
        Integer totalPages;
        do {
            Pagination<EipLog> pagination = new EipLog().queryPage(pageRequest, query);
            List<EipLog> contents = pagination.getContent();
            if (CollectionUtils.isEmpty(contents)) {
                return;
            }
            consumer.accept(contents);
            totalPages = pagination.getTotalPages();
            pageRequest.setCurrentPage(pageRequest.getCurrentPage() + 1);
        } while (pageRequest.getCurrentPage() <= totalPages);
    }
}
