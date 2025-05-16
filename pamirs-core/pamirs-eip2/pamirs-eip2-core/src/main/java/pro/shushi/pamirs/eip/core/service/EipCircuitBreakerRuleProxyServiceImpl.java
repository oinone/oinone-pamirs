package pro.shushi.pamirs.eip.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.groovy.util.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.eip.api.constant.EipCircuitBreakerConstant;
import pro.shushi.pamirs.eip.api.enmu.CircuitBreakerTypeEnum;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.pmodel.EipCircuitBreakerRuleProxy;
import pro.shushi.pamirs.eip.api.service.EipCircuitBreakerRuleProxyService;
import pro.shushi.pamirs.eip.api.service.EipCircuitBreakerRuleService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLHelper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import static pro.shushi.pamirs.eip.api.constant.EipCircuitBreakerConstant.STATISTICAL_DURATION_MAX;

/**
 * @author yeshenyue on 2025/4/14 17:34.
 */
@Slf4j
@Service
@Fun(EipCircuitBreakerRuleProxyService.FUN_NAMESPACE)
public class EipCircuitBreakerRuleProxyServiceImpl implements EipCircuitBreakerRuleProxyService {

    @Autowired
    private EipCircuitBreakerRuleService eipCircuitBreakerRuleService;

    @Override
    @Function
    public void deleteOne(EipCircuitBreakerRuleProxy eipCircuitBreakerRuleProxy) {
        eipCircuitBreakerRuleService.deleteOne(eipCircuitBreakerRuleProxy);
    }

    @Override
    @Function
    public EipCircuitBreakerRuleProxy create(EipCircuitBreakerRuleProxy data) {
        checkData(data);
        eipCircuitBreakerRuleService.create(data);
        return data;
    }

    @Override
    @Function
    public Integer update(EipCircuitBreakerRuleProxy data) {
        checkData(data);
        return eipCircuitBreakerRuleService.update(data);
    }

    @Override
    @Function
    public Pagination<EipCircuitBreakerRuleProxy> queryPage(Pagination<EipCircuitBreakerRuleProxy> page,
                                                            IWrapper<EipCircuitBreakerRuleProxy> queryWrapper) {
        Map<String, Object> queryData = queryWrapper.getQueryData();
        Pagination<EipCircuitBreakerRuleProxy> result = queryPage(page, queryData, queryWrapper.getOriginRsql());
        for (EipCircuitBreakerRuleProxy ruleProxy : Optional.ofNullable(result.getContent()).orElse(Collections.emptyList())) {
            ruleProxy.setThreshold(ruleProxy.getThreshold());
        }
        return result;
    }

    private Pagination<EipCircuitBreakerRuleProxy> queryPage(Pagination<EipCircuitBreakerRuleProxy> page,
                                                             Map<String, Object> queryData,
                                                             String rsql) {
        LambdaQueryWrapper<EipCircuitBreakerRuleProxy> query = Pops.<EipCircuitBreakerRuleProxy>lambdaQuery()
                .from(EipCircuitBreakerRuleProxy.MODEL_MODEL);
        if (MapUtils.isNotEmpty(queryData)) {
            String fieldName = LambdaUtil.fetchFieldName(EipCircuitBreakerRuleProxy::getIntegrationInterface);
            Object rawValue = queryData.get(fieldName);
            if (rawValue != null) {
                String jsonString = JsonUtils.toJSONString(rawValue);
                EipIntegrationInterface eipIntegration = JsonUtils.parseObject(jsonString, EipIntegrationInterface.class);
                eipIntegration = eipIntegration.queryOne();
                String circuitBreakerRuleCode = eipIntegration.getCircuitBreakerRuleCode();
                if (StringUtils.isNotBlank(circuitBreakerRuleCode)) {
                    query.eq(EipCircuitBreakerRuleProxy::getCode, circuitBreakerRuleCode);
                } else {
                    return new Pagination<>();
                }
            }
        }

        buildQuery(rsql, query);
        return new EipCircuitBreakerRuleProxy().queryPage(page, query);
    }

