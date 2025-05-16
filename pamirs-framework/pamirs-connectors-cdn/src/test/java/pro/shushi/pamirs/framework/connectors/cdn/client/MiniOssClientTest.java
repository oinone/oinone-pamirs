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
 * MiniOssClientTest
 *
 * @author wangxian
 */
@ActiveProfiles("minio")
public class MiniOssClientTest extends AbstractBaseTest {

    private static final Logger log = LoggerFactory.getLogger(MiniOssClientTest.class);

    @Autowired
    private MiniOssClient     miniOssClient;
    @Autowired
    private FileClientFactory fileClientFactory;

    private String testFileName    = "testxxxxx.txt";
    private String testFileContent = "测试测试测试测试测试测试";

    private byte[] data = testFileContent.getBytes(StandardCharsets.UTF_8);

    private String url;

    @Test
    public void test_clientInstance() {
        FileClient fileClient = FileClientFactory.getClient();
        Assertions.assertEquals(fileClient, miniOssClient);
    }

    @Test
    public void test_deleteByFolder() {
        miniOssClient.deleteByFolder("/test_upload");
    }

}
