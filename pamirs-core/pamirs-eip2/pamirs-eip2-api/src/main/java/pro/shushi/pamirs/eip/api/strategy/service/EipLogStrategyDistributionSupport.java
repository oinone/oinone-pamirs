package pro.shushi.pamirs.eip.api.strategy.service;

import pro.shushi.pamirs.eip.api.service.distribution.EipDistributionService;
import pro.shushi.pamirs.eip.api.strategy.entity.EipLogStrategyEntity;
import pro.shushi.pamirs.meta.api.dto.common.Result;

/**
 * @author Adamancy Zhang at 10:12 on 2025-08-18
 */
public interface EipLogStrategyDistributionSupport extends EipDistributionService {

    String NODE_PATH_PREFIX = "/eip/strategy/log";

    byte[] ENABLED = new byte[]{1};

    byte[] DISABLED = new byte[]{0};

    Result<String> refreshLogStrategy(EipLogStrategyEntity logStrategy);
}
