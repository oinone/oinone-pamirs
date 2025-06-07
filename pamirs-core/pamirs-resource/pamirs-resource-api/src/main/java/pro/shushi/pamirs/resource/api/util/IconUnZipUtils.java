package pro.shushi.pamirs.resource.api.util;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author yexiu at 19:26 on 2024/12/16
 */
public class IconUnZipUtils {

    public static Result unzipFromStream(InputStream inputStream) throws IOException {
        Result result = new Result();
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                // 预先校验文件是否符合需求
                if (!isValidFile(fileName)) {
                    zipInputStream.closeEntry();
                    continue;
                }
                try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                    extractFileToStream(zipInputStream, byteArrayOutputStream);
                    addFile(result, fileName, byteArrayOutputStream.toByteArray());
                } finally {
                    zipInputStream.closeEntry();
                }
            }
        }
        return result;
    }

    private static boolean isValidFile(String fileName) {
        return fileName.endsWith(".css") || fileName.endsWith(".js") || fileName.endsWith(".json") ||
                fileName.endsWith(".ttf") || fileName.endsWith(".woff") || fileName.endsWith(".woff2");
    }

    private static void addFile(Result result, String fileName, byte[] fileData) {
        if (fileName.endsWith(".css") && !fileName.contains("demo")) {
            result.getCssNameMap().put(fileName, fileData);
        } else if (fileName.endsWith(".js")) {
            result.getJsNameMap().put(fileName, fileData);
        } else if (fileName.endsWith(".json")) {
            result.getJsonNameMap().put(fileName, fileData);
        } else if (fileName.endsWith(".ttf") || fileName.endsWith(".woff") || fileName.endsWith(".woff2")) {
            result.getFontNameMap().put(fileName, fileData);
        }
    }

    private static void extractFileToStream(ZipInputStream zipIn, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = zipIn.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }

    @Data
    public static class Result {
        private final Map<String, byte[]> cssNameMap = new HashMap<>();
        private final Map<String, byte[]> jsNameMap = new HashMap<>();
        private final Map<String, byte[]> jsonNameMap = new HashMap<>();
        private final Map<String, byte[]> fontNameMap = new HashMap<>();

    }
}
