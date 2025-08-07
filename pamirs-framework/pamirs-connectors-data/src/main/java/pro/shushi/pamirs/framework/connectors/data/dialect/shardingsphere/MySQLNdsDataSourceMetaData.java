package pro.shushi.pamirs.framework.connectors.data.dialect.shardingsphere;

import com.google.common.base.Strings;
import org.apache.shardingsphere.infra.database.metadata.DataSourceMetaData;
import org.apache.shardingsphere.infra.database.metadata.UnrecognizedDatabaseURLException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Adamancy Zhang at 10:50 on 2025-08-07
 */
public final class MySQLNdsDataSourceMetaData implements DataSourceMetaData  {

    private static final int DEFAULT_PORT = 3306;

    private final String hostName;

    private final int port;

    private final String catalog;

    private final String schema;

    private final Pattern pattern = Pattern.compile("jdbc:(mysql|nds)(:replication|:failover|:sequential|:aurora)?:(\\w*:)?//([\\w\\-\\.]+):?([0-9]*)/([\\w\\-]+);?\\S*", Pattern.CASE_INSENSITIVE);

    public MySQLNdsDataSourceMetaData(final String url) {
        Matcher matcher = pattern.matcher(url);
        if (!matcher.find()) {
            throw new UnrecognizedDatabaseURLException(url, pattern.pattern());
        }
        hostName = matcher.group(4);
        port = Strings.isNullOrEmpty(matcher.group(5)) ? DEFAULT_PORT : Integer.parseInt(matcher.group(5));
        catalog = matcher.group(6);
        schema = null;
    }

    @Override
    public String getHostName() {
        return hostName;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getCatalog() {
        return catalog;
    }

    @Override
    public String getSchema() {
        return schema;
    }
}
