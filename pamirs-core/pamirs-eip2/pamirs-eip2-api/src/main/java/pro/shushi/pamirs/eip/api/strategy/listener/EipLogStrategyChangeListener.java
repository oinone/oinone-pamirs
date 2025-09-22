package pro.shushi.pamirs.eip.api.strategy.listener;

import com.alibaba.fastjson.JSON;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.strategy.context.EipLogStrategyContext;
import pro.shushi.pamirs.eip.api.strategy.entity.EipLogStrategyEntity;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogStrategyDistributionSupport;
import pro.shushi.pamirs.framework.common.utils.kryo.KryoUtils;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

/**
 * @author Adamancy Zhang at 14:15 on 2025-08-16
 */
@Slf4j
@Component
public class EipLogStrategyChangeListener implements TreeCacheListener {

    @Autowired
    private ZookeeperService zookeeperService;

    @Override
    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
        switch (treeCacheEvent.getType()) {
            case NODE_ADDED:
            case NODE_UPDATED:
            case NODE_REMOVED:
                process(treeCacheEvent.getData());
                break;
        }
    }

    private void process(ChildData childData) {
        String path = childData.getPath();
        int rootPathLength = zookeeperService.getRootPath().length() + EipLogStrategyDistributionSupport.NODE_PATH_PREFIX.length() + 1;
        if (path.length() <= rootPathLength) {
            return;
        }
        path = path.substring(rootPathLength);
        String[] pathList = path.split(CharacterConstants.SEPARATOR_SLASH);
        String tenant;
        if (pathList.length == 3) {
            tenant = pathList[0];
            pathList = new String[]{pathList[1], pathList[2]};
        } else if (pathList.length == 2) {
            tenant = null;
        } else {
            return;
        }
        if (tenant != null) {
            PamirsTenantSession.setTenant(tenant);
        }
        InterfaceTypeEnum interfaceType = InterfaceTypeEnum.safeValueOf(pathList[0]);
        if (interfaceType == null) {
            //无效类型忽略
            return;
        }
        String interfaceName = pathList[1];
        byte[] data = childData.getData();
        if (data == null) {
            if (log.isInfoEnabled()) {
                log.info("remove log strategy. interfaceType: {}, interfaceName: {}", interfaceType, interfaceName);
            }
            EipLogStrategyContext.remove(interfaceType, interfaceName);
        } else {
            EipLogStrategyEntity logStrategy = KryoUtils.deserialize(data, EipLogStrategyEntity.class);
            if (log.isInfoEnabled()) {
                log.info("refresh log strategy: {}", JSON.toJSONString(logStrategy));
            }
            EipLogStrategyContext.put(interfaceType, interfaceName, logStrategy);
        }
    }
}
