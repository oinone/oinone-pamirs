package pro.shushi.pamirs.middleware.common.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import pro.shushi.pamirs.middleware.common.CommonConstants;

import javax.sql.DataSource;

/**
 * @author Adamancy Zhang
 * @date 2020-11-11 11:15
 */
@Component(DataSourceManager.BEAN_NAME)
public class DataSourceManager {

    public static final String BEAN_NAME = "middlewareDataSourceManager";

    public static final String HIKARI_CONFIG_BEAN_NAME = "middlewareHikariConfig";

    public static final String DATA_SOURCE_BEAN_NAME = "middlewareDataSource";

    public static final String TRANSACTION_TEMPLATE_BEAN_NAME = "middlewareTransactionTemplate";

    public static final String TRANSACTION_MANAGER_BEAN_NAME = "middlewareDataSourceTransactionManager";

    @Bean(name = HIKARI_CONFIG_BEAN_NAME)
    @ConfigurationProperties(prefix = CommonConstants.DATASOURCE_PREFIX)
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = DATA_SOURCE_BEAN_NAME)
    public DataSource dataSource(@Autowired @Qualifier(HIKARI_CONFIG_BEAN_NAME) HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = TRANSACTION_TEMPLATE_BEAN_NAME)
    public PlatformTransactionManager transactionManager(@Qualifier(DATA_SOURCE_BEAN_NAME) DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = TRANSACTION_MANAGER_BEAN_NAME)
    public TransactionTemplate transactionTemplate(@Qualifier(TRANSACTION_TEMPLATE_BEAN_NAME) PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
