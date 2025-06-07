package pro.shushi.pamirs.eip.jdbc.util;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.model.connector.EipConnector;
import pro.shushi.pamirs.eip.jdbc.manager.EipDataSourceManager;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import javax.sql.DataSource;
import java.sql.*;

@Slf4j
public class DbConnectionUtils {

    public static boolean testConn(EipConnector connector) {
        try {
            // jdbc:mysql://127.0.0.1:3306/database
            String url = buildUrl(connector);
            Class.forName(connector.getConnDBType().getDriver());
            //2、获取数据库连接
            DriverManager.setLoginTimeout(50);
            try (Connection conn = DriverManager.getConnection(url, connector.getUser(), connector.getPassword())) {
                DatabaseMetaData dmd = conn.getMetaData();
                ResultSet rs = dmd.getCatalogs();
                while (rs.next()) {
                    String name = rs.getString("TABLE_CAT");
                    log.info("读取数据库表:{}", name);
                }
                log.info("获取{}数据库连接成功！", connector.getConnDBType());
                return Boolean.TRUE;
            }
        } catch (ClassNotFoundException | SQLException e) {
            log.error("获取数据库连接失败！", e);
            return Boolean.FALSE;
        }
    }

    public static DataSource buildDataSource(EipConnector connector) {
        String url = buildUrl(connector);
        return EipDataSourceManager.buildSimpleDataSource(url, connector.getConnDBType().getDriver(), connector.getUser(), connector.getPassword());
    }

    public static String buildUrl(EipConnector connector) {
        String urlTemplate;
        String url;
        String link;
        switch (connector.getConnDBType()) {
            case MySQL:
                urlTemplate = "jdbc:mysql://%s:%s/%s";
                url = String.format(urlTemplate, connector.getHost(), connector.getPort(), connector.getDatabase());
                link = "?";
                break;
            case SQLServer:
                urlTemplate = "jdbc:sqlserver://%s:%s;databaseName=%s";
                url = String.format(urlTemplate, connector.getHost(), connector.getPort(), connector.getDatabase());
                link = ";";
                break;
            case Oracle:
                urlTemplate = "jdbc:oracle:thin:@%s:%s/%s";
                url = String.format(urlTemplate, connector.getHost(), connector.getPort(), connector.getSid());
                link = ";";
                break;
            case PostgreSQL:
                urlTemplate = "jdbc:postgresql://%s:%s/%s";
                url = String.format(urlTemplate, connector.getHost(), connector.getPort(), connector.getDatabase());
                link = "?";
                break;
            default:
                throw PamirsException.construct(EipExpEnumerate.EIP_DB_TYPE_ERROR).errThrow();
        }

        // 附加参数
        if (StringUtils.isNotBlank(connector.getExtParam())) {
            url = url + link + connector.getExtParam();
        }
        return url;
    }
}