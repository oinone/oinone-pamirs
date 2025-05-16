package pro.shushi.pamirs.framework.connectors.data.dialect.api;

import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 表配置 方言服务
 * <p>
 * 2023/06/25
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface TableInfoDialectService {

    void fillDefaultConfig(String dsKey, PamirsTableInfo pamirsTableInfo);

}
