package pro.shushi.pamirs.eip.jdbc.helper;

import pro.shushi.pamirs.eip.api.enmu.connector.ConnType;
import pro.shushi.pamirs.eip.api.model.connector.EipConnector;
import pro.shushi.pamirs.eip.jdbc.camel.EipSqlPrepareStatementStrategy;
import pro.shushi.pamirs.eip.jdbc.manager.EipDataSourceManager;
import pro.shushi.pamirs.eip.jdbc.manager.EipPrepareStatementManager;
import pro.shushi.pamirs.eip.jdbc.service.EipJdbcDistributionSupport;
import pro.shushi.pamirs.eip.jdbc.util.DbConnectionUtils;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import javax.sql.DataSource;

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
        EipJdbcDistributionSupport distributionSupport = BeanDefinitionUtils.getBean(EipJdbcDistributionSupport.class);
        if (distributionSupport != null) {
            distributionSupport.refreshConnector(connector);
        }
        return dataSourceBeanName;
    }

    public static DataSource getDataSource(EipConnector connector) {
        String dsKey = String.valueOf(connector.getId());
        return EipDataSourceManager.get(dsKey);
    }

    public static boolean closeDataSource(EipConnector connector) {
        String dsKey = String.valueOf(connector.getId());
        boolean result;
        EipJdbcDistributionSupport distributionSupport = BeanDefinitionUtils.getBean(EipJdbcDistributionSupport.class);
        if (distributionSupport == null) {
            result = EipDataSourceManager.close(dsKey);
        } else {
            result = distributionSupport.refreshConnector(connector).isSuccess();
        }
        return result;
    }

    public static boolean restoreDataSource(EipConnector connector) {
        String dsKey = String.valueOf(connector.getId());
        boolean result;
        EipJdbcDistributionSupport distributionSupport = BeanDefinitionUtils.getBean(EipJdbcDistributionSupport.class);
        if (distributionSupport == null) {
            result = EipDataSourceManager.refresh(dsKey, () -> DbConnectionUtils.buildDataSource(connector));
        } else {
            result = distributionSupport.refreshConnector(connector).isSuccess();
        }
        return result;
    }

    public static String initEipPrepareStatement(EipConnector connector) {
        String connDBType = connector.getConnDBType();
        switch (connDBType) {
            case "SQLServer":
                EipPrepareStatementManager.register(connDBType, EipSqlPrepareStatementStrategy.DEFAULT_SEPARATOR, "[DEFAULT]");
                return EipPrepareStatementManager.generatorId(connDBType);
            default:
                return EipSqlPrepareStatementStrategy.NAME;
        }
    }
}
