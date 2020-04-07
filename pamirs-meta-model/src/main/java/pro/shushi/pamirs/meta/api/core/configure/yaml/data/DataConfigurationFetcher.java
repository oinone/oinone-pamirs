package pro.shushi.pamirs.meta.api.core.configure.yaml.data;

import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsDataConfiguration;

/**
 * 数据配置获取接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
public interface DataConfigurationFetcher {

    PamirsDataConfiguration config();

}
