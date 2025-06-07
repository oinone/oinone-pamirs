package pro.shushi.pamirs.framework.connectors.data.dialect.api;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 脚本执行方言服务
 * <p>
 * 2020/7/16 1:47 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface TableMetaDialectService {

    boolean existTable(String dsKey, String tableName);

}
