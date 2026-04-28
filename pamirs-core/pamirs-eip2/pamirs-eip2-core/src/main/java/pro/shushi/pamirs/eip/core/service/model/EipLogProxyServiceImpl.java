package pro.shushi.pamirs.eip.core.service.model;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.enmu.EipRetryStatusEnum;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.AbstractSingleInterface;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.pmodel.EipLogProxy;
import pro.shushi.pamirs.eip.api.service.EipLogRetryService;
import pro.shushi.pamirs.eip.api.service.model.EipLogProxyService;
import pro.shushi.pamirs.eip.api.strategy.cache.EipLongRedisTemplate;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.util.PStringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EipLogProxyServiceImpl implements EipLogProxyService {

    @Autowired
    @Qualifier(EipLongRedisTemplate.REDIS_TEMPLATE_BEAN_NAME)
    private EipLongRedisTemplate eipLongRedisTemplate;

    @Override
    public <T extends EipLogProxy> Pagination<T> queryPage(Pagination<T> page, IWrapper<T> queryWrapper) {
        queryWrapper = convertQueryWrapper((QueryWrapper<T>) queryWrapper);
        if (queryWrapper == null) {
            return page;
        }
        Pagination<T> result = Models.origin().queryPage(page, queryWrapper);
        List<T> resultList = result.getContent();
        if (CollectionUtils.isEmpty(resultList)) {
            return result;
        }
        outConvert(resultList);
        return result;
    }

    private <T extends EipLogProxy> QueryWrapper<T> convertQueryWrapper(QueryWrapper<T> queryWrapper) {
        Map<String, Object> queryData = queryWrapper.getQueryData();
        if (queryData == null || queryData.size() == 0) {
            return queryWrapper;
        }
        String interfaceDisplayName = (String) queryData.get(LambdaUtil.fetchFieldName(EipLogProxy::getInterfaceDisplayName));
        String interfaceDescription = (String) queryData.get(LambdaUtil.fetchFieldName(EipLogProxy::getInterfaceDescription));

        if (StringUtils.isNotEmpty(interfaceDisplayName) || StringUtils.isNotEmpty(interfaceDescription)) {
            LambdaQueryWrapper<EipIntegrationInterface> integrationInterfaceLambdaQueryWrapper = Pops.<EipIntegrationInterface>lambdaQuery()
                    .from(EipIntegrationInterface.MODEL_MODEL)
                    .select(EipIntegrationInterface::getInterfaceName);
            LambdaQueryWrapper<EipOpenInterface> openInterfaceLambdaQueryWrapper = Pops.<EipOpenInterface>lambdaQuery()
                    .from(EipOpenInterface.MODEL_MODEL)
                    .select(EipOpenInterface::getInterfaceName);
            if (StringUtils.isNotEmpty(interfaceDisplayName)) {
                integrationInterfaceLambdaQueryWrapper.like(EipIntegrationInterface::getName, interfaceDisplayName);
                openInterfaceLambdaQueryWrapper.like(EipOpenInterface::getName, interfaceDisplayName);
            }
            if (StringUtils.isNotEmpty(interfaceDescription)) {
                integrationInterfaceLambdaQueryWrapper.like(EipIntegrationInterface::getDescription, interfaceDescription);
                openInterfaceLambdaQueryWrapper.like(EipOpenInterface::getDescription, interfaceDescription);
            }

            Set<String> interfaceNameList = new HashSet<>();
            interfaceNameList.addAll(
                    Models.origin().queryListByWrapper(integrationInterfaceLambdaQueryWrapper).stream().map(EipIntegrationInterface::getInterfaceName).collect(Collectors.toSet())
            );
            interfaceNameList.addAll(
                    Models.origin().queryListByWrapper(openInterfaceLambdaQueryWrapper).stream().map(EipOpenInterface::getInterfaceName).collect(Collectors.toSet())
            );

            if (CollectionUtils.isEmpty(interfaceNameList)) {
                return null;
            } else {
                queryWrapper.in(
                        PStringUtils.fieldName2Column(LambdaUtil.fetchFieldName(EipLogProxy::getInterfaceName)),
                        interfaceNameList
                );
            }
        }
        return queryWrapper;
    }

    @Override
    public <T extends EipLogProxy> T queryOne(T query) {
        T result = query.queryOne();
        if (result == null) {
            return result;
        }
        outConvert(Collections.singletonList(result));
        return result;
    }

    private <T extends EipLogProxy> void outConvert(List<T> eipLogProxyList) {
        if (CollectionUtils.isEmpty(eipLogProxyList)) {
            return;
        }
        // 填充接口数据
        fetchInterface(eipLogProxyList);
        for (EipLogProxy eipLogProxy : eipLogProxyList) {
            if (eipLogProxy.getInvokeDate() != null && eipLogProxy.getInvokeEndDate() != null) {
                eipLogProxy.setInvokeMillisecond(eipLogProxy.getInvokeEndDate().getTime() - eipLogProxy.getInvokeDate().getTime());
            }
            if (eipLogProxy.getEipInterface() != null) {
                AbstractSingleInterface eipInterface = eipLogProxy.getEipInterface();
                eipLogProxy.setInterfaceDisplayName(eipInterface.getName());
                eipLogProxy.setInterfaceDescription(eipInterface.getDescription());
                eipLogProxy.setInterfaceUri(eipInterface.getUri());
                eipLogProxy.setInterfaceModuleDefinition(eipInterface.getModuleDefinition());
                eipLogProxy.setInterfaceConnGroup(eipInterface.getConnGroup());
            }
        }
        fillLogStatus(eipLogProxyList);
        for (EipLogProxy proxy : eipLogProxyList) {
            int successCount = Optional.ofNullable(proxy.getRetrySuccessCount()).orElse(0);
            int failCount = Optional.ofNullable(proxy.getRetryFailCount()).orElse(0);
            proxy.setRetryCount(successCount + failCount);
            proxy.setRetrySuccessCount(successCount);
            proxy.setRetryFailCount(failCount);
        }
    }

    private <T extends EipLogProxy> void fillLogStatus(List<T> eipLogProxyList) {
        List<String> lockKeys = new ArrayList<>();
        for (EipLogProxy proxy : eipLogProxyList) {
            lockKeys.add(EipLogRetryService.RETRY_LOCK_KEY_PREFIX + proxy.getId());
        }
        List<Long> lockValues = null;
        try {
            lockValues = eipLongRedisTemplate.opsForValue().multiGet(lockKeys);
        } catch (Exception e) {
            log.warn("Failed to fetch retry lock status, falling back to 'isSuccess': {}", e.getMessage());
        }
        for (int i = 0; i < eipLogProxyList.size(); i++) {
            EipLogProxy proxy = eipLogProxyList.get(i);
            boolean locked = lockValues != null && lockValues.get(i) != null;
            if (locked) {
                proxy.setLogStatus(EipRetryStatusEnum.RETRYING);
            } else if (Boolean.TRUE.equals(proxy.getIsSuccess())) {
                proxy.setLogStatus(EipRetryStatusEnum.SUCCESS);
            } else if (Boolean.FALSE.equals(proxy.getIsSuccess())) {
                proxy.setLogStatus(EipRetryStatusEnum.FAILURE);
            }
        }
    }

    private <T extends EipLogProxy> void fetchInterface(List<T> eipLogProxyList) {
        List<String> integrationInterfaceNames = new ArrayList<>();
        List<String> openInterfaceNames = new ArrayList<>();
        for (EipLogProxy eipLogProxy : eipLogProxyList) {
            if (StringUtils.isEmpty(eipLogProxy.getInterfaceName())) {
                continue;
            }
            if (InterfaceTypeEnum.OPEN.equals(eipLogProxy.getInterfaceType())) {
                openInterfaceNames.add(eipLogProxy.getInterfaceName());
            } else {
                integrationInterfaceNames.add(eipLogProxy.getInterfaceName());
            }
        }

        List<EipIntegrationInterface> integrationInterfaces = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(integrationInterfaceNames)) {
            integrationInterfaces = Models.data().queryListByWrapper(
                    Pops.<EipIntegrationInterface>lambdaQuery()
                            .from(EipIntegrationInterface.MODEL_MODEL)
                            .in(EipIntegrationInterface::getInterfaceName, integrationInterfaceNames)
            );
        }
        List<EipOpenInterface> openInterfaces = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(openInterfaceNames)) {
            openInterfaces = Models.data().queryListByWrapper(
                    Pops.<EipOpenInterface>lambdaQuery()
                            .from(EipOpenInterface.MODEL_MODEL)
                            .in(EipOpenInterface::getInterfaceName, openInterfaceNames)
            );
        }

        if (CollectionUtils.isNotEmpty(integrationInterfaces)) {
            Models.data().listFieldQuery(integrationInterfaces, EipIntegrationInterface::getModuleDefinition);
            Models.data().listFieldQuery(integrationInterfaces, EipIntegrationInterface::getConnGroup);
        }
        if (CollectionUtils.isNotEmpty(openInterfaces)) {
            Models.data().listFieldQuery(openInterfaces, EipOpenInterface::getModuleDefinition);
            Models.data().listFieldQuery(openInterfaces, EipOpenInterface::getConnGroup);
        }

        Map<String, EipIntegrationInterface> integrationInterfaceMap = integrationInterfaces.stream()
                .collect(Collectors.toMap(EipIntegrationInterface::getInterfaceName, i -> i, (a, b) -> a));
        Map<String, EipOpenInterface> openInterfaceMap = openInterfaces.stream()
                .collect(Collectors.toMap(EipOpenInterface::getInterfaceName, i -> i, (a, b) -> a));

        for (EipLogProxy eipLogProxy : eipLogProxyList) {
            if (StringUtils.isEmpty(eipLogProxy.getInterfaceName())) {
                continue;
            }
            if (InterfaceTypeEnum.OPEN.equals(eipLogProxy.getInterfaceType())) {
                eipLogProxy.setEipInterface(openInterfaceMap.get(eipLogProxy.getInterfaceName()));
            } else {
                eipLogProxy.setEipInterface(integrationInterfaceMap.get(eipLogProxy.getInterfaceName()));
            }
        }
    }
}
