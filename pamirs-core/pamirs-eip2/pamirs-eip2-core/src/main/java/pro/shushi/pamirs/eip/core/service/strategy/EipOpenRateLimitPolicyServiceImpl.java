package pro.shushi.pamirs.eip.core.service.strategy;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.enmu.FlowControlEffectTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipApplication;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.model.strategy.EipOpenRateLimitPolicy;
import pro.shushi.pamirs.eip.api.pmodel.EipApplicationProxy;
import pro.shushi.pamirs.eip.api.strategy.limiter.api.OpenRateLimitApi;
import pro.shushi.pamirs.eip.api.strategy.service.EipOpenRateLimitPolicyService;
import pro.shushi.pamirs.eip.api.strategy.service.EipOpenRateLimitStateSyncService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yeshenyue on 2025/4/21 14:38.
 */
@Slf4j
@Service
@Fun(EipOpenRateLimitPolicyService.FUN_NAMESPACE)
public class EipOpenRateLimitPolicyServiceImpl implements EipOpenRateLimitPolicyService {

    @Autowired
    private EipOpenRateLimitStateSyncService eipOpenRateLimitStateSyncService;

    @Override
    @Function
    public void init() {
        // 注册监听
        eipOpenRateLimitStateSyncService.startListener();

        // 初始化流控
        List<EipOpenRateLimitPolicy> policyList = Models.data().queryListByWrapper(Pops.<EipOpenRateLimitPolicy>lambdaQuery()
                .from(EipOpenRateLimitPolicy.MODEL_MODEL)
                .isNotNull(EipOpenRateLimitPolicy::getFlowControlEffect)
                .isNotNull(EipOpenRateLimitPolicy::getQps)
        );
        if (CollectionUtils.isEmpty(policyList)) {
            return;
        }

        Models.data().listFieldQuery(policyList, EipOpenRateLimitPolicy::getApplication);
        for (EipOpenRateLimitPolicy policy : policyList) {
            if (policy.getQps() != null && policy.getFlowControlEffect() != null) {
                Spider.getDefaultExtension(OpenRateLimitApi.class).registerPolicy(policy);
            }
        }
    }

    @Override
    @Function
    @Transactional
    public List<EipOpenRateLimitPolicy> batchModifyFlowControlPolicies(List<EipOpenRateLimitPolicy> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        Models.data().listFieldQuery(dataList, EipOpenRateLimitPolicy::getApplication);
        List<EipOpenRateLimitPolicy> saveDataList = new ArrayList<>();
        List<EipOpenRateLimitPolicy> removeDataList = new ArrayList<>();

        for (EipOpenRateLimitPolicy policy : dataList) {
            // 应用编码和接口技术名称校验
            validateBasicFields(policy);

            Long qps = policy.getQps();
            FlowControlEffectTypeEnum flowControlEffect = policy.getFlowControlEffect();
            Long timeout = policy.getTimeout();

            if (qps == null && flowControlEffect == null && timeout == null) {
                removeDataList.add(policy);
            } else {
                // 必填参数校验
                validateRequireFields(policy, qps, flowControlEffect, timeout);
                saveDataList.add(policy);
            }
        }

        Tx.build().executeWithoutResult(status -> {
            if (!saveDataList.isEmpty()) {
                Models.data().createOrUpdateBatch(saveDataList);
            }
            if (!removeDataList.isEmpty()) {
                Models.data().createOrUpdateBatch(removeDataList);
            }
        });

        // 通知
        for (EipOpenRateLimitPolicy policy : saveDataList) {
            String appKey = policy.getApplication().getAppKey();
            eipOpenRateLimitStateSyncService.handleUpdate(appKey, policy.getInterfaceName());
        }
        for (EipOpenRateLimitPolicy policy : removeDataList) {
            String appKey = policy.getApplication().getAppKey();
            boolean exist = Spider.getDefaultExtension(OpenRateLimitApi.class).isExist(appKey, policy.getInterfaceName());
            if (exist) {
                eipOpenRateLimitStateSyncService.handleRemove(appKey, policy.getInterfaceName());
            }
        }
        return saveDataList;
    }

