package pro.shushi.pamirs.eip.core.service.alarm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.pmodel.alarm.EipAlarmRuleProxy;
import pro.shushi.pamirs.eip.api.service.alarm.EipAlarmRuleProxyService;
import pro.shushi.pamirs.eip.core.manager.EipAlarmRuleProxyManager;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

/**
 * EipAlarmRuleProxyServiceImpl
 *
 * @author yakir on 2026/04/09 16:32.
 */
@Component
public class EipAlarmRuleProxyServiceImpl implements EipAlarmRuleProxyService {

    @Autowired
    private EipAlarmRuleProxyManager eipAlarmRuleProxyManager;

    @Override
    public EipAlarmRuleProxy createOrUpdate(EipAlarmRuleProxy data) {
        return eipAlarmRuleProxyManager.createOrUpdate(data);
    }

    @Override
    public EipAlarmRuleProxy queryOne(EipAlarmRuleProxy query) {
        return eipAlarmRuleProxyManager.queryOne(query);
    }

    @Override
    public Pagination<EipAlarmRuleProxy> queryPage(Pagination<EipAlarmRuleProxy> page, IWrapper<EipAlarmRuleProxy> queryWrapper) {
        return eipAlarmRuleProxyManager.queryPage(page, queryWrapper);
    }

    @Override
    public EipAlarmRuleProxy deleteOne(EipAlarmRuleProxy data) {
        return eipAlarmRuleProxyManager.deleteOne(data);
    }
}
