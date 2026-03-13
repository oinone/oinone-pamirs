package pro.shushi.pamirs.middle.lock.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;
import pro.shushi.pamirs.middleware.zookeeper.util.ZookeeperHelper;

/**
 * @author Adamancy Zhang
 * @date 2020-12-24 10:51
 */
public class SessionConnectionListener implements ConnectionStateListener {

    private static final Logger log = LoggerFactory.getLogger(SessionConnectionListener.class);

    private final ZookeeperService zookeeperService;

    private final String routePath;

    public SessionConnectionListener(ZookeeperService zookeeperService, String routePath) {
        this.zookeeperService = zookeeperService;
        this.routePath = ZookeeperHelper.repairPath(routePath);
    }

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {
        if (newState == ConnectionState.LOST) {
            log.error("zookeeper session timeout");
            while (true) {
                try {
                    if (client.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
                        zookeeperService.create(routePath, createBuilder -> createBuilder.withMode(CreateMode.EPHEMERAL_SEQUENTIAL));
                        log.error("Re-acquire lock successfully [routePath {}]", routePath);
                        break;
                    }
                } catch (InterruptedException e) {
                    log.error("SessionConnectionListener error", e);
                    break;
                } catch (Exception e) {
                    log.error("SessionConnectionListener error", e);
                }
            }
        }
    }
}
