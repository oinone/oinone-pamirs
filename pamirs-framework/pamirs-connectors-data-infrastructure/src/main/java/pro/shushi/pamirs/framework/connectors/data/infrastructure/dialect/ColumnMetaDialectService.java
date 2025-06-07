package pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect;

import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 列元数据方言服务
 * <p>
 * 2020/7/16 1:47 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ColumnMetaDialectService {

    boolean existColumn(String dsKey, String tableName, String columnName);

}
