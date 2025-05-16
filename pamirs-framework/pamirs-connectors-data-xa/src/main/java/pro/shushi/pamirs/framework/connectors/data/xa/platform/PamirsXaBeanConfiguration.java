package pro.shushi.pamirs.framework.connectors.data.xa.platform;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Role;
import pro.shushi.pamirs.framework.connectors.data.datasource.DataSourceAutoRefreshManager;
import pro.shushi.pamirs.framework.connectors.data.xa.configure.PamirsXaConfiguration;
import pro.shushi.pamirs.framework.connectors.data.xa.constants.SystemBeanConstants;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 * pamirs分布式事务bean配置
 *
 * @author deng
 */
@SuppressWarnings("unused")
@Configuration
@DependsOn(DataSourceAutoRefreshManager.beanName)
public class PamirsXaBeanConfiguration {

    @RefreshScope
    @Bean(initMethod = "init", destroyMethod = "close")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public UserTransactionManager userTransactionManager() {
        UserTransactionManager transactionManager = new UserTransactionManager();
        transactionManager.setForceShutdown(getPamirsXaConfiguration().isForceShutdown());
        return transactionManager;
    }

    @RefreshScope
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public UserTransaction userTransaction() throws SystemException {
        UserTransaction userTransaction = new UserTransactionImp();
        userTransaction.setTransactionTimeout(getPamirsXaConfiguration().getTimeout());
        return userTransaction;
    }

    private PamirsXaConfiguration getPamirsXaConfiguration() {
        return BeanDefinitionUtils.getBean(SystemBeanConstants.PAMIRS_XA_CONFIGURATION, PamirsXaConfiguration.class);
    }

}
