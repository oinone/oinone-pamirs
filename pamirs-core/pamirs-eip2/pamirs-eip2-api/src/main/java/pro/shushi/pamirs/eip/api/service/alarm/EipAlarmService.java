package pro.shushi.pamirs.eip.api.service.alarm;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * EipAlarmService
 *
 * @author yakir on 2026/04/08 14:43.
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface EipAlarmService {

    boolean alarm(EipLog eipLog, IEipContext<SuperMap> context);

    void clearRuleCache();
}
