package pro.shushi.pamirs.core.common;

import pro.shushi.pamirs.framework.connectors.cdn.constant.FileConstants;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.util.AppClassLoader;

import java.io.*;
import java.net.URL;

/**
 * 文件读取帮助类
 *
 * @author Adamancy Zhang at 16:23 on 2024-05-28
 */
@Slf4j
public class FileReadHelper {

    private FileReadHelper() {
        //reject create object
    }

    /**
     * 获取一个文件资源
     *
     * @param path 资源路径（支持本地路径/classpath/远程路径）
     * @return 文件输入流
     */
    public static InputStream getResource(String path) {
        InputStream inputStream = FileReadHelper.getResourceByFile(path);
        if (inputStream != null) {
            return inputStream;
        }
        inputStream = FileReadHelper.getResourceByClassLoader(path);
        if (inputStream != null) {
            return inputStream;
        }
        return FileReadHelper.getResourceByURL(path);
    }

    /**
     * 通过本地路径获取一个文件资源
     *
     * @param path 本地路径
     * @return 文件输入流
     */
    public static InputStream getResourceByFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                try {
                    return new FileInputStream(path);
                } catch (FileNotFoundException e) {
                    log.error("File not found. path: {}", path, e);
                    return null;
                }
            } else {
                log.error("{} is not file.", path);
                return null;
            }
        }
        return null;
    }

    /**
     * 通过classpath获取一个文件资源
     *
     * @param path classpath
     * @return 文件输入流
     */
    public static InputStream getResourceByClassLoader(String path) {
        return AppClassLoader.getClassLoader(FileReadHelper.class).getResourceAsStream(path);
    }

    /**
     * 通过URL获取一个文件资源
     *
     * @param path url
     * @return 文件输入流
     */
    public static InputStream getResourceByURL(String path) {
        try {
            if (path.startsWith(FileConstants.LOCAL_PREFIX)) {
                return FileClientFactory.getClient().getDownloadStream(path);
            }
            URL url = new URL(path);
            return new BufferedInputStream(url.openStream());
        } catch (IOException e) {
            log.error("Remote resource fetch error.", e);
            return null;
        }
    }
}