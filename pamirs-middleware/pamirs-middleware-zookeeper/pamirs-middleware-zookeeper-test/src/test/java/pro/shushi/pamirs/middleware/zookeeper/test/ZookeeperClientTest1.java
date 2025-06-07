package pro.shushi.pamirs.middleware.zookeeper.test;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {ZookeeperApplicationTest.class}, properties = {"test1"})
public class ZookeeperClientTest1 extends AbstractZookeeperClientTest {

    @Override
    protected String getPathPrefix() {
        return "/test1";
    }
}
