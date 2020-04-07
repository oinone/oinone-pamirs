package pro.shushi.pamirs.meta.api.core.configure.yaml;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.MetaApiFactory;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.DataConfigurationFetcher;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsDataConfiguration;

/**
 * 数据持久化配置获取
 *
 * 逻辑删除、乐观锁
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@Component
public class ConfigurationFetcher {

    public PamirsDataConfiguration config() {
        return MetaApiFactory.getApi(DataConfigurationFetcher.class).config();
    }

}
