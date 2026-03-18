package pro.shushi.pamirs.record.sql.manager;

import com.google.common.base.Splitter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * FilterWatcherManager 监听zk变更,监听到变更之后从db捞取过滤规则
 *
 * @author yakir on 2023/06/28 17:03.
 */
@Slf4j
public class FilterWatcherManager {

    private final RecordFilterManager recordFilterManager;
    private final ZookeeperService zookeeperService;

    private static final String MY_ID = UUID.randomUUID().toString();

    private static final String SQL_RECORD = "/sql/record";

    private static final AtomicReference<String> tenantRef = new AtomicReference<>();

    public FilterWatcherManager() {
        this.recordFilterManager = BeanDefinitionUtils.getBean(RecordFilterManager.class);
        this.zookeeperService = BeanDefinitionUtils.getBean(ZookeeperService.class);
    }

    public void init() {

        CuratorFramework client = zookeeperService.getClient();
        client.getConnectionStateListenable().addListener(new SqlRecordConnectionStateListener());

        String sqlRecordRoot = tenantSqlRecordRoot();
        try {
            if (client.checkExists().forPath(sqlRecordRoot) == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(sqlRecordRoot);
            }
        } catch (Exception exp) {
            log.error("Exception creating node: [{}]", sqlRecordRoot, exp);
        }
        String myPath = sqlRecordRoot + "/" + MY_ID;
        try {
            if (client.checkExists().forPath(myPath) == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(myPath, "0".getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception exp) {
            log.error("Exception creating MyPath node: [{}]", myPath, exp);
        }

        NodeCache nodeCache = new NodeCache(client, myPath);
        nodeCache.getListenable().addListener(new ZkNodeListener(client, nodeCache, recordFilterManager));
        try {
            nodeCache.start();
        } catch (Exception e) {
            log.error("Exception listening to node", e);
        }
    }

    @Slf4j
    public static class ZkNodeListener implements NodeCacheListener {

        private final CuratorFramework client;
        private final NodeCache nodeCache;
        private final RecordFilterManager recordFilterManager;

        public ZkNodeListener(CuratorFramework client, NodeCache nodeCache, RecordFilterManager recordFilterManager) {
            this.client = client;
            this.nodeCache = nodeCache;
            this.recordFilterManager = recordFilterManager;
        }

        @Override
        public void nodeChanged() throws Exception {
            log.info("Refresh event triggered");
            ChildData childData = nodeCache.getCurrentData();
            String myPath = nodeCache.getPath();
            if (null == childData) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(myPath, "0".getBytes(StandardCharsets.UTF_8));
                return;
            }
            byte[] bytes = childData.getData();
            String data = new String(bytes, StandardCharsets.UTF_8);
            log.info("Path:[{}] data:[{}]", myPath, data);
            if (StringUtils.equals(data, "1")) {
                String oldTenant = PamirsTenantSession.getTenant();
                String tenant = Optional.of(Splitter.on(SQL_RECORD).splitToList(myPath))
                        .map(_list -> _list.get(0))
                        .filter(StringUtils::isNotBlank)
                        .map(_tenant -> _tenant.substring(1))
                        .orElse(null);
                // 设置租户
                if (StringUtils.isNotBlank(tenant)) {
                    PamirsTenantSession.setTenant(tenant);
                }
                // 延迟触发
                TimeUnit.SECONDS.sleep(3L);
                recordFilterManager.refresh();
                // 还原租户
                if (StringUtils.isNotBlank(oldTenant)) {
                    PamirsTenantSession.setTenant(oldTenant);
                }
                log.info("Refresh Filter");
                client.setData().forPath(myPath, "0".getBytes(StandardCharsets.UTF_8));
                log.info("Reset refresh status");
            }
        }
    }

    @Slf4j
    public static class SqlRecordConnectionStateListener implements ConnectionStateListener {

        @Override
        public void stateChanged(CuratorFramework client, ConnectionState newState) {
            if (ConnectionState.LOST == newState) {
                String sqlRecordRoot = tenantSqlRecordRoot();
                String myPath = sqlRecordRoot + "/" + MY_ID;
                while (true) {
                    try {
                        if (client.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
                            if (client.checkExists().forPath(sqlRecordRoot) == null) {
                                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(sqlRecordRoot);
                            }
                            if (client.checkExists().forPath(myPath) == null) {
                                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(myPath, "0".getBytes(StandardCharsets.UTF_8));
                            }
                        }
                        log.info("Reconnection successful [{}]", myPath);
                        break;
                    } catch (InterruptedException e) {
                        log.info("Reconnection interrupted");
                        break;
                    } catch (Exception exp) {
                        log.error("Exception creating MyPath node during reconnection: [{}]", myPath, exp);
                    }
                }
            }
        }
    }

    public boolean allRefresh() {

        CuratorFramework client = zookeeperService.getClient();

        String sqlRecordRoot = tenantSqlRecordRoot();
        List<String> children = null;
        try {
            children = client.getChildren().forPath(sqlRecordRoot);
        } catch (Exception exp) {
            log.error("Exception getting child nodes: [{}]", sqlRecordRoot, exp);
        }

        if (CollectionUtils.isEmpty(children)) {
            return true;
        }

        for (String child : children) {
            try {
                log.info("child: [{}]", child);
                if (!StringUtils.equals(MY_ID, child)) {
                    client.setData().forPath(sqlRecordRoot + "/" + child, "1".getBytes(StandardCharsets.UTF_8));
                }
            } catch (Exception exp) {
                log.error("Exception updating child node: [{}]", child, exp);
            }
        }
        recordFilterManager.initFilterCache();
        return true;
    }

    private static String tenantSqlRecordRoot() {
        tenantRef.compareAndSet(null, PamirsTenantSession.getTenant());
        String tenant = tenantRef.get();
        if (StringUtils.isNotBlank(tenant)) {
            return "/" + tenant + SQL_RECORD;
        }
        return SQL_RECORD;
    }
}
