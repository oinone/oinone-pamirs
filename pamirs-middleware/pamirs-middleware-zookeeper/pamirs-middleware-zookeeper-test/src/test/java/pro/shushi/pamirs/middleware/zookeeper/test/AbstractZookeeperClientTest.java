package pro.shushi.pamirs.middleware.zookeeper.test;

import org.apache.curator.framework.api.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class AbstractZookeeperClientTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected abstract String getPathPrefix();

    @Autowired
    private ZookeeperService zookeeperService;

    @Before
    public void beforeRunner() throws Exception {
        //启动zookeeper连接
        zookeeperService.start();
        //创建测试类的根路径
        zookeeperService.create(getPath(""));
    }

    @Test
    public void test001() throws Exception {
        //单节点类型测试
        create("/a", null);//持久节点测试
        create("/b", createBuilder -> createBuilder.withMode(CreateMode.EPHEMERAL));//关闭连接自动删除节点测试
        create("/c/a", createBuilder -> createBuilder.creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL));//关闭连接自动删除节点测试
        create("/c/b", createBuilder -> createBuilder.creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL));//关闭连接自动删除节点测试
        create("/d/a", createBuilder -> createBuilder.creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL));//关闭连接自动删除节点测试
        create("/d/b", createBuilder -> createBuilder.creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL));//关闭连接自动删除节点测试

        //关闭zookeeper连接
        zookeeperService.close();

        //重新连接zookeeper
        zookeeperService.start();

        isExists("/a");//持久节点是否存在
        delete("/a", null);//删除持久节点
        isNotExists("/b");//关闭连接自动删除节点是否不存在
        isExists("/c");//关闭连接自动删除节点是否不存在
        isNotExists("/c/a");//关闭连接自动删除节点是否不存在
        isNotExists("/c/b");//关闭连接自动删除节点是否不存在
        delete("/c", null);//删除
        isExists("/d");//关闭连接自动删除节点是否不存在
        isNotExists("/d/a");//关闭连接自动删除节点是否不存在
        isNotExists("/d/b");//关闭连接自动删除节点是否不存在
        delete("/d", null);//删除
    }

    @Test
    public void test002() throws Exception {
        //子节点删除测试
        create("/e/a", createBuilder -> createBuilder.creatingParentsIfNeeded());
        create("/e/b", createBuilder -> createBuilder.creatingParentsIfNeeded());
        notAllowDelete("/e", null);
        isExists("/e");
        isExists("/e/a");
        isExists("/e/b");
        delete("/e", ChildrenDeletable::deletingChildrenIfNeeded);

        //容器子节点删除测试
        create("/f/a", createBuilder -> createBuilder.creatingParentContainersIfNeeded());
        create("/f/b", createBuilder -> createBuilder.creatingParentContainersIfNeeded());
        notAllowDelete("/f", null);
        isExists("/f");
        isExists("/f/a");
        isExists("/f/b");
        delete("/f", ChildrenDeletable::deletingChildrenIfNeeded);
    }

    @Test
    public void test003() throws Exception {
        //数据创建或更新测试

        byte[] d0 = "0".getBytes();
        byte[] d1 = "1".getBytes();

        //创建首个节点
        createOrUpdateData("/g/a", d0, null, CreateBuilderMain::creatingParentsIfNeeded, null);

        //尝试更新
        createOrUpdateData("/g/a", d0, (oldData, newData) -> Boolean.FALSE, null, null);

        //尝试更新
        createOrUpdateData("/g/a", d1, (oldData, newData) -> Boolean.TRUE, null, null);

        //尝试更新
        createOrUpdateData("/g/a", d0, (oldData, newData) -> Boolean.TRUE, null, null);

        //尝试更新
        createOrUpdateData("/g/a", d1, (oldData, newData) -> Boolean.FALSE, null, null);

        //删除测试节点
        delete("/g", ChildrenDeletable::deletingChildrenIfNeeded);
    }

    @After
    public void afterAfterRunner() throws Exception {
        //删除测试类的根路径
        zookeeperService.delete(null, Boolean.TRUE);
        //关闭zookeeper连接
        zookeeperService.close();
    }

    protected String getPath(String path) {
        return getPathPrefix() + path;
    }

    protected void create(String path, Consumer<CreateBuilder> createBuilderConsumer) throws Exception {
        path = getPath(path);
        zookeeperService.create(path, createBuilderConsumer);
        Assert.assertTrue(path + "创建失败", zookeeperService.checkExists(path));
    }

    protected void createOrUpdateData(String path, byte[] data, BiPredicate<byte[], byte[]> comparator, Consumer<CreateBuilder> createBuilderConsumer, Consumer<SetDataBuilder> setDataBuilderConsumer) throws Exception {
        path = getPath(path);
        byte[] oldData = zookeeperService.getData(path);
        boolean isSetValue = Boolean.TRUE;
        if (comparator != null) {
            isSetValue = comparator.test(oldData, data);
        }
        zookeeperService.createOrUpdateData(path, data, comparator, createBuilderConsumer, setDataBuilderConsumer);
        byte[] newData = zookeeperService.getData(path);
        if (isSetValue) {
            Assert.assertArrayEquals(newData, data);
        } else {
            Assert.assertArrayEquals(newData, oldData);
        }
    }

    protected void delete(String path, Consumer<DeleteBuilder> deleteBuilderConsumer) throws Exception {
        path = getPath(path);
        zookeeperService.delete(path, deleteBuilderConsumer);
        Assert.assertFalse(path + "删除失败", zookeeperService.checkExists(path));
    }

    protected void notAllowDelete(String path, Consumer<DeleteBuilder> deleteBuilderConsumer) {
        path = getPath(path);
        try {
            zookeeperService.delete(path, deleteBuilderConsumer);
            Assert.assertTrue(path + "允许删除", zookeeperService.checkExists(path));
        } catch (Exception e) {
            Assert.assertTrue(path + "删除失败", e instanceof KeeperException.NotEmptyException);
        }
    }

    protected void isExists(String path) throws Exception {
        path = getPath(path);
        Assert.assertTrue(path + "不存在", zookeeperService.checkExists(path));
    }

    protected void isNotExists(String path) throws Exception {
        path = getPath(path);
        Assert.assertFalse(path + "存在", zookeeperService.checkExists(path));
    }
}
