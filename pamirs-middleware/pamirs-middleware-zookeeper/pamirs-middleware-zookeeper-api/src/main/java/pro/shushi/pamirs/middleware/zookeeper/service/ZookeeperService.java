package pro.shushi.pamirs.middleware.zookeeper.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.data.Stat;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

public interface ZookeeperService {

    void start() throws Exception;

    void close() throws Exception;

    boolean isEnabled();

    String getRootPath();

    String getFullPath(String routePath);

    Stat getStat(String routePath) throws Exception;

    boolean checkExists(String routePath) throws Exception;

    void create(String routePath) throws Exception;

    void create(String routePath, Consumer<CreateBuilder> createBuilderConsumer) throws Exception;

    void createOrUpdateData(String routePath, byte[] data) throws Exception;

    void createOrUpdateData(String routePath, byte[] data, Consumer<CreateBuilder> createBuilderConsumer, Consumer<SetDataBuilder> setDataBuilderConsumer) throws Exception;

    void createOrUpdateData(String routePath, byte[] data, BiPredicate<byte[], byte[]> comparator) throws Exception;

    void createOrUpdateData(String routePath, byte[] data, BiPredicate<byte[], byte[]> comparator, Consumer<CreateBuilder> createBuilderConsumer, Consumer<SetDataBuilder> setDataBuilderConsumer) throws Exception;

    byte[] getData(String routePath) throws Exception;

    byte[] getData(String routePath, Consumer<GetDataBuilder> deleteBuilderConsumer) throws Exception;

    void delete(String routePath) throws Exception;

    void delete(String routePath, boolean isDeleteChildren) throws Exception;

    void delete(String routePath, Consumer<DeleteBuilder> deleteBuilderConsumer) throws Exception;

    CuratorFramework getClient();

    ConnectionStateListener getConnectionStateListener(String key);

    void registerConnectionStateListener(String key, ConnectionStateListener listener);

    ConnectionStateListener cancellationConnectionStateListener(String key);

    TreeCache getTreeCache(String routePath);

    TreeCache registerTreeCache(String routePath, TreeCacheListener listener);

    TreeCache cancellationTreeCache(String routePath);
}
