package pro.shushi.pamirs.eip.api.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.eip.api.enmu.CircuitBreakerStatusEnum;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.manager.CircuitBreakerManager;
import pro.shushi.pamirs.eip.api.model.CircuitBreakerRecord;
import pro.shushi.pamirs.eip.api.model.EipCircuitBreakerRule;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.pmodel.EipCircuitBreakerRuleProxy;
import pro.shushi.pamirs.eip.api.service.EipCircuitBreakerStateSyncService;
import pro.shushi.pamirs.eip.api.service.EipCircuitBreakerRecordService;
import pro.shushi.pamirs.eip.api.service.EipCircuitBreakerRuleService;
import pro.shushi.pamirs.eip.api.service.model.EipIntegrationInterfaceService;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author yeshenyue on 2025/4/16 15:01.
 */
@Slf4j
@Service
@Fun(EipCircuitBreakerRuleService.FUN_NAMESPACE)
public class EipCircuitBreakerRuleServiceImpl implements EipCircuitBreakerRuleService {

    @Autowired
    private CircuitBreakerManager circuitBreakerManager;
    @Autowired
    private EipIntegrationInterfaceService eipIntegrationInterfaceService;
    @Autowired
    private EipCircuitBreakerStateSyncService eipCircuitBreakerStateSyncService;
    @Autowired
    private EipCircuitBreakerRecordService eipCircuitBreakerRecordService;

    @Override
    @Function
    public void register(String interfaceName) {
        EipIntegrationInterface integrationInterface = eipIntegrationInterfaceService.queryByInterfaceName(interfaceName);
        if (integrationInterface == null) {
            throw PamirsException.construct(EipExpEnumerate.INTEGRATION_INTERFACE_NULL_ERROR).errThrow();
        }

        if (StringUtils.isBlank(integrationInterface.getCircuitBreakerRuleCode())) {
            log.warn("熔断器注销，interfaceName:{}", interfaceName);
            circuitBreakerManager.unregister(interfaceName);
        } else {
            log.warn("熔断器注册，interfaceName：{}", interfaceName);
            integrationInterface.fieldQuery(EipIntegrationInterface::getCircuitBreakerRule);
            EipCircuitBreakerRule circuitBreakerRule = integrationInterface.getCircuitBreakerRule();
            circuitBreakerManager.registerCircuitBreaker(interfaceName, circuitBreakerRule);

            Boolean isOpen = eipCircuitBreakerRecordService.findActiveRecordByInterfaceName(interfaceName);
            if (isOpen) {
                circuitBreakerManager.updateState(interfaceName, CircuitBreakerStatusEnum.OPEN);
            }
        }
    }

    @Override
    @Function
    public void create(EipCircuitBreakerRule data) {
        data.create();
        data.fieldSave(EipCircuitBreakerRule::getIntegrationInterfaceList);
        data.fieldQuery(EipCircuitBreakerRule::getIntegrationInterfaceList);
        refreshAndRegisterInterfaces(data);
    }

    @Override
    @Function
    public Integer update(EipCircuitBreakerRule data) {
        AtomicReference<Integer> result = new AtomicReference<>();
        List<String> removedInterfaceList = Tx.build().execute(status -> {
            // 获取需要注销的接口，并删除原关联关系
            List<String> removedInterfaces = reconcileCircuitBreakerInterfaces(data);
            data.fieldSave(EipCircuitBreakerRule::getIntegrationInterfaceList);
            result.set(data.updateById());
            return removedInterfaces;
        });
        // 注销接口
        unregisterRemovedInterfaces(removedInterfaceList);
        // 刷新接口
        refreshAndRegisterInterfaces(data);
        return result.get();
    }

    @Override
    @Function
    @Transactional
    public void deleteOne(EipCircuitBreakerRule eipCircuitBreakerRule) {
        eipCircuitBreakerRule.fieldQuery(EipCircuitBreakerRuleProxy::getIntegrationInterfaceList);
        List<EipIntegrationInterface> integrationInterfaceList = eipCircuitBreakerRule.getIntegrationInterfaceList();
        eipCircuitBreakerRule.relationDelete(EipCircuitBreakerRuleProxy::getIntegrationInterfaceList);
        eipCircuitBreakerRule.deleteById();

        if (CollectionUtils.isEmpty(integrationInterfaceList)) {
            return;
        }
        List<String> interfaceNameList = integrationInterfaceList.stream()
                .map(EipIntegrationInterface::getInterfaceName)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        eipCircuitBreakerRecordService.updateEndTime(interfaceNameList);

        for (String interfaceName : interfaceNameList) {
            circuitBreakerManager.unregister(interfaceName);
            eipCircuitBreakerStateSyncService.handleRemove(interfaceName);
        }
    }

