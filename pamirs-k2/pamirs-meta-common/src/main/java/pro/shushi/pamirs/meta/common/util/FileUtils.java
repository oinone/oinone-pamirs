package pro.shushi.pamirs.meta.common.util;

import org.apache.commons.io.IOUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

/**
 * 文件工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:20 下午
 */
public class FileUtils {

    public static String read(String path) {
        try {
            URL url = ResourceUtils.getURL(path);
            return IOUtils.toString(url.openStream(), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    public static String readResourceFirst(String path) {
        List<String> contents = readResource(path);
        if (!CollectionUtils.isEmpty(contents)) {
            return contents.get(0);
        }
        return null;
    }

    public static List<String> readResource(String path) {
        try {
            Enumeration<URL> urls = FileUtils.class.getClassLoader().getResources(path);
            if (!urls.hasMoreElements()) {
                return null;
            }
            List<String> contents = new ArrayList<>();
            while (urls.hasMoreElements()) {
                URL p = urls.nextElement();
                contents.add(IOUtils.toString(p.openStream(), StandardCharsets.UTF_8.name()));
            }
            return contents;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    public static void write(String path, String content) {
        writeDirect(Objects.requireNonNull(FileUtils.class.getClassLoader().getResource(".")).getPath() + path, content);
    }

    @SuppressWarnings("unused")
    public static void writeResource(String path, String content) {
        writeDirect(Objects.requireNonNull(FileUtils.class.getClassLoader().getResource(path)).getPath(), content);
    }

    public static void writeDirect(String absolutePath, String content) {
        try {
            PrintWriter writer = new PrintWriter(new File(absolutePath));
            IOUtils.write(content, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
