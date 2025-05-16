package pro.shushi.pamirs.middleware.zookeeper.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"pro.shushi.pamirs.middle.zookeeper"})
public class ZookeeperApplicationTest {

    public static void main(String[] args) {
        SpringApplication.run(ZookeeperApplicationTest.class);
    }
}
