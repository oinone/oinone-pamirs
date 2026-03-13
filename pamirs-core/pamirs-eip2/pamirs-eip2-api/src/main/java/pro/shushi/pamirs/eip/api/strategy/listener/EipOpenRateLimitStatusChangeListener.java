package pro.shushi.pamirs.eip.api.strategy.listener;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.strategy.service.EipOpenRateLimitPolicyService;
import pro.shushi.pamirs.eip.api.strategy.service.EipOpenRateLimitStateSyncService;
import pro.shushi.pamirs.eip.api.util.EipZkHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

/**
 * @author yeshenyue on 2025/4/27 08:56.
 */
@Slf4j
@Component
public class EipOpenRateLimitStatusChangeListener implements TreeCacheListener {

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private EipOpenRateLimitPolicyService eipOpenRateLimitPolicyService;

    @Override
    public void childEvent(CuratorFramework client, TreeCacheEvent event) {
        String path = event.getData() != null ? event.getData().getPath() : null;
        if (StringUtils.isBlank(path)) {
            return;
        }

        String rootPath = zookeeperService.getRootPath() + EipOpenRateLimitStateSyncService.OPEN_RATE_LIMIT_ZK_ROOT_PATH;
        if (!path.startsWith(rootPath) == path.length() > rootPath.length()) {
            return;
        }

        String dataPath = EipZkHelper.processorListenerPath(rootPath, path);
        if (StringUtils.isBlank(dataPath)) {
            log.info("Flow control detected top-level or unexpected node change, ignored, path: {}", path);
            return;
        }

        String[] pathList = dataPath.split(CharacterConstants.SEPARATOR_SLASH);
        if (pathList.length != 2) {
            log.info("Flow control detected top-level or unexpected node change, ignored, path: {}", path);
            return;
        }

        String appKey = pathList[0];
        String interfaceName = pathList[1];
        eipOpenRateLimitPolicyService.refreshLocal(appKey, interfaceName);
    }
}
