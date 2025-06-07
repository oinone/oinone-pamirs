package pro.shushi.pamirs.middleware.zookeeper.test;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {ZookeeperApplicationTest.class}, properties = {"test2"})
public class ZookeeperClientTest2 extends AbstractZookeeperClientTest {

    @Override
    protected String getPathPrefix() {
        return "/test2";
    }
}
