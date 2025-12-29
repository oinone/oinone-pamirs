package pro.shushi.pamirs.eip.api.strategy.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.modules.pmodel.AppSwitcherModuleProxy;
import pro.shushi.pamirs.eip.api.config.PamirsEipProperties;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.AbstractSingleInterface;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.model.statistics.EipLogCount;
import pro.shushi.pamirs.eip.api.model.statistics.EipLogDailyCount;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogDailyCountService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

/**
 * @author yeshenyue on 2025/11/6 10:49.
 */
@Slf4j
@Service
@Fun(EipLogDailyCountService.FUN_NAMESPACE)
public class EipLogDailyCountServiceImpl implements EipLogDailyCountService {

    private static final ZoneId ZONE = ZoneId.systemDefault();

    private static final List<EipLogDailyCount> EMPTY_EIP_LOG_DAILY_COUNT = new ArrayList<EipLogDailyCount>();

    @Autowired
    private PamirsEipProperties pamirsEipProperties;

    @Function
    @Override
    public void syncYesterday() {
        if (Boolean.FALSE.equals(pamirsEipProperties.getEnableLogCount())) {
            return;
        }
        LocalDate yesterdayLocalDate = LocalDate.now(ZONE).minusDays(1);
        Date yesterdayStart = Date.from(yesterdayLocalDate.atStartOfDay(ZONE).toInstant());
        Date yesterdayEnd = Date.from(yesterdayLocalDate.plusDays(1).atStartOfDay(ZONE).toInstant());
        syncDateRange(yesterdayStart, yesterdayEnd);
    }

    @Function
    @Override
    public List<EipIntegrationInterface> fillIntegrationLogCountData(List<EipIntegrationInterface> eipIntegrationInterfaceList,Date start, Date end) {
        if (CollectionUtils.isEmpty(eipIntegrationInterfaceList)) {
            return eipIntegrationInterfaceList;
        }
        fillLogCount(eipIntegrationInterfaceList, InterfaceTypeEnum.INTEGRATION,start, end);
        return eipIntegrationInterfaceList;
    }

    @Function
    @Override
    public List<EipOpenInterface> fillOpenLogCountData(List<EipOpenInterface> eipOpenInterfaceList,Date start, Date end) {
        if (CollectionUtils.isEmpty(eipOpenInterfaceList)) {
            return eipOpenInterfaceList;
        }
        fillLogCount(eipOpenInterfaceList, InterfaceTypeEnum.OPEN,start, end);
        return eipOpenInterfaceList;
    }

    private <T extends AbstractSingleInterface> void fillLogCount(List<T> interfaceList, InterfaceTypeEnum interfaceType,Date start, Date end) {
        List<String> interfaceNames = interfaceList.stream().map(T::getInterfaceName).collect(Collectors.toList());
        boolean isDateFilter = start != null && end != null;

        QueryWrapper<EipLogDailyCount> itemWrapper = Pops.query();
        itemWrapper.setModel(EipLogDailyCount.MODEL_MODEL);
        itemWrapper.eq("interface_type", interfaceType);
        itemWrapper.in("interface_name", interfaceNames);
        if(isDateFilter) {
            itemWrapper.ge("count_date", start);
            itemWrapper.lt("count_date", end);
        }
        itemWrapper.groupBy("interface_name");
        itemWrapper.select("interface_name as interfaceName,sum(success_call_count) as successCallCount,sum(fail_call_count) as failCallCount," +
                "sum(ultra_fast_call) as ultraFastCall,sum(very_fast_call) as veryFastCall,sum(fast_call) as fastCall," +
                "sum(moderate_call) as moderateCall,sum(slow_call) as slowCall,sum(very_slow_call) as verySlowCall," +
                "sum(slowest_call) as slowestCall,sum(timeout_call) as timeoutCall");

        List<EipLogDailyCount> eipLogCounts = Models.data().queryListByWrapper(itemWrapper);

        Map<String, List<EipLogDailyCount>> eipLogCountMap;
        if (CollectionUtils.isEmpty(eipLogCounts)) {
            eipLogCountMap = Collections.emptyMap();
        } else {
            eipLogCountMap = eipLogCounts.stream().collect(Collectors.groupingBy(EipLogDailyCount::getInterfaceName));
        }
        // 3.组装
        for (T singleInterface : interfaceList) {
            List<EipLogDailyCount> eipLogCountList = eipLogCountMap.getOrDefault(singleInterface.getInterfaceName(), EMPTY_EIP_LOG_DAILY_COUNT);
            if(CollectionUtils.isNotEmpty(eipLogCountList)){
                long successCount = eipLogCountList.stream().mapToLong(EipLogDailyCount::getSuccessCallCount).sum();
                long failCount = eipLogCountList.stream().mapToLong(EipLogDailyCount::getFailCallCount).sum();

                singleInterface.setCallCount(successCount + failCount);
                singleInterface.setSuccessCallCount(successCount);
                singleInterface.setFailCallCount(failCount);

                singleInterface.setUltraFastCall(eipLogCountList.stream().mapToLong(EipLogDailyCount::getUltraFastCall).sum());
                singleInterface.setVeryFastCall(eipLogCountList.stream().mapToLong(EipLogDailyCount::getVeryFastCall).sum());
                singleInterface.setFastCall(eipLogCountList.stream().mapToLong(EipLogDailyCount::getFastCall).count());
                singleInterface.setModerateCall(eipLogCountList.stream().mapToLong(EipLogDailyCount::getModerateCall).sum());
                singleInterface.setSlowCall(eipLogCountList.stream().mapToLong(EipLogDailyCount::getSlowCall).sum());
                singleInterface.setVerySlowCall(eipLogCountList.stream().mapToLong(EipLogDailyCount::getVerySlowCall).sum());
                singleInterface.setSlowestCall(eipLogCountList.stream().mapToLong(EipLogDailyCount::getSlowestCall).sum());
                singleInterface.setTimeoutCall(eipLogCountList.stream().mapToLong(EipLogDailyCount::getTimeoutCall).sum());
            }else{
                singleInterface.setCallCount(0L);
                singleInterface.setSuccessCallCount(0L);
                singleInterface.setFailCallCount(0L);
                singleInterface.setUltraFastCall(0L);
                singleInterface.setVeryFastCall(0L);
                singleInterface.setFastCall(0L);
                singleInterface.setModerateCall(0L);
                singleInterface.setSlowCall(0L);
                singleInterface.setVerySlowCall(0L);
                singleInterface.setSlowestCall(0L);
                singleInterface.setTimeoutCall(0L);
            }
        }

    }

