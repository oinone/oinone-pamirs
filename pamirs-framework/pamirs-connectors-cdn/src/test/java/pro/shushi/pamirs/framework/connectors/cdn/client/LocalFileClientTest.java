package pro.shushi.pamirs.framework.connectors.cdn.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import pro.shushi.pamirs.framework.connectors.cdn.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;

import java.nio.charset.StandardCharsets;

/**
 * LocalFileClientTest
 *
 * @author wangxian
 */
@ActiveProfiles("local")
public class LocalFileClientTest extends AbstractBaseTest {

    private static final Logger log = LoggerFactory.getLogger(LocalFileClientTest.class);

    @Autowired
    private LocalFileClient    localFileClient;
    @Autowired
    private FileClientFactory fileClientFactory;

    private String testFileName    = "testxxxxx.txt";
    private String testFileContent = "测试测试测试测试测试测试";

    private byte[] data = testFileContent.getBytes(StandardCharsets.UTF_8);

    private String url;

    @Test
    public void test_clientInstance() {
        FileClient fileClient = FileClientFactory.getClient();
        Assertions.assertEquals(fileClient, localFileClient);
    }

    @Test
    public void test_deleteByFolder() {
        localFileClient.deleteByFolder("/test_upload");
    }

}