    private static void buildQuery(String rsql, LambdaQueryWrapper<EipCircuitBreakerRuleProxy> query) {
        final Map<String, BiConsumer<LambdaQueryWrapper<EipCircuitBreakerRuleProxy>, Object>> fieldMap = Maps.of(
                LambdaUtil.fetchFieldName(EipCircuitBreakerRuleProxy::getRuleName),
                (q, v) -> q.like(EipCircuitBreakerRuleProxy::getRuleName, v),

                LambdaUtil.fetchFieldName(EipCircuitBreakerRuleProxy::getCode),
                (q, v) -> q.eq(EipCircuitBreakerRuleProxy::getCode, v),

                LambdaUtil.fetchFieldName(EipCircuitBreakerRuleProxy::getCircuitBreakerType),
                (q, v) -> q.eq(EipCircuitBreakerRuleProxy::getCircuitBreakerType, v)
        );
        Map<String, Object> rsqlValues = RSQLHelper.getRsqlValues(rsql, fieldMap.keySet());
        rsqlValues.forEach((field, value) -> {
            BiConsumer<LambdaQueryWrapper<EipCircuitBreakerRuleProxy>, Object> consumer = fieldMap.get(field);
            if (consumer != null && value != null) {
                consumer.accept(query, value);
            }
        });
    }

    private void checkData(EipCircuitBreakerRuleProxy data) {
        // 规则名称
        String ruleName = data.getRuleName();
        if (ruleName == null || !ruleName.matches(EipCircuitBreakerConstant.RULE_NAME_REGULAR)) {
            throw PamirsException.construct(EipExpEnumerate.EIP_CB_RULE_NAME_INVALID).errThrow();
        }

        // 集成接口校验
        if (CollectionUtils.isEmpty(data.getIntegrationInterfaceList())) {
            throw PamirsException.construct(EipExpEnumerate.EIP_CB_NO_INTERFACE_DEFINED).errThrow();
        }

        // 统计时长：1s~120分钟
        Integer statisticalDuration = data.getStatisticalDuration();
        if (statisticalDuration == null || statisticalDuration < 1 || statisticalDuration > STATISTICAL_DURATION_MAX) {
            throw PamirsException.construct(EipExpEnumerate.EIP_CB_STATISTICAL_DURATION_INVALID).errThrow();
        }

        // 最小请求数
        Integer minRequestCount = data.getMinRequestCount();
        if (minRequestCount == null || minRequestCount < 1) {
            throw PamirsException.construct(EipExpEnumerate.EIP_CB_MIN_REQUEST_COUNT_INVALID).errThrow();
        }

        // 熔断器类型
        CircuitBreakerTypeEnum circuitBreakerType = data.getCircuitBreakerType();
        if (circuitBreakerType == null) {
            throw PamirsException.construct(EipExpEnumerate.EIP_CB_TYPE_NULL).errThrow();
        }

        // 比例阈值校验
        if (CircuitBreakerTypeEnum.SLOW_CALL.equals(circuitBreakerType)) {
            Integer slowCallThreshold = data.getSlowCallThreshold();
            if (slowCallThreshold == null || slowCallThreshold < 1 || slowCallThreshold > 100) {
                throw PamirsException.construct(EipExpEnumerate.EIP_CB_SLOW_CALL_THRESHOLD_INVALID).errThrow();
            }
            Long slowCallResponseTime = data.getSlowCallResponseTime();
            if (slowCallResponseTime == null || slowCallResponseTime < 1) {
                throw PamirsException.construct(EipExpEnumerate.EIP_CB_SLOW_CALL_RESPONSE_TIME_INVALID).errThrow();
            }

        } else if (CircuitBreakerTypeEnum.EXCEPTION.equals(circuitBreakerType)) {
            Integer failureRateThreshold = data.getFailureRateThreshold();
            if (failureRateThreshold == null || failureRateThreshold < 1 || failureRateThreshold > 100) {
                throw PamirsException.construct(EipExpEnumerate.EIP_CB_FAILURE_RATE_THRESHOLD_INVALID).errThrow();
            }
        }

        // 熔断时长
        Integer circuitBreakerDuration = data.getCircuitBreakerDuration();
        if (circuitBreakerDuration == null || circuitBreakerDuration < 1) {
            throw PamirsException.construct(EipExpEnumerate.EIP_CB_DURATION_INVALID).errThrow();
        }

        // 熔断恢复策略校验
        if (data.getRecoveryStrategy() == null) {
            throw PamirsException.construct(EipExpEnumerate.EIP_CB_RECOVERY_STRATEGY_INVALID).errThrow();
        }
    }
}
