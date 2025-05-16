package pro.shushi.pamirs.framework.connectors.cdn;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.shushi.pamirs.framework.connectors.cdn.client.FileClient;
import pro.shushi.pamirs.framework.connectors.cdn.configure.CdnConfig;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@DisplayName("基础服务测试")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CDNApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class AbstractBaseTest {

    private static final String TENANT = "pamirs";

    @Autowired
    private CdnConfig cdnConfig;

    protected FileClient fileClient;

    @BeforeAll
    @Order(0)
    @DisplayName("全部开始之前")
    public static void lifecycle() {
        System.out.println("before all");
    }

    @BeforeEach
    @DisplayName("单个测试方法开始之前")
    public void beforeEach() {
        System.out.println("before each");
        fileClient = FileClientFactory.getClient();
    }

    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

}
