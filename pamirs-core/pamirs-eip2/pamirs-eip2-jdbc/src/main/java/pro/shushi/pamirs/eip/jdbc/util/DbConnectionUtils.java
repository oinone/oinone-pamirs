package pro.shushi.pamirs.eip.jdbc.util;

import pro.shushi.pamirs.eip.api.model.connector.EipConnector;
import pro.shushi.pamirs.eip.jdbc.manager.EipDataSourceManager;
import pro.shushi.pamirs.eip.jdbc.service.EipJdbcComponent;
import pro.shushi.pamirs.eip.jdbc.spring.EipJdbcComponentManager;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import javax.sql.DataSource;
import java.sql.*;

@Slf4j
public class DbConnectionUtils {

    public static boolean testConn(EipConnector connector) {
        try {
            // jdbc:mysql://127.0.0.1:3306/database
            String url = buildUrl(connector);
            Class.forName(connector.driver());
            //2、获取数据库连接
            DriverManager.setLoginTimeout(50);
            try (Connection conn = DriverManager.getConnection(url, connector.getUser(), connector.getPassword())) {
                DatabaseMetaData dmd = conn.getMetaData();
                ResultSet rs = dmd.getCatalogs();
                while (rs.next()) {
                    String name = rs.getString("TABLE_CAT");
                    log.info("Read database table:{}", name);
                }
                log.info("Get {} database connection successfully!", connector.getConnDBType());
                return Boolean.TRUE;
            }
        } catch (ClassNotFoundException | SQLException e) {
            log.error("Failed to get database connection!", e);
            return Boolean.FALSE;
        }
    }

    public static DataSource buildDataSource(EipConnector connector) {
        String url = buildUrl(connector);
        return EipDataSourceManager.buildSimpleDataSource(url, connector.driver(), connector.getUser(), connector.getPassword(), connector.getConnDBType(), connector.getConnBasicDbType());
    }

    public static String buildUrl(EipConnector connector) {
        String connDBType = connector.getConnDBType();
        EipJdbcComponent jdbcComponent = EipJdbcComponentManager.get(connDBType);
        return jdbcComponent.jdbcUrl(connector);
    }
}