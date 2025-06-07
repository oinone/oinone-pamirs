package pro.shushi.pamirs.file.util;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.file.api.util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Adamancy Zhang at 16:04 on 2025-04-27
 */
public class FileUtilTest {

    @Test
    public void test1() throws IOException {
        try (InputStream inputStream = FileUtil.getRemoteBufferedInputStream("file:///Users/adamancyzhang/Documents/shushi/backend/5.1.0/test.png")) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                System.out.println(inputStream.available());
                IOUtils.copy(inputStream, outputStream);
                outputStream.flush();
            }
        }
    }

    @Test
    public void test2() throws IOException {
        try (InputStream inputStream = FileUtil.getRemoteBufferedInputStream("classpath:test.png")) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                System.out.println(inputStream.available());
                IOUtils.copy(inputStream, outputStream);
                outputStream.flush();
            }
        }
    }
}
