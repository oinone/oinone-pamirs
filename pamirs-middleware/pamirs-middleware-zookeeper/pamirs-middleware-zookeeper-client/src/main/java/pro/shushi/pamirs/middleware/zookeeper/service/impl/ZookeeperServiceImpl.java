package pro.shushi.pamirs.middleware.zookeeper.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.middleware.zookeeper.auth.PamirsACLProvider;
import pro.shushi.pamirs.middleware.zookeeper.config.ZKConfigurationConstant;
import pro.shushi.pamirs.middleware.zookeeper.config.ZookeeperProperties;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;
import pro.shushi.pamirs.middleware.zookeeper.util.ZookeeperHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

@Service
public class ZookeeperServiceImpl implements ZookeeperService, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperServiceImpl.class);

    private CuratorFramework curator;

    private final Map<String, ConnectionStateListener> connectionStateListenerMap = new ConcurrentHashMap<>();

    private final Map<String, TreeCache> treeCacheMap = new ConcurrentHashMap<>();

    @Autowired
    private ZookeeperProperties zookeeperProperties;

    @Override
    public synchronized void start() throws Exception {
        if (!isEnabled()) {
            if (this.curator == null) {
                afterPropertiesSet();
                return;
            }
            this.curator.start();
            create(null);
        }
    }

    @Override
    public synchronized void close() throws Exception {
        if (isEnabled()) {
            this.curator.close();
            this.curator = null;
        }
    }

    @Override
    public boolean isEnabled() {
        return this.curator != null && CuratorFrameworkState.STARTED.equals(this.curator.getState());
    }

    @Override
    public String getRootPath() {
        return this.zookeeperProperties.getRootPath();
    }

    @Override
    public String getFullPath(String routePath) {
        if (StringUtils.isBlank(routePath)) {
            return getRootPath();
        }
        routePath = ZookeeperHelper.repairPath(routePath);
        return getRootPath() + routePath;
    }

    @Override
    public Stat getStat(String routePath) throws Exception {
        return curator.checkExists().forPath(getFullPath(routePath));
    }

    @Override
    public boolean checkExists(String routePath) throws Exception {
        return getStat(routePath) != null;
    }

    @Override
    public void create(String routePath) throws Exception {
        createOrUpdateData(routePath, null, null, null);
    }

    @Override
    public void create(String routePath, Consumer<CreateBuilder> createBuilderConsumer) throws Exception {
        createOrUpdateData(routePath, null, createBuilderConsumer, null);
    }

    @Override
    public void createOrUpdateData(String routePath, byte[] data) throws Exception {
        createOrUpdateData(routePath, data, null, null, null);
    }

    @Override
    public void createOrUpdateData(String routePath, byte[] data, Consumer<CreateBuilder> createBuilderConsumer, Consumer<SetDataBuilder> setDataBuilderConsumer) throws Exception {
        createOrUpdateData(routePath, data, null, createBuilderConsumer, setDataBuilderConsumer);
    }

    @Override
    public void createOrUpdateData(String routePath, byte[] data, BiPredicate<byte[], byte[]> comparator) throws Exception {
        createOrUpdateData(routePath, data, comparator, null, null);
    }

    @Override
    public void createOrUpdateData(String routePath, byte[] data, BiPredicate<byte[], byte[]> comparator, Consumer<CreateBuilder> createBuilderConsumer, Consumer<SetDataBuilder> setDataBuilderConsumer) throws Exception {
        routePath = getFullPath(routePath);
        if (curator.checkExists().forPath(routePath) == null) {
            //当节点不存在时，补充节点信息
            CreateBuilder createBuilder = curator.create();
            createBuilder.creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT);
            if (createBuilderConsumer != null) {
                createBuilderConsumer.accept(createBuilder);
            }
            if (data == null) {
                createBuilder.forPath(routePath);
            } else {
                createBuilder.forPath(routePath, data);
            }
        } else {
            //否则，获取节点数据，进行比较，根据比较结果确定是否更新节点信息
            byte[] originData = curator.getData().forPath(routePath);
            if (originData == null && data == null) {
                //当节点数据和将要设置的数据都为null时不进行操作
                return;
            }
            boolean needSetNewData = Boolean.TRUE;
            if (comparator != null) {
                needSetNewData = comparator.test(originData, data);
            }
            if (needSetNewData) {
                SetDataBuilder setDataBuilder = curator.setData();
                if (setDataBuilderConsumer != null) {
                    setDataBuilderConsumer.accept(setDataBuilder);
                }
                setDataBuilder.forPath(routePath, data);
            }
        }
    }

    @Override
    public byte[] getData(String routePath) throws Exception {
        return getData(routePath, null);
    }

    @Override
    public byte[] getData(String routePath, Consumer<GetDataBuilder> getDataBuilderConsumer) throws Exception {
        routePath = getFullPath(routePath);
        if (curator.checkExists().forPath(routePath) == null) {
            return null;
        }
        GetDataBuilder getDataBuilder = curator.getData();
        if (getDataBuilderConsumer != null) {
            getDataBuilderConsumer.accept(getDataBuilder);
        }
        return getDataBuilder.forPath(routePath);
    }

    @Override
    public void delete(String routePath) throws Exception {
        delete(routePath, Boolean.FALSE);
    }

    @Override
    public void delete(String routePath, boolean isDeleteChildren) throws Exception {
        delete(routePath, deleteBuilder -> {
            if (isDeleteChildren) {
                deleteBuilder.deletingChildrenIfNeeded();
            }
        });
    }

    @Override
    public void delete(String routePath, Consumer<DeleteBuilder> deleteBuilderConsumer) throws Exception {
        routePath = getFullPath(routePath);
        DeleteBuilder deleteBuilder = curator.delete();
        if (deleteBuilderConsumer != null) {
            deleteBuilderConsumer.accept(deleteBuilder);
        }
        deleteBuilder.forPath(routePath);
    }

    @Override
    public CuratorFramework getClient() {
        return this.curator;
    }

    @Override
    public ConnectionStateListener getConnectionStateListener(String key) {
        return connectionStateListenerMap.get(key);
    }

    @Override
    public void registerConnectionStateListener(String key, ConnectionStateListener listener) {
        this.curator.getConnectionStateListenable().addListener(listener);
        connectionStateListenerMap.put(key, listener);
    }

    @Override
    public ConnectionStateListener cancellationConnectionStateListener(String key) {
        ConnectionStateListener connectionStateListener = getConnectionStateListener(key);
        if (connectionStateListener != null) {
            this.curator.getConnectionStateListenable().removeListener(connectionStateListener);
        }
        return connectionStateListener;
    }

    @Override
    public TreeCache getTreeCache(String routePath) {
        return treeCacheMap.get(getFullPath(routePath));
    }

    @Override
    public TreeCache registerTreeCache(String routePath, TreeCacheListener listener) {
        cancellationTreeCache(routePath);
        routePath = getFullPath(routePath);
        TreeCache treeCache = new TreeCache(curator, routePath);
        try {
            treeCache.start();
            treeCache.getListenable().addListener(listener);
            treeCacheMap.put(routePath, treeCache);
        } catch (Exception e) {
            log.error("注册监听失败 [routePath {}]", routePath, e);
            return null;
        }
        return treeCache;
    }

    @Override
    public TreeCache cancellationTreeCache(String routePath) {
        TreeCache treeCache = getTreeCache(routePath);
        if (treeCache != null) {
            routePath = getFullPath(routePath);
            try {
                treeCache.close();
                treeCacheMap.remove(routePath);
            } catch (Exception e) {
                log.error("监听关闭失败 [routePath {}]", routePath, e);
                return null;
            }
        }
        return treeCache;
    }

    @Override
    public synchronized void afterPropertiesSet() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(2000, 3);
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder().connectString(zookeeperProperties.getZkConnectString())
                .sessionTimeoutMs(zookeeperProperties.getZkSessionTimeout())
                .retryPolicy(retryPolicy);
        String authString = zookeeperProperties.getUsername() + ":" + zookeeperProperties.getPassword();
        builder.authorization(ZKConfigurationConstant.DIGEST, authString.getBytes());
        builder.aclProvider(new PamirsACLProvider(authString));
        curator = builder.build();
        zookeeperProperties.setRootPath(ZookeeperHelper.repairPath(zookeeperProperties.getRootPath()));

        start();//自动启动
    }
}
