package pro.shushi.pamirs.eip.jdbc.service.impl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.model.connector.EipConnector;
import pro.shushi.pamirs.eip.jdbc.helper.EipConnectorHelper;
import pro.shushi.pamirs.eip.jdbc.manager.EipDataSourceManager;
import pro.shushi.pamirs.eip.jdbc.service.EipJdbcDistributionSupport;
import pro.shushi.pamirs.eip.jdbc.util.DbConnectionUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

import java.util.function.BiConsumer;

/**
 * @author Adamancy Zhang at 11:22 on 2025-08-13
 */
@Slf4j
@Component
public class EipJdbcZookeeperNodeListener implements TreeCacheListener {

    @Autowired
    private ZookeeperService zookeeperService;

    @Override
    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
        switch (treeCacheEvent.getType()) {
            case NODE_ADDED:
                register(treeCacheEvent.getData());
                break;
            case NODE_UPDATED:
                refresh(treeCacheEvent.getData());
                break;
            case NODE_REMOVED:
                close(treeCacheEvent.getData());
                break;
        }
    }

    private void register(ChildData data) {
        process0(data, (info, connector) -> EipConnectorHelper.initCamelDataSource(connector));
    }

    private void refresh(ChildData data) {
        process0(data, (info, connector) -> {
            String dsKey = String.valueOf(connector.getId());
            if (info.enabled) {
                EipDataSourceManager.refresh(dsKey, () -> DbConnectionUtils.buildDataSource(connector));
            } else {
                EipDataSourceManager.close(dsKey);
            }
        });
    }

    private void close(ChildData data) {
        process0(data, (info, connector) -> EipConnectorHelper.closeDataSource(connector));
    }

    private void process0(ChildData data, BiConsumer<ConnectorInfo, EipConnector> consumer) {
        ConnectorInfo info = getConnectorInfo(data);
        if (info == null) {
            log.error("get connector info error. path: {}, data: {}", data.getPath(), data.getData());
            return;
        }
        EipConnector connector = info.getConnector();
        if (connector == null) {
            log.error("get connector error. dsKey: {}", info.dsKey);
            return;
        }
        consumer.accept(info, connector);
    }

    private ConnectorInfo getConnectorInfo(ChildData data) {
        String path = data.getPath();
        int rootPathLength = zookeeperService.getRootPath().length() + EipJdbcDistributionSupport.NODE_PATH_PREFIX.length() + 1;
        if (path.length() <= rootPathLength) {
            return null;
        }
        path = path.substring(rootPathLength);
        String[] pathList = path.split(CharacterConstants.SEPARATOR_SLASH);
        String tenant;
        String id;
        if (pathList.length == 2) {
            tenant = pathList[0];
            id = pathList[1];
        } else if (pathList.length == 1) {
            tenant = null;
            id = pathList[0];
        } else {
            return null;
        }
        if (tenant != null) {
            PamirsTenantSession.setTenant(tenant);
        }
        String dsKey = EipDataSourceManager.resolveDsKey(id);
        return new ConnectorInfo(dsKey, getConnectorId(dsKey), getEnabled(data.getData()));
    }

    private Long getConnectorId(String dsKey) {
        try {
            return Long.valueOf(dsKey);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private boolean getEnabled(byte[] data) {
        Boolean enabled = null;
        if (data != null && data.length >= 1) {
            byte data0 = data[0];
            if (data0 == EipJdbcDistributionSupport.ENABLED[0]) {
                enabled = Boolean.TRUE;
            } else if (data0 == EipJdbcDistributionSupport.DISABLED[0]) {
                enabled = Boolean.FALSE;
            }
        }
        if (enabled != null) {
            return enabled;
        }
        return false;
    }

    private static class ConnectorInfo {

        private final String dsKey;

        private final Long connectorId;

        private final boolean enabled;

        public ConnectorInfo(String dsKey, Long connectorId, boolean enabled) {
            this.dsKey = dsKey;
            this.connectorId = connectorId;
            this.enabled = enabled;
        }

        public EipConnector getConnector() {
            if (connectorId == null) {
                return null;
            }
            return Models.origin().queryOneByWrapper(Pops.<EipConnector>lambdaQuery()
                    .from(EipConnector.MODEL_MODEL)
                    .eq(EipConnector::getId, connectorId));
        }
    }
}
