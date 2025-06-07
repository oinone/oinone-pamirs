package pro.shushi.pamirs.framework.connectors.data.elastic.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * AbstractBaseTest
 *
 * @author yakir on 2019/05/03 03:49.
 */
@DisplayName("基础服务测试")
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestElasticRestApp.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract
public class AbstractBaseTest {

    private static final Logger log = LoggerFactory.getLogger(AbstractBaseTest.class);


}