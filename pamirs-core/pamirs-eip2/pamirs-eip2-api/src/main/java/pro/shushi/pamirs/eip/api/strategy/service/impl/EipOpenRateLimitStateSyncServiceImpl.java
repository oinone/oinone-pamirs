package pro.shushi.pamirs.eip.api.strategy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.eip.api.strategy.listener.EipOpenRateLimitStatusChangeListener;
import pro.shushi.pamirs.eip.api.strategy.service.EipOpenRateLimitStateSyncService;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * @author yeshenyue on 2025/4/26 20:06.
 */
@Slf4j
@Service
public class EipOpenRateLimitStateSyncServiceImpl extends AbstractEipDistributedConfigSync implements EipOpenRateLimitStateSyncService {

    @Autowired
    private EipOpenRateLimitStatusChangeListener openRateLimitStatusChangeListener;

    @Override
    protected String getRootPath() {
        return OPEN_RATE_LIMIT_ZK_ROOT_PATH;
    }

    @Override
    protected String getDisplayName() {
        return I18nUtils.getMessage("EipOpenRateLimitStateSyncServiceImpl.displayName");
    }

    @Override
    public void startListener() {
        this.startListener(openRateLimitStatusChangeListener);
    }

    @Override
    public void handleUpdate(String appKey, String interfaceName) {
        String childPath = buildChildPath(appKey, interfaceName);
        this.syncConfig(childPath, CONFIG_UPDATE, true);
    }

    @Override
    public void handleRemove(String appKey, String interfaceName) {
        String childPath = buildChildPath(appKey, interfaceName);
        this.removeConfig(childPath);
    }

    private static String buildChildPath(String appKey, String interfaceName) {
        return appKey + CharacterConstants.SEPARATOR_SLASH + interfaceName;
    }
}
