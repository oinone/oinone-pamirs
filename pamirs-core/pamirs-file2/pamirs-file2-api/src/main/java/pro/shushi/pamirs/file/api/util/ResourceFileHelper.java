package pro.shushi.pamirs.file.api.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import pro.shushi.pamirs.framework.connectors.cdn.client.FileClient;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 资源文件帮助类
 *
 * @author Adamancy Zhang on 2021-05-08 14:12
 */
@Slf4j
public class ResourceFileHelper {

    private ResourceFileHelper() {
        //reject create object
    }

    /**
     * 获取本地资源文件并进行上传
     *
     * @param fileClient        文件客户端
     * @param resourcePattern   资源匹配表达式
     * @param filenameGenerator 文件名生成器，最终拼接在路径中，可指定文件夹路径
     * @return 生成的文件名和上传的url形成的映射对象
     * @throws IOException 可能抛出I/O异常
     */
    public static Map<String, String> uploadByResources(FileClient fileClient, String resourcePattern, Function<Resource, String> filenameGenerator) throws IOException {
        return getResources(resourcePattern, filenameGenerator, (resource, filename) -> {
            try (InputStream is = resource.getInputStream()) {
                String url = fileClient.uploadByFileName(filename, IOUtils.toByteArray(is));
                log.info("upload: {}", url);
                return url;
            }
        });
    }

    /**
     * 获取本地资源文件并获取下载路径<br/>
     * 建议调用参数与{@link ResourceFileHelper#uploadByResources(FileClient, String, Function)}方法一致<br/>
     *
     * @param fileClient        文件客户端
     * @param resourcePattern   资源匹配表达式
     * @param filenameGenerator 文件名生成器，最终拼接在路径中，可指定文件夹路径
     * @return 生成的文件名和下载的url形成的映射对象
     * @throws IOException 可能抛出I/O异常
     */
    public static Map<String, String> getDownloadUrlByResources(FileClient fileClient, String resourcePattern, Function<Resource, String> filenameGenerator) throws IOException {
        return getResources(resourcePattern, filenameGenerator, (resource, filename) -> fileClient.getDownloadUrl(filename));
    }

    /**
     * 获取资源文件
     *
     * @param resourcePattern   资源匹配表达式
     * @param filenameGenerator 文件名生成器
     * @param consumer          消费本地资源和生成的文件名，并返回一个可识别的值
     * @return 生成的文件名和可识别的值形成的映射对象
     * @throws IOException 可能抛出I/O异常
     */
    public static Map<String, String> getResources(String resourcePattern, Function<Resource, String> filenameGenerator, ResourceConsumer consumer) throws IOException {
        PathMatchingResourcePatternResolver finder = new PathMatchingResourcePatternResolver(ResourceFileHelper.class.getClassLoader());
        Resource[] resources = finder.getResources(resourcePattern);
        Map<String, String> result = new HashMap<>(resources.length);
        for (Resource resource : resources) {
            String filename = filenameGenerator.apply(resource);
            if (StringUtils.isBlank(filename) || result.containsKey(filename)) {
                continue;
            }
            String value = consumer.accept(resource, filename);
            if (StringUtils.isBlank(value)) {
                continue;
            }
            result.put(filename, value);
        }
        return result;
    }

    /**
     * 资源消费者
     */
    @FunctionalInterface
    public interface ResourceConsumer {

        /**
         * 资源消费逻辑
         *
         * @param resource 获取的资源对象
         * @param filename 处理完成的文件名 {@link Map.Entry#keySet()}
         * @return 需要传出的值 {@link Map.Entry#values()}
         * @throws IOException 当读取{@link Resource}时可能抛出的I/O异常
         */
        String accept(Resource resource, String filename) throws IOException;
    }
}
