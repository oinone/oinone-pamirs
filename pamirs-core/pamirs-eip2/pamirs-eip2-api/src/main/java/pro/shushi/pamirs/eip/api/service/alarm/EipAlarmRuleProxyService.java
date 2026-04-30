package pro.shushi.pamirs.eip.api.service.alarm;

import pro.shushi.pamirs.eip.api.pmodel.alarm.EipAlarmRuleProxy;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

/**
 * EipAlarmRuleProxyService
 *
 * @author yakir on 2026/04/09 16:32.
 */
public interface EipAlarmRuleProxyService {

    EipAlarmRuleProxy createOrUpdate(EipAlarmRuleProxy data);

    EipAlarmRuleProxy queryOne(EipAlarmRuleProxy query);

    Pagination<EipAlarmRuleProxy> queryPage(Pagination<EipAlarmRuleProxy> page, IWrapper<EipAlarmRuleProxy> queryWrapper);

    EipAlarmRuleProxy deleteOne(EipAlarmRuleProxy data);
}
