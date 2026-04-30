package pro.shushi.pamirs.eip.core.manager;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.service.alarm.EipAlarmService;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * EipAlarmRuleRefreshManager
 *
 * @author yakir on 2026/04/09 15:15.
 */
@Slf4j
@Component
public class EipAlarmRuleRefreshManager implements NodeCacheListener {

    @Autowired
    private ZookeeperService zookeeperService;

    private static final String MY_ID = UUID.randomUUID().toString();

    private static final String ALARM_ZK_ROOT_PATH = "/eip/alarm";

    private static final AtomicReference<String> tenantRef = new AtomicReference<>();

    private NodeCache nodeCache;

    @PostConstruct
    public void init() {

        CuratorFramework client = zookeeperService.getClient();
        client.getConnectionStateListenable().addListener(new EipAlarmConnectionStateListener());

        String sqlRecordRoot = tenantRoot();
        try {
            if (client.checkExists().forPath(sqlRecordRoot) == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(sqlRecordRoot);
            }
        } catch (Exception exp) {
            log.error("create node error: [{}]", sqlRecordRoot, exp);
        }
        String myPath = sqlRecordRoot + "/" + MY_ID;
        try {
            if (client.checkExists().forPath(myPath) == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                        .forPath(myPath, "0".getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception exp) {
            log.error("create MyPath error: [{}]", myPath, exp);
        }

        this.nodeCache = new NodeCache(client, myPath);
        nodeCache.getListenable().addListener(this);
        try {
            nodeCache.start();
        } catch (Exception e) {
            log.error("node listen error", e);
        }
    }

    @Override
    public void nodeChanged() throws Exception {
        log.info("Refresh Cache Start");
        ChildData childData = nodeCache.getCurrentData();
        String myPath = nodeCache.getPath();
        if (null == childData) {
            zookeeperService.getClient()
                    .create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(myPath, "0".getBytes(StandardCharsets.UTF_8));
            return;
        }
        byte[] bytes = childData.getData();
        String data = new String(bytes, StandardCharsets.UTF_8);
        log.info("Path:[{}] data:[{}]", myPath, data);
        if (StringUtils.equals(data, "1")) {
            String oldTenant = PamirsTenantSession.getTenant();
            String tenant = Optional.of(Splitter.on(ALARM_ZK_ROOT_PATH).splitToList(myPath))
                    .map(_list -> _list.get(0))
                    .filter(StringUtils::isNotBlank)
                    .map(_tenant -> _tenant.substring(1))
                    .orElse(null);
            if (StringUtils.isNotBlank(tenant)) {
                PamirsTenantSession.setTenant(tenant);
            }
            TimeUnit.SECONDS.sleep(3L);
            Spider.getDefaultExtension(EipAlarmService.class).clearRuleCache();
            if (StringUtils.isNotBlank(oldTenant)) {
                PamirsTenantSession.setTenant(oldTenant);
            }
            zookeeperService.getClient()
                    .setData()
                    .forPath(myPath, "0".getBytes(StandardCharsets.UTF_8));
            log.info("refresh cache");
        }
    }

    @Slf4j
    public static class EipAlarmConnectionStateListener implements ConnectionStateListener {

        @Override
        public void stateChanged(CuratorFramework client, ConnectionState newState) {
            if (ConnectionState.LOST == newState) {
                String pathRoot = tenantRoot();
                String myPath = pathRoot + "/" + MY_ID;
                while (true) {
                    try {
                        if (client.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
                            if (client.checkExists().forPath(pathRoot) == null) {
                                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(pathRoot);
                            }
                            if (client.checkExists().forPath(myPath) == null) {
                                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                                        .forPath(myPath, "0".getBytes(StandardCharsets.UTF_8));
                            }
                        }
                        log.info("reconnect succeed [{}]", myPath);
                        break;
                    } catch (InterruptedException e) {
                        log.info("reconnect interrupted");
                        break;
                    } catch (Exception exp) {
                        log.error("reconnect create MyPath error: [{}]", myPath, exp);
                    }
                }
            }
        }
    }

    public boolean refresh() {

        CuratorFramework client = zookeeperService.getClient();

        String sqlRecordRoot = tenantRoot();
        List<String> children = null;
        try {
            children = client.getChildren().forPath(sqlRecordRoot);
        } catch (Exception exp) {
            log.error("fetch child node error: [{}]", sqlRecordRoot, exp);
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
                log.error("update child node error: [{}]", child, exp);
            }
        }
        Spider.getDefaultExtension(EipAlarmService.class).clearRuleCache();
        return true;
    }

    private static String tenantRoot() {
        tenantRef.compareAndSet(null, PamirsTenantSession.getTenant());
        String tenant = tenantRef.get();
        if (StringUtils.isNotBlank(tenant)) {
            return "/" + tenant + ALARM_ZK_ROOT_PATH;
        }
        return ALARM_ZK_ROOT_PATH;
    }
}
