package pro.shushi.pamirs.core.common.test;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.core.common.FileReadHelper;

import java.io.*;

/**
 * @author Adamancy Zhang at 14:06 on 2025-04-23
 */
public class FileReadHelperTest {

    private static final String TEST_HOME = "/Users/adamancyzhang/Documents/shushi/backend/5.1.0";

    private static final String TEST_FILE_PATH = TEST_HOME + "/test.png";

    @Test
    public void urlToLocalFile() throws IOException {
        try (InputStream inputStream = FileReadHelper.getResourceByURL("https://pamirs.oss-cn-hangzhou.aliyuncs.com/oinone/img/apps/management_center@2x.png")) {
            assert inputStream != null;
            try (FileOutputStream outputStream = new FileOutputStream(TEST_FILE_PATH)) {
                System.out.println(inputStream.available());
                IOUtils.copy(inputStream, outputStream);
                outputStream.flush();
            }
        }
//        File file = new File(TEST_FILE_PATH);
//        if (file.exists()) {
//            file.delete();
//        }
    }

    @Test
    public void fileReadSize() throws IOException {
        try (InputStream inputStream = FileReadHelper.getResourceByFile(TEST_FILE_PATH)) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                System.out.println(inputStream.available());
                IOUtils.copy(inputStream, outputStream);
                outputStream.flush();
            }
        }
    }
}
