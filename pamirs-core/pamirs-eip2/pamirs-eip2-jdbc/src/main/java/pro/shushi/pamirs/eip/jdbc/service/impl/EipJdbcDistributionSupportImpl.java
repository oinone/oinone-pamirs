package pro.shushi.pamirs.eip.jdbc.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.eip.api.enmu.connector.TestConnStatus;
import pro.shushi.pamirs.eip.api.model.connector.EipConnector;
import pro.shushi.pamirs.eip.jdbc.manager.EipDataSourceManager;
import pro.shushi.pamirs.eip.jdbc.service.EipJdbcDistributionSupport;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EIP Jdbc 分布式支持
 *
 * @author Adamancy Zhang at 11:22 on 2025-08-13
 */
@Slf4j
@Service
public class EipJdbcDistributionSupportImpl implements EipJdbcDistributionSupport {

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private EipJdbcZookeeperNodeListener nodeListener;

    private final Map<String, TreeCache> treeCacheMap = new ConcurrentHashMap<>();

    @Override
    public synchronized void start() throws Exception {
        zookeeperService.start();
        if (!treeCacheMap.isEmpty()) {
            close();
        }
        registerListeners();
    }

    @Override
    public synchronized void close() {
        for (Map.Entry<String, TreeCache> entry : treeCacheMap.entrySet()) {
            entry.getValue().close();
        }
        treeCacheMap.clear();
    }

    @Override
    public void registerListener(List<String> tenantRootPathList) {
        for (String tenantRootPath : tenantRootPathList) {
            TreeCache treeCache = zookeeperService.registerTreeCache(tenantRootPath, nodeListener);
            if (treeCache != null) {
                treeCacheMap.put(tenantRootPath, treeCache);
            }
        }
    }

    @Override
    public Result<String> refreshConnector(EipConnector connector) {
        String dsKey = String.valueOf(connector.getId());
        String key = EipDataSourceManager.generatorId(dsKey);
        String routePath = getRootPath() + CharacterConstants.SEPARATOR_SLASH + key;
        TestConnStatus dataStatus = connector.getTestConnStatus();
        byte[] finalData;
        if (TestConnStatus.CONN_SUCCESS.equals(dataStatus)) {
            finalData = ENABLED;
        } else {
            finalData = DISABLED;
        }
        try {
            if (log.isInfoEnabled()) {
                log.info("ready refresh connector. {}", dsKey);
            }
            this.zookeeperService.createOrUpdateData(routePath, finalData, this::defaultComparator);
        } catch (Exception e) {
            log.error("{} refresh error.", key, e);
            return new Result<String>().error().setData(String.format("%s refresh error.", key));
        }
        return new Result<>();
    }

    private void registerListeners() {
        List<String> tenantRootPathList = new ArrayList<>();

        Set<String> keys = EipDataSourceManager.keySet();
        if (CollectionUtils.isNotEmpty(keys)) {
            String rootPath = getRootPath();
            try {
                for (String key : keys) {
                    String routePath = rootPath + CharacterConstants.SEPARATOR_SLASH + key;
                    this.zookeeperService.createOrUpdateData(routePath, ENABLED, this::defaultComparator);
                }
                tenantRootPathList.add(rootPath);
            } catch (Exception e) {
                log.error("Eip Jdbc 开启分布式支持失败，rootPath: {}, keys: {}", rootPath, keys, e);
            }
        }

        if (CollectionUtils.isNotEmpty(tenantRootPathList)) {
            registerListener(tenantRootPathList);
        }
    }

    private String getRootPath() {
        String tenant = PamirsTenantSession.getTenant();
        String rootPath;
        if (StringUtils.isBlank(tenant)) {
            rootPath = NODE_PATH_PREFIX;
        } else {
            rootPath = NODE_PATH_PREFIX + CharacterConstants.SEPARATOR_SLASH + tenant;
        }
        return rootPath;
    }

    private boolean defaultComparator(byte[] originData, byte[] data) {
        if (originData == null) {
            return Boolean.TRUE;
        }
        if (data == null) {
            return Boolean.FALSE;
        }
        if (data.length == 1) {
            if (originData[0] == data[0]) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        }
        return Boolean.TRUE;
    }
}
