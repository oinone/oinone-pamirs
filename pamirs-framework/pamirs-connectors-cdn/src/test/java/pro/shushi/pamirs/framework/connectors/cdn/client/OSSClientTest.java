package pro.shushi.pamirs.framework.connectors.cdn.client;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.framework.connectors.cdn.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFileForm;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2020-12-02 09:52
 */
@DisplayName("公共资源中心单元测试")
public class OSSClientTest extends AbstractBaseTest {

    private static final String FOLDER = "test/";

    private static final String FILENAME = "a.txt";

    private static final String FILE_CONTENT = "Hello World!";

    @Test
    public void getFormData() {
        CdnFileForm form = fileClient.getFormData(FILENAME);

    }

    @Test
    public void uploadByFilename() throws IOException {
        uploadByFilename(FILENAME);
    }

    @Test
    public void deleteByFolder() throws IOException {
        List<String> list = Lists.newArrayList(
                FOLDER + FILENAME,
                FOLDER + FOLDER + "a.txt",
                FOLDER + FOLDER + "b.txt"
        );
        for (String item : list) {
            uploadByFilename(item);
            assert fileClient.isExistByFilename(item);
        }
        fileClient.deleteByFolder(FOLDER);
        for (String item : list) {
            assert !fileClient.isExistByFilename(item);
        }
    }

    @Test
    public void deleteByFilename() throws IOException {
        uploadByFilename();
        assert fileClient.isExistByFilename(FILENAME);
        fileClient.deleteByFilename(FILENAME);
        assert !fileClient.isExistByFilename(FILENAME);
    }

    private void uploadByFilename(String filename) throws IOException {
        System.out.println(fileClient.uploadByFileName(filename, FILE_CONTENT.getBytes(StandardCharsets.UTF_8)));
    }
}
