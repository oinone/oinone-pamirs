package pro.shushi.pamirs.framework.connectors.data.dialect.shardingsphere;

import org.apache.shardingsphere.infra.database.type.BranchDatabaseType;
import org.apache.shardingsphere.infra.database.type.DatabaseType;
import org.apache.shardingsphere.infra.database.type.DatabaseTypeRegistry;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Adamancy Zhang at 10:45 on 2025-08-07
 */
public final class MySQLNdsType implements BranchDatabaseType {

    public static final String NAME = "MySQL-Nds";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Collection<String> getJdbcUrlPrefixes() {
        return Arrays.asList("jdbc:nds:");
    }

    @Override
    public MySQLNdsDataSourceMetaData getDataSourceMetaData(final String url, final String username) {
        return new MySQLNdsDataSourceMetaData(url);
    }

    @Override
    public DatabaseType getTrunkDatabaseType() {
        return DatabaseTypeRegistry.getActualDatabaseType("MySQL");
    }
}
