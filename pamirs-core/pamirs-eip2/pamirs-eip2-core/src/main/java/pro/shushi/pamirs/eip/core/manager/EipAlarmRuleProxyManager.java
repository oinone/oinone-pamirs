package pro.shushi.pamirs.eip.core.manager;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.eip.api.enmu.alarm.AlarmMetricType;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.alarm.EipAlarmRuleRelEmployee;
import pro.shushi.pamirs.eip.api.model.alarm.EipAlarmRuleRelInterface;
import pro.shushi.pamirs.eip.api.pmodel.alarm.EipAlarmRuleProxy;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate.*;

/**
 * EipAlarmRuleProxyManager
 *
 * @author yakir on 2026/04/07 15:03.
 */
@Slf4j
@Component
public class EipAlarmRuleProxyManager {

    @Autowired
    private EipAlarmRuleRefreshManager eipAlarmRuleRefreshManager;

    @Transactional
    public EipAlarmRuleProxy createOrUpdate(EipAlarmRuleProxy data) {

        if (null == data) {
            return data;
        }

        Long id = data.getId();
        boolean isUpdate = null != id && id > 0;

        String name = data.getName();
        if (StringUtils.isBlank(name)) {
            throw PamirsException.construct(EIP_ALARM_NAME_EMPTY)
                    .errThrow();
        }

        IWrapper<EipAlarmRuleProxy> ruleQw = Pops.<EipAlarmRuleProxy>lambdaQuery()
                .from(EipAlarmRuleProxy.MODEL_MODEL)
                .eq(EipAlarmRuleProxy::getName, name)
                .ne(isUpdate, EipAlarmRuleProxy::getId, id);
        long ruleNameCount = new EipAlarmRuleProxy().count(ruleQw);
        if (ruleNameCount > 0) {
            throw PamirsException.construct(EIP_ALARM_NAME_DUP)
                    .errThrow();
        }

        AlarmMetricType metricType = data.getMetricType();
        if (AlarmMetricType.FAILURE_COUNT.equals(metricType)) {
            data.setThreshold(data.getThresholdForCount());
        } else if (AlarmMetricType.FAILURE_RATE.equals(metricType)) {
            data.setThreshold(new BigDecimal(data.getThresholdForRate() + "").multiply(new BigDecimal("100")).intValue());
        }

        if (!isUpdate) {
            data.setTechName(UUIDUtil.getUUIDNumberString());
        }

        data.createOrUpdate();

        List<EipIntegrationInterface> eipInterfaceList = data.getEipInterface();

        if (CollectionUtils.isNotEmpty(eipInterfaceList)) {
            Set<String> interfaceNames = new HashSet<>();
            for (EipIntegrationInterface eipInterface : eipInterfaceList) {
                String interfaceName = eipInterface.getInterfaceName();
                interfaceNames.add(interfaceName);
            }

            IWrapper<EipAlarmRuleRelInterface> otherRelQw = Pops.<EipAlarmRuleRelInterface>lambdaQuery()
                    .from(EipAlarmRuleRelInterface.MODEL_MODEL)
                    .ne(EipAlarmRuleRelInterface::getRuleTechName, data.getTechName())
                    .in(EipAlarmRuleRelInterface::getInterfaceName, interfaceNames);
            Long otherCount = new EipAlarmRuleRelInterface().count(otherRelQw);
            if (null != otherCount && otherCount > 0) {
                throw PamirsException.construct(EIP_ALARM_INTERFACE_OTHER_REL)
                        .errThrow();
            }
        }

        data.fieldSaveOnCascade(EipAlarmRuleProxy::getEipInterface);
        data.fieldSaveOnCascade(EipAlarmRuleProxy::getReceivers);

        eipAlarmRuleRefreshManager.refresh();

        return data;
    }

    public EipAlarmRuleProxy queryOne(EipAlarmRuleProxy query) {

        query = query.queryById();
        query.fieldQuery(EipAlarmRuleProxy::getEipInterface);
        query.fieldQuery(EipAlarmRuleProxy::getReceivers);

        AlarmMetricType metricType = query.getMetricType();
        if (AlarmMetricType.FAILURE_RATE.equals(metricType)) {
            query.setThresholdForRate(new BigDecimal(query.getThreshold())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP).floatValue());
        } else if (AlarmMetricType.FAILURE_COUNT.equals(metricType)) {
            query.setThresholdForCount(query.getThreshold());
        }

        return query;
    }

    public Pagination<EipAlarmRuleProxy> queryPage(Pagination<EipAlarmRuleProxy> page, IWrapper<EipAlarmRuleProxy> queryWrapper) {

        page = new EipAlarmRuleProxy().queryPage(page, queryWrapper);

        return page;
    }

    @Transactional
    public EipAlarmRuleProxy deleteOne(EipAlarmRuleProxy data) {

        if (null == data) {
            return data;
        }

        Long id = data.getId();
        if (null == id || id < 1) {
            return data;
        }

        data = data.queryById();

        String ruleTechName = data.getTechName();

        IWrapper<EipAlarmRuleRelInterface> relInterfaceQw = Pops.<EipAlarmRuleRelInterface>lambdaQuery()
                .from(EipAlarmRuleRelInterface.MODEL_MODEL)
                .eq(EipAlarmRuleRelInterface::getRuleTechName, ruleTechName);
        new EipAlarmRuleRelInterface().deleteByWrapper(relInterfaceQw);

        IWrapper<EipAlarmRuleRelEmployee> relEmployeeQw = Pops.<EipAlarmRuleRelEmployee>lambdaQuery()
                .from(EipAlarmRuleRelEmployee.MODEL_MODEL)
                .eq(EipAlarmRuleRelEmployee::getRuleTechName, ruleTechName);
        new EipAlarmRuleRelEmployee().deleteByWrapper(relEmployeeQw);

        data.deleteById();
        return data;
    }
}