    @Override
    @Function
    public List<EipOpenRateLimitPolicy> queryListByApplicationCode(EipApplicationProxy applicationProxy) {
        if (applicationProxy == null || StringUtils.isBlank(applicationProxy.getCode())) {
            throw PamirsException.construct(EipExpEnumerate.OPEN_APPLICATION_NOT_EXIST).errThrow();
        }

        final String applicationCode = applicationProxy.getCode();
        List<EipOpenRateLimitPolicy> existingPolicies = queryListByApplicationCode(applicationCode);

        Set<String> policyInterfaceNames = existingPolicies.stream()
                .map(EipOpenRateLimitPolicy::getInterfaceName)
                .collect(Collectors.toSet());

        EipApplicationProxy fullProxy = applicationProxy.fieldQuery(EipApplicationProxy::getOpenInterfaceList);
        List<EipOpenInterface> interfaceList = Optional.ofNullable(fullProxy.getOpenInterfaceList())
                .orElse(Collections.emptyList());

        List<EipOpenRateLimitPolicy> result = new ArrayList<>(existingPolicies);
        for (EipOpenInterface openInterface : interfaceList) {
            String interfaceName = openInterface.getInterfaceName();
            if (!policyInterfaceNames.contains(interfaceName)) {
                EipOpenRateLimitPolicy newPolicy = new EipOpenRateLimitPolicy();
                newPolicy.setApplicationCode(applicationCode);
                newPolicy.setInterfaceName(interfaceName);
                newPolicy.setOpenInterface(openInterface);
                result.add(newPolicy);
            }
        }
        return result;
    }

    @Override
    @Function
    public void removeAll(EipApplication application) {
        if (application == null || StringUtils.isBlank(application.getCode())) {
            throw PamirsException.construct(EipExpEnumerate.OPEN_APPLICATION_NOT_EXIST).errThrow();
        }
        final String applicationCode = application.getCode();
        List<EipOpenRateLimitPolicy> existingPolicies = queryListByApplicationCode(applicationCode);
        if (CollectionUtils.isNotEmpty(existingPolicies)) {
            Models.data().deleteByPk(existingPolicies);
        }
    }

    @Override
    @Function
    public void deleteByInterfaceName(EipApplication application, List<String> interfaceNames) {
        if (application == null || StringUtils.isBlank(application.getCode())) {
            throw PamirsException.construct(EipExpEnumerate.OPEN_APPLICATION_NOT_EXIST).errThrow();
        }
        if (StringUtils.isBlank(application.getAppKey())) {
            application = application.queryOne();
        }
        if (CollectionUtils.isEmpty(interfaceNames)) {
            return;
        }
        List<EipOpenRateLimitPolicy> rateLimitPolicies = Models.data().queryListByWrapper(
                Pops.<EipOpenRateLimitPolicy>lambdaQuery()
                        .from(EipOpenRateLimitPolicy.MODEL_MODEL)
                        .eq(EipOpenRateLimitPolicy::getApplicationCode, application.getCode())
                        .in(EipOpenRateLimitPolicy::getInterfaceName, interfaceNames));

        if (CollectionUtils.isNotEmpty(rateLimitPolicies)) {
            String appKey = application.getAppKey();
            Models.data().deleteByPk(rateLimitPolicies);
            for (EipOpenRateLimitPolicy policy : rateLimitPolicies) {
                eipOpenRateLimitStateSyncService.handleRemove(appKey, policy.getInterfaceName());
            }
        }
    }

