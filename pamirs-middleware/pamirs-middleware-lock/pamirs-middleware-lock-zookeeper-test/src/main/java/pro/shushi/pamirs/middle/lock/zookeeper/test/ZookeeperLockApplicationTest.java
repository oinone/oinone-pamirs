package pro.shushi.pamirs.middle.lock.zookeeper.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"pro.shushi.pamirs.middle.zookeeper", "pro.shushi.pamirs.middle.lock"})
public class ZookeeperLockApplicationTest {

    public static void main(String[] args) {
        SpringApplication.run(ZookeeperLockApplicationTest.class);
    }
}
