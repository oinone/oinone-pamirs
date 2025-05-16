package pro.shushi.pamirs.framework.connectors.cdn.factory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.cdn.client.DefaultFileClient;
import pro.shushi.pamirs.framework.connectors.cdn.client.DynamicFileClient;
import pro.shushi.pamirs.framework.connectors.cdn.client.FileClient;
import pro.shushi.pamirs.framework.connectors.cdn.configure.CdnConfig;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

@Slf4j
@Component
public class FileClientFactory {

    public static FileClient getClient() {
        return getClient(null);
    }

    public static FileClient getClient(String key) {
        CdnConfig cdnConfig = BeanDefinitionUtils.getBean(CdnConfig.class);
        //未配置文件系统的情况
        if (cdnConfig == null || cdnConfig.getType() == null) {
            return BeanDefinitionUtils.getBean(DefaultFileClient.class);
        }
        if (StringUtils.isNotBlank(key)) {
            CdnConfig otherCdnConfig = cdnConfig.getOthers().get(key);
            if (otherCdnConfig != null) {
                return new DynamicFileClient(key, otherCdnConfig.getType());
            }
            return null;
        }
        return Spider.getExtension(FileClient.class, cdnConfig.getType());
    }
}
