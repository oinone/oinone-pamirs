package pro.shushi.pamirs.framework.connectors.cdn.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import pro.shushi.pamirs.framework.connectors.cdn.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFile;

import java.nio.charset.StandardCharsets;

/**
 * UpyunOSSClientTest
 *
 * @author yakir on 2021/01/04 18:06.
 */
@ActiveProfiles("upyun")
public class UpyunOSSClientTest extends AbstractBaseTest {

    private static final Logger log = LoggerFactory.getLogger(UpyunOSSClientTest.class);

    @Autowired
    private UpyunOSSClient upyunOSSClient;
    @Autowired
    private FileClientFactory fileClientFactory;

    private String testFileName = "testxxxxx.txt";
    private String testFileContent = "测试测试测试测试测试测试";

    private byte[] data = testFileContent.getBytes(StandardCharsets.UTF_8);

    private String url;

    @Test
    public void test_clientInstance() {

        FileClient fileClient = FileClientFactory.getClient();

        Assertions.assertEquals(fileClient, upyunOSSClient);
    }

    @Test
    public void test_upload() {

        CdnFile cdnFile = upyunOSSClient.upload(testFileName, data);

        Assertions.assertNotEquals(cdnFile, null);

        String name = cdnFile.getName();
        this.url = cdnFile.getUrl();
        String type = cdnFile.getType();

        // https://mall-cdn.daddylab.com/test_upload/2021/01/05/test.txt

        Assertions.assertEquals(name, testFileName);

    }

    @Test
    public void test_uploadByFileName() {

        String downloadUrl = upyunOSSClient.uploadByFileName(testFileName, data);

        Assertions.assertEquals(
                "https://mall-cdn.daddylab.com/test_upload/daddylab/test.txt",
                downloadUrl
        );

    }

    @Test
    public void test_deleteByFolder() {

        upyunOSSClient.deleteByFolder("/test_upload");

    }

}
