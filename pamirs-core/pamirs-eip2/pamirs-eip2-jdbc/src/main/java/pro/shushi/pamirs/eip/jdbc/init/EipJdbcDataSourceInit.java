package pro.shushi.pamirs.eip.jdbc.init;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.SystemBootAfterInit;
import pro.shushi.pamirs.eip.api.config.EipSwitchCondition;
import pro.shushi.pamirs.eip.api.enmu.connector.ConnType;
import pro.shushi.pamirs.eip.api.enmu.connector.TestConnStatus;
import pro.shushi.pamirs.eip.api.model.connector.ConnDbType;
import pro.shushi.pamirs.eip.api.model.connector.EipConnector;
import pro.shushi.pamirs.eip.jdbc.config.EipJdbcProperties;
import pro.shushi.pamirs.eip.jdbc.helper.EipConnectorHelper;
import pro.shushi.pamirs.eip.jdbc.service.EipJdbcDistributionSupport;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * JDBC数据源初始化
 *
 * @author Adamancy Zhang at 14:18 on 2024-06-05
 */
@Slf4j
@Order(88)
@Component
@Conditional(EipSwitchCondition.class)
public class EipJdbcDataSourceInit implements SystemBootAfterInit {

    @Autowired(required = false)
    private EipJdbcProperties eipJdbcProperties;

    @Autowired(required = false)
    private EipJdbcDistributionSupport jdbcDistributionSupport;

    @Override
    public boolean init(AppLifecycleCommand command) {
        initDbTypes();
        if (eipJdbcProperties != null) {
            initConnector();
            if (jdbcDistributionSupport != null) {
                try {
                    jdbcDistributionSupport.start();
                    log.info("eip jdbc distribution supported.");
                } catch (Exception e) {
                    log.info("eip jdbc distribution unsupported.", e);
                }
            }
        }
        return true;
    }

    @Override
    public int priority() {
        return 88;
    }

    private void initDbTypes() {

        List<ConnDbType> types = new ArrayList<>();

        ConnDbType mysql = new ConnDbType();
        mysql.setCode("MySQL");
        mysql.setDisplayName("MySQL");
        mysql.setHelp("MySQL");
        mysql.setDriver("com.mysql.jdbc.Driver");
        mysql.setBasic(true);

        ConnDbType sqlServer = new ConnDbType();
        sqlServer.setCode("SQLServer");
        sqlServer.setDisplayName("SQL Server");
        sqlServer.setHelp("SQL Server");
        sqlServer.setDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        sqlServer.setBasic(true);

        ConnDbType oracle = new ConnDbType();
        oracle.setCode("Oracle");
        oracle.setDisplayName("Oracle");
        oracle.setHelp("Oracle");
        oracle.setDriver("oracle.jdbc.OracleDriver");
        oracle.setBasic(true);

        ConnDbType postgreSQL = new ConnDbType();
        postgreSQL.setCode("PostgreSQL");
        postgreSQL.setDisplayName("PostgreSQL");
        postgreSQL.setHelp("PostgreSQL");
        postgreSQL.setDriver("org.postgresql.Driver");
        postgreSQL.setBasic(true);

        ConnDbType kingbase8v9 = new ConnDbType();
        kingbase8v9.setCode("Kingbase");
        kingbase8v9.setDisplayName("Kingbase");
        kingbase8v9.setHelp("Kingbase");
        kingbase8v9.setDriver("com.kingbase8.Driver");
        kingbase8v9.setBasic(false);

        ConnDbType dmv8 = new ConnDbType();
        dmv8.setCode("DM");
        dmv8.setDisplayName("DM");
        dmv8.setHelp("DM");
        dmv8.setDriver("dm.jdbc.driver.DmDriver");
        dmv8.setBasic(false);

        ConnDbType hana = new ConnDbType();
        hana.setCode("HANA");
        hana.setDisplayName("HANA");
        hana.setHelp("HANA");
        hana.setDriver("com.sap.db.jdbc.Driver");
        hana.setBasic(false);

        types.add(mysql);
        types.add(sqlServer);
        types.add(oracle);
        types.add(postgreSQL);
        types.add(kingbase8v9);
        types.add(dmv8);
        types.add(hana);

        new ConnDbType().createOrUpdateBatch(types);
    }

    private void initConnector() {
        List<EipConnector> connectors = queryAllValidDbConnectors();
        if (CollectionUtils.isEmpty(connectors)) {
            log.info("camel data source is not initialized. cause: connector count is zero.");
            return;
        }
        for (EipConnector connector : connectors) {
            try {
                EipConnectorHelper.initConnector(connector);
            } catch (Throwable e) {
                log.error("init connector data source error.", e);
            }
        }
        log.info("connector data source is initialized. {}", connectors.size());
    }

    private List<EipConnector> queryAllValidDbConnectors() {
        return new EipConnector().queryList(Pops.<EipConnector>lambdaQuery()
                .from(EipConnector.MODEL_MODEL)
                .eq(EipConnector::getType, ConnType.DB)
                .eq(EipConnector::getTestConnStatus, TestConnStatus.CONN_SUCCESS));
    }
}
