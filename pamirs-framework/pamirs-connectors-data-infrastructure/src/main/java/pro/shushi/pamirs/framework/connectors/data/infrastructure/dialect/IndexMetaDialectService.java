package pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect;

import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 索引元数据方言服务
 * <p>
 * 2020/7/16 1:47 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface IndexMetaDialectService {

    boolean existIndex(String dsKey, String tableName, String indexName);

    boolean existIndex(String dsKey, String tableName, String indexName, boolean unique);

}
