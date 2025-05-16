package pro.shushi.pamirs.framework.connectors.data.dialect;

import pro.shushi.pamirs.framework.connectors.data.dialect.api.DsDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.TableMetaDialectService;

import java.util.Objects;

/**
 * 抽象表格原数据方言服务
 *
 * @author Adamancy Zhang at 18:23 on 2023-06-21
 */
public abstract class AbstractTableMetaDialectService extends AbstractMetaDialectService implements TableMetaDialectService {
    @Override
    public boolean existTable(String dsKey, String tableName) {
        tableName = dialectTableName(dsKey, tableName);
        return Objects.requireNonNull(Dialects.component(DsDialectComponent.class, dsKey)).existTable(dsKey, tableName);
    }
}