    @Override
    @Function
    public void init() {
        List<EipCircuitBreakerRule> ruleList = new EipCircuitBreakerRule().queryList();
        if (CollectionUtils.isNotEmpty(ruleList)) {
            Models.data().listFieldQuery(ruleList, EipCircuitBreakerRule::getIntegrationInterfaceList);

            eipCircuitBreakerRecordService.saveRecord();

            // 查询仍处于熔断期间的记录
            List<CircuitBreakerRecord> activeRecords = eipCircuitBreakerRecordService.findActiveRecords();
            Map<String, CircuitBreakerRecord> activeMap = Optional.ofNullable(activeRecords)
                    .orElse(Collections.emptyList())
                    .stream()
                    .collect(Collectors.toMap(CircuitBreakerRecord::getInterfaceName, a -> a, (a, b) -> a));

            for (EipCircuitBreakerRule rule : ruleList) {
                List<EipIntegrationInterface> interfaceList = rule.getIntegrationInterfaceList();
                if (CollectionUtils.isEmpty(interfaceList)) {
                    continue;
                }

                for (EipIntegrationInterface eipInterface : interfaceList) {
                    String interfaceName = eipInterface.getInterfaceName();
                    if (StringUtils.isBlank(interfaceName)) {
                        log.error("熔断器注册失败，接口技术名称为空");
                        continue;
                    }

                    // 注册熔断器配置
                    circuitBreakerManager.registerCircuitBreaker(interfaceName, rule);

                    // 若存在“未结束”的熔断记录，则恢复状态
                    CircuitBreakerRecord record = activeMap.get(interfaceName);
                    CircuitBreakerStatusEnum statusEnum = record == null ?
                            CircuitBreakerStatusEnum.CLOSED : CircuitBreakerStatusEnum.OPEN;
                    if (record != null) {
                        circuitBreakerManager.updateState(interfaceName, statusEnum);
                    }
                }
            }
        }
        eipCircuitBreakerStateSyncService.startListener();
    }

    private List<String> reconcileCircuitBreakerInterfaces(EipCircuitBreakerRule data) {
        // 填充集成接口数据
        List<EipIntegrationInterface> newInterfaces = eipIntegrationInterfaceService.queryByIds(
                data.getIntegrationInterfaceList().stream()
                        .map(EipIntegrationInterface::getId)
                        .collect(Collectors.toList())
        );
        Map<String, EipIntegrationInterface> newInterfaceMap = newInterfaces.stream()
                .collect(Collectors.toMap(EipIntegrationInterface::getInterfaceName, a -> a));
        data.setIntegrationInterfaceList(newInterfaces);

        // 处理更新，拿到原规则适用集成接口列表
        EipCircuitBreakerRule oldRule = new EipCircuitBreakerRule().queryById(data.getId());
        oldRule.fieldQuery(EipCircuitBreakerRule::getIntegrationInterfaceList);

        // 获取旧接口名称集合
        List<EipIntegrationInterface> oldInterfaceList = oldRule.getIntegrationInterfaceList();
        Set<String> oldInterfaceNames = oldInterfaceList.stream()
                .map(EipIntegrationInterface::getInterfaceName)
                .collect(Collectors.toSet());

        // 删除集成接口关系
        oldRule.relationDelete(EipCircuitBreakerRule::getIntegrationInterfaceList);

        // 更新熔断结束时间
        updateCircuitBreakerRecordEndTime(oldInterfaceList);

        // 收集需要注销的旧接口
        return collectRemovedInterfaces(oldInterfaceNames, newInterfaceMap);
    }

    private void unregisterRemovedInterfaces(List<String> interfaceNames) {
        if (CollectionUtils.isEmpty(interfaceNames)) {
            return;
        }
        for (String interfaceName : interfaceNames) {
            circuitBreakerManager.unregister(interfaceName);
            eipCircuitBreakerStateSyncService.handleRemove(interfaceName);
        }
    }

    private List<String> collectRemovedInterfaces(Set<String> oldInterfaceNames, Map<String, EipIntegrationInterface> newInterfaceMap) {
        List<String> removedInterfaces = new ArrayList<>();
        for (String oldInterfaceName : oldInterfaceNames) {
            if (Boolean.FALSE.equals(newInterfaceMap.containsKey(oldInterfaceName))) {
                removedInterfaces.add(oldInterfaceName);
            }
        }
        return removedInterfaces;
    }

    private void updateCircuitBreakerRecordEndTime(List<EipIntegrationInterface> interfaces) {
        List<String> interfaceNames = interfaces.stream()
                .map(EipIntegrationInterface::getInterfaceName)
                .collect(Collectors.toList());
        eipCircuitBreakerRecordService.updateEndTime(interfaceNames);
    }

    private void refreshAndRegisterInterfaces(EipCircuitBreakerRule data) {
        for (EipIntegrationInterface eipInterface : data.getIntegrationInterfaceList()) {
            String interfaceName = eipInterface.getInterfaceName();
            if (StringUtils.isNotBlank(interfaceName)) {
                circuitBreakerManager.unregister(interfaceName);
                circuitBreakerManager.registerCircuitBreaker(interfaceName, data);
                eipCircuitBreakerStateSyncService.handleUpdateConfig(interfaceName);
            } else {
                log.error("熔断器注册失败，interfaceName为空");
            }
        }
    }
}
