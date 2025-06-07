package pro.shushi.pamirs.middleware.schedule.core.manager;

import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import com.taobao.pamirs.schedule.zk.ZkManagerKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.schedule.core.condition.ScheduleSwitchCondition;
import pro.shushi.pamirs.middleware.zookeeper.config.ZookeeperProperties;
import pro.shushi.pamirs.middleware.zookeeper.util.ZookeeperHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * TBScheduleManagerBean
 *
 * @author yakir on 2020/07/02 14:08.
 */
@Component
@Conditional(ScheduleSwitchCondition.class)
public class TBScheduleManagerBean {

    public static final String TB_SCHEDULE_MANAGER_FACTORY_BEAN_NAME = "tbScheduleManagerFactory";

    @Autowired
    private ZookeeperProperties zookeeperProperties;

    @Autowired
    private ApplicationContext context;

    @Bean(name = {TB_SCHEDULE_MANAGER_FACTORY_BEAN_NAME})
    public TBScheduleManagerFactory tbScheduleManagerFactory(ZookeeperProperties zookeeperProperties) {
        Map<String, String> zkConfig = new HashMap<>();
        zkConfig.put(ZkManagerKeys.ZK_CONNECT_STRING, zookeeperProperties.getZkConnectString());
        zkConfig.put(ZkManagerKeys.ROOT_PATH, ZookeeperHelper.repairPath(zookeeperProperties.getRootPath()));
        zkConfig.put(ZkManagerKeys.USER_NAME, zookeeperProperties.getUsername());
        zkConfig.put(ZkManagerKeys.PASSWORD, zookeeperProperties.getPassword());
        zkConfig.put(ZkManagerKeys.ZK_SESSION_TIMEOUT, String.valueOf(zookeeperProperties.getZkSessionTimeout()));
        zkConfig.put(ZkManagerKeys.IS_CHECK_PARENT_PATH, Boolean.TRUE.toString());

        TBScheduleManagerFactory scheduleManagerFactory = new TBScheduleManagerFactory();
        scheduleManagerFactory.setApplicationContext(context);
        scheduleManagerFactory.setZkConfig(zkConfig);
        return scheduleManagerFactory;
    }
}
