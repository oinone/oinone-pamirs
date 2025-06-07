package pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect.mysql;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.dialect.AbstractMetaDialectService;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DsDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect.IndexMetaDialectService;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.mapper.mysql.IndexMapper;
import pro.shushi.pamirs.meta.common.spi.SPI;

import javax.annotation.Resource;

/**
 * MYSQL索引方言服务
 * <p>
 * 2020/7/16 1:47 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Dialect.component
@SPI.Service
@Component
public class MysqlIndexMetaDialectService extends AbstractMetaDialectService implements IndexMetaDialectService {

    @Resource
    private IndexMapper indexMapper;

    @Override
    public boolean existIndex(String dsKey, String tableName, String indexName) {
        String database = Dialects.component(DsDialectComponent.class, dsKey).getDatabase(dsKey);
        tableName = dialectTableName(dsKey, tableName);
        return indexMapper.countIndexByName(database, tableName, indexName) > 0;
    }

    @Override
    public boolean existIndex(String dsKey, String tableName, String indexName, boolean unique) {
        String database = Dialects.component(DsDialectComponent.class, dsKey).getDatabase(dsKey);
        tableName = dialectTableName(dsKey, tableName);
        return indexMapper.countIndexByNameNonUnique(database, tableName, indexName, unique) > 0;
    }

}
