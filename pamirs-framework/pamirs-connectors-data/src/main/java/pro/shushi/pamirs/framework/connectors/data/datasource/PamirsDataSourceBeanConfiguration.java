package pro.shushi.pamirs.framework.connectors.data.datasource;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import pro.shushi.pamirs.framework.connectors.data.api.configure.PamirsFrameworkDataConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.persistence.PamirsPersistenceConfiguration;
import pro.shushi.pamirs.framework.connectors.data.constant.SystemBeanConstants;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import jakarta.annotation.Resource;
import javax.sql.DataSource;

/**
 * pamirs数据源bean配置
 *
 * @author deng
 */
@SuppressWarnings("unused")
//@Configuration(PamirsDataSourceBeanConfiguration.beanName)
@Slf4j
@DependsOn({DynamicDataSource.beanName, DataSourceAutoRefreshManager.beanName})
public class PamirsDataSourceBeanConfiguration {

    public static final String beanName = "pamirsDataSourceBeanConfiguration";

    @Resource
    private DataSourceHolder dataSourceHolder;

    @RefreshScope
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DataSource dataSource() {
        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setPamirsFrameworkDataConfiguration(getPamirsFrameworkDataConfiguration());
        dataSource.setPamirsPersistenceConfiguration(getPamirsPersistenceConfiguration());
        dataSource.setTargetDataSources(dataSourceHolder.getDataSourceMap());
        String defaultDataSourceName = getPamirsFrameworkDataConfiguration().getDefaultDsKey();
        dataSource.setDefaultTargetDataSource(dataSourceHolder.get(defaultDataSourceName));
        return dataSource;
    }

    @RefreshScope
    @Order
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    private PamirsFrameworkDataConfiguration getPamirsFrameworkDataConfiguration() {
        return BeanDefinitionUtils.getBean(SystemBeanConstants.PAMIRS_FRAMEWORK_DATA_CONFIGURATION, PamirsFrameworkDataConfiguration.class);
    }

    private PamirsPersistenceConfiguration getPamirsPersistenceConfiguration() {
        return BeanDefinitionUtils.getBean(SystemBeanConstants.PAMIRS_PERSISTENCE_CONFIGURATION, PamirsPersistenceConfiguration.class);
    }

}
