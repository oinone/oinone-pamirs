package pro.shushi.pamirs.framework.connectors.data.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import pro.shushi.pamirs.framework.connectors.data.api.configure.PamirsFrameworkDataConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.persistence.PamirsPersistenceConfiguration;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.sql.Connection;

/**
 * 动态数据源
 * <p>
 * 2020/6/4 2:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    @SuppressWarnings("unused")
    public final static String beanName = "dataSource";

    private PamirsPersistenceConfiguration pamirsPersistenceConfiguration;

    private PamirsFrameworkDataConfiguration pamirsFrameworkDataConfiguration;

    public PamirsPersistenceConfiguration getPamirsPersistenceConfiguration() {
        return pamirsPersistenceConfiguration;
    }

    public void setPamirsPersistenceConfiguration(PamirsPersistenceConfiguration pamirsPersistenceConfiguration) {
        this.pamirsPersistenceConfiguration = pamirsPersistenceConfiguration;
    }

    public PamirsFrameworkDataConfiguration getPamirsFrameworkDataConfiguration() {
        return pamirsFrameworkDataConfiguration;
    }

    public void setPamirsFrameworkDataConfiguration(PamirsFrameworkDataConfiguration pamirsFrameworkDataConfiguration) {
        this.pamirsFrameworkDataConfiguration = pamirsFrameworkDataConfiguration;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceHolder.getLookupKeyByDsKey(PamirsSession.getDsKey());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Connection getConnection() {
//        return determineTargetDataSource().getConnection();
        return new ConnectionHolder(getPamirsPersistenceConfiguration(), getPamirsFrameworkDataConfiguration());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Connection getConnection(String username, String password) {
//        return determineTargetDataSource().getConnection(username, password);
        return new ConnectionHolder(username, password, getPamirsPersistenceConfiguration(), getPamirsFrameworkDataConfiguration());
    }

}