    @Override
    @Function
    public void refreshLocal(String appKey, String interfaceName) {
        EipApplication eipApplication = new EipApplication().setAppKey(appKey).queryOne();
        if (eipApplication == null) {
            log.error("Integration application info not found, appKey: {}", appKey);
            return;
        }
        EipOpenRateLimitPolicy rateLimitPolicy = Models.data().queryOneByWrapper(
                Pops.<EipOpenRateLimitPolicy>lambdaQuery()
                        .from(EipOpenRateLimitPolicy.MODEL_MODEL)
                        .eq(EipOpenRateLimitPolicy::getApplicationCode, eipApplication.getCode())
                        .eq(EipOpenRateLimitPolicy::getInterfaceName, interfaceName));
        if (rateLimitPolicy == null) {
            log.warn("Flow control config not found, unregister open interface flow control config, appKey: {}, interfaceName: {}", appKey, interfaceName);
            Spider.getDefaultExtension(OpenRateLimitApi.class).unregisterPolicy(appKey, interfaceName);
            return;
        }
        if (rateLimitPolicy.getQps() == null || rateLimitPolicy.getFlowControlEffect() == null) {
            log.warn("Unregister open interface flow control config, appKey: {}, interfaceName: {}", appKey, interfaceName);
            Spider.getDefaultExtension(OpenRateLimitApi.class).unregisterPolicy(appKey, interfaceName);
        } else {
            log.info("Register open interface flow control config, appKey: {}, interfaceName: {}", appKey, interfaceName);
            Models.data().fieldQuery(rateLimitPolicy, EipOpenRateLimitPolicy::getApplication);
            Spider.getDefaultExtension(OpenRateLimitApi.class).registerPolicy(rateLimitPolicy);
        }
    }

    private List<EipOpenRateLimitPolicy> queryListByApplicationCode(String applicationCode) {
        return Models.data().queryListByWrapper(Pops.<EipOpenRateLimitPolicy>lambdaQuery()
                .from(EipOpenRateLimitPolicy.MODEL_MODEL)
                .eq(EipOpenRateLimitPolicy::getApplicationCode, applicationCode)
        );
    }

    private void validateRequireFields(EipOpenRateLimitPolicy policy, Long qps, FlowControlEffectTypeEnum flowControlEffect, Long timeout) {
        String apiName = resolveInterfaceName(policy);
        if (qps == null) {
            throw PamirsException.construct(EipExpEnumerate.PARAM_RATE_LIMIT_PARAM_NULL)
                    .appendMsg(I18nUtils.getMessage("pamirs.eip.rateLimit.qps.missing", apiName)).errThrow();
        }
        if (flowControlEffect == null) {
            throw PamirsException.construct(EipExpEnumerate.PARAM_RATE_LIMIT_PARAM_NULL)
                    .appendMsg(I18nUtils.getMessage("pamirs.eip.rateLimit.effect.missing", apiName)).errThrow();
        }
        if (FlowControlEffectTypeEnum.QUEUEING_WAIT.equals(flowControlEffect) && timeout == null) {
            throw PamirsException.construct(EipExpEnumerate.PARAM_RATE_LIMIT_PARAM_NULL)
                    .appendMsg(I18nUtils.getMessage("pamirs.eip.rateLimit.timeout.missing", apiName)).errThrow();
        }
    }

    private void validateBasicFields(EipOpenRateLimitPolicy policy) {
        if (StringUtils.isBlank(policy.getApplicationCode())) {
            throw PamirsException.construct(EipExpEnumerate.PARAM_APPLICATION_NULL).errThrow();
        }
        if (StringUtils.isBlank(policy.getInterfaceName())) {
            throw PamirsException.construct(EipExpEnumerate.PARAM_OPEN_INTERFACE_NULL).errThrow();
        }
    }

    private String resolveInterfaceName(EipOpenRateLimitPolicy policy) {
        if (policy.getOpenInterface() != null && StringUtils.isNotBlank(policy.getOpenInterface().getName())) {
            return policy.getOpenInterface().getName();
        }
        return policy.getInterfaceName();
    }
}
