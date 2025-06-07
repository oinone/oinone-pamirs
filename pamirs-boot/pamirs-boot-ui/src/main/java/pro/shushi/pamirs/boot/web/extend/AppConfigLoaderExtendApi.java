package pro.shushi.pamirs.boot.web.extend;

import pro.shushi.pamirs.boot.base.model.AppConfig;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;

/**
 * 应用配置加载扩展API
 *
 * @author Adamancy Zhang at 10:09 on 2024-06-21
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AppConfigLoaderExtendApi {

    List<AppConfig> queryAfterProperties(List<AppConfig> appConfigs);
}
