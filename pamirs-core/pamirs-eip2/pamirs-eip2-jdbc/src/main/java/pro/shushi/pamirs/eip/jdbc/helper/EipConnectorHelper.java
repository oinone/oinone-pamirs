package pro.shushi.pamirs.eip.jdbc.helper;

import pro.shushi.pamirs.eip.api.enmu.connector.ConnDBType;
import pro.shushi.pamirs.eip.api.enmu.connector.ConnType;
import pro.shushi.pamirs.eip.api.model.connector.EipConnector;
import pro.shushi.pamirs.eip.jdbc.camel.EipSqlPrepareStatementStrategy;
import pro.shushi.pamirs.eip.jdbc.manager.EipDataSourceManager;
import pro.shushi.pamirs.eip.jdbc.manager.EipPrepareStatementManager;
import pro.shushi.pamirs.eip.jdbc.util.DbConnectionUtils;

/**
 * @author Adamancy Zhang at 17:56 on 2025-01-26
 */
public class EipConnectorHelper {

    private EipConnectorHelper() {
        // reject create object
    }

    public static void initConnector(EipConnector connector) {
        ConnType connectorType = connector.getType();
        if (connectorType == null) {
            throw new IllegalArgumentException("Invalid connector type.");
        }
        switch (connectorType) {
            case DB:
                initCamelDataSource(connector);
                initEipPrepareStatement(connector);
                break;
            default:
                throw new IllegalArgumentException("Invalid connector type. value: " + connectorType);
        }
    }

    public static String initCamelDataSource(EipConnector connector) {
        String dsKey = String.valueOf(connector.getId());
        String dataSourceBeanName = EipDataSourceManager.generatorId(dsKey);
        EipDataSourceManager.register(dsKey, () -> DbConnectionUtils.buildDataSource(connector));
        return dataSourceBeanName;
    }

    public static String initEipPrepareStatement(EipConnector connector) {
        ConnDBType connDBType = connector.getConnDBType();
        switch (connDBType) {
            case SQLServer:
                EipPrepareStatementManager.register(connDBType.name(), EipSqlPrepareStatementStrategy.DEFAULT_SEPARATOR, "[DEFAULT]");
                return EipPrepareStatementManager.generatorId(connDBType.name());
            default:
                return EipSqlPrepareStatementStrategy.NAME;
        }
    }
}