    /**
     * 同步指定日期范围的统计数据
     */
    private void syncDateRange(Date start, Date end) {
        // 查询时间范围内存在日志的接口技术名称
        IWrapper<EipLog> query = Pops.<EipLog>lambdaQuery().from(EipLog.MODEL_MODEL)
                .select(EipLog::getInterfaceType, EipLog::getInterfaceName)
                .isNotNull(EipLog::getInterfaceType)
                .isNotNull(EipLog::getInterfaceName)
                .between(EipLog::getCreateDate, start, end)
                .groupBy(EipLog::getInterfaceType)
                .groupBy(EipLog::getInterfaceName)
                .setSortable(false);
        List<EipLog> interfaces = new EipLog().queryList(query);
        if (CollectionUtils.isEmpty(interfaces)) {
            return;
        }

        // 按接口类型分组
        List<String> openInterfaces = new ArrayList<>();
        List<String> integrationInterfaces = new ArrayList<>();
        for (EipLog log : interfaces) {
            if (InterfaceTypeEnum.OPEN.equals(log.getInterfaceType())) {
                openInterfaces.add(log.getInterfaceName());
            } else if (InterfaceTypeEnum.INTEGRATION.equals(log.getInterfaceType())) {
                integrationInterfaces.add(log.getInterfaceName());
            }
        }

        // 同步接口统计数据
        syncInterfaceStatistics(integrationInterfaces, pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum.INTEGRATION, start, end);
        syncInterfaceStatistics(openInterfaces, InterfaceTypeEnum.OPEN, start, end);
    }

    /**
     * 同步接口统计数据
     *
     * @param interfaceNames 接口名称列表
     * @param interfaceType 接口类型
     * @param start 开始时间
     * @param end 结束时间
     */
    private void syncInterfaceStatistics(List<String> interfaceNames, InterfaceTypeEnum interfaceType, Date start, Date end) {
        if (CollectionUtils.isEmpty(interfaceNames) || interfaceType == null) {
            return;
        }
        if (start == null || end == null) {
            log.error("eip log count sync error: start or end is null, start={}, end={}", start, end);
            return;
        }

        List<EipLogDailyCount> dailyCounts = new ArrayList<>();
        for (String interfaceName : interfaceNames) {
            long allCount = new EipLog().count(Pops.<EipLog>lambdaQuery()
                    .from(EipLog.MODEL_MODEL)
                    .between(EipLog::getCreateDate, start, end)
                    .eq(EipLog::getInterfaceName, interfaceName)
                    .eq(EipLog::getInterfaceType, interfaceType));

            long[] callTimes = new long[8];

            LambdaQueryWrapper<EipLog> query = Pops.<EipLog>lambdaQuery().from(EipLog.MODEL_MODEL)
                    .select(EipLog::getInvokeDate, EipLog::getInvokeEndDate)
                    .isNotNull(EipLog::getInvokeEndDate)
                    .isNotNull(EipLog::getInvokeDate)
                    .between(EipLog::getCreateDate, start, end)
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

            EipLogDailyCount dailyCount = new EipLogDailyCount();
            dailyCount.setInterfaceName(interfaceName);
            dailyCount.setInterfaceType(interfaceType);
            dailyCount.setCountDate(start);
            dailyCount.setSuccessCallCount(successTotal);
            dailyCount.setFailCallCount(failTotal);
            dailyCount.setUltraFastCall(callTimes[0]);
            dailyCount.setVeryFastCall(callTimes[1]);
            dailyCount.setFastCall(callTimes[2]);
            dailyCount.setModerateCall(callTimes[3]);
            dailyCount.setSlowCall(callTimes[4]);
            dailyCount.setVerySlowCall(callTimes[5]);
            dailyCount.setSlowestCall(callTimes[6]);
            dailyCount.setTimeoutCall(callTimes[7]);
            dailyCounts.add(dailyCount);
        }

        if (!dailyCounts.isEmpty()) {
            new EipLogDailyCount().createOrUpdateBatch(dailyCounts);
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
