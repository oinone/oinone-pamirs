package pro.shushi.pamirs.eip.api.strategy.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.AbstractSingleInterface;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.model.statistics.EipLogCount;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogCountService;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogDailyCountService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yeshenyue on 2025/4/10 10:25.
 */
@Slf4j
@Service
@Fun(EipLogCountService.FUN_NAMESPACE)
public class EipLogCountServiceImpl implements EipLogCountService {

    private static final EipLogCount EMPTY_EIP_LOG_COUNT = new EipLogCount();

    @Autowired
    private EipLogDailyCountService eipLogDailyCountService;

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

        // 3.组装
        for (T singleInterface : interfaceList) {
            EipLogCount eipLogCount = eipLogCountMap.getOrDefault(singleInterface.getInterfaceName(), EMPTY_EIP_LOG_COUNT);
            long successCount = eipLogCount.getSuccessCallCount();
            long failCount = eipLogCount.getFailCallCount();

            singleInterface.setCallCount(successCount + failCount);
            singleInterface.setSuccessCallCount(successCount);
            singleInterface.setFailCallCount(failCount);

            singleInterface.setUltraFastCall(eipLogCount.getUltraFastCall());
            singleInterface.setVeryFastCall(eipLogCount.getVeryFastCall());
            singleInterface.setFastCall(eipLogCount.getFastCall());
            singleInterface.setModerateCall(eipLogCount.getModerateCall());
            singleInterface.setSlowCall(eipLogCount.getSlowCall());
            singleInterface.setVerySlowCall(eipLogCount.getVerySlowCall());
            singleInterface.setSlowestCall(eipLogCount.getSlowestCall());
            singleInterface.setTimeoutCall(eipLogCount.getTimeoutCall());
        }
    }

    @Deprecated
    @Override
    @Function
    public void syncEipLogCount() {
        log.error("Full synchronization of interface log statistics has been deprecated");
        eipLogDailyCountService.syncYesterday();
    }
}
