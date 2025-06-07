package pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect.mysql;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.dialect.AbstractMetaDialectService;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DsDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect.ColumnMetaDialectService;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.mapper.mysql.ColumnMapper;
import pro.shushi.pamirs.meta.common.spi.SPI;

import javax.annotation.Resource;

/**
 * MYSQL列方言服务
 * <p>
 * 2020/7/16 1:47 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Dialect.component
@SPI.Service
@Component
public class MysqlColumnMetaDialectService extends AbstractMetaDialectService implements ColumnMetaDialectService {

    @Resource
    private ColumnMapper columnMapper;

    @Override
    public boolean existColumn(String dsKey, String tableName, String columnName) {
        String database = Dialects.component(DsDialectComponent.class, dsKey).getDatabase(dsKey);
        tableName = dialectTableName(dsKey, tableName);
        return 1 == columnMapper.existColumn(database, tableName, columnName);
    }

}
