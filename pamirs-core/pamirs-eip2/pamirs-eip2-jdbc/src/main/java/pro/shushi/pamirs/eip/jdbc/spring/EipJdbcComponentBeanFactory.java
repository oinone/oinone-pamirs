package pro.shushi.pamirs.eip.jdbc.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.eip.jdbc.service.url.*;

/**
 * EipJdbcComponentBeanFactory
 *
 * @author yakir on 2025/06/24 16:48.
 */
@Configuration
public class EipJdbcComponentBeanFactory {

    @Bean(initMethod = "init")
    public DefaultDMComponent eipDMComponent() {
        return new DefaultDMComponent();
    }

    @Bean(initMethod = "init")
    public DefaultKingbaseComponent eipKingbaseComponent() {
        return new DefaultKingbaseComponent();
    }

    @Bean(initMethod = "init")
    public DefaultMySQLComponent eipMySQLComponent() {
        return new DefaultMySQLComponent();
    }

    @Bean(initMethod = "init")
    public DefaultOracleComponent eipOracleComponent() {
        return new DefaultOracleComponent();
    }

    @Bean(initMethod = "init")
    public DefaultPostgreSQLComponent eipPostgreSQLComponent() {
        return new DefaultPostgreSQLComponent();
    }

    @Bean(initMethod = "init")
    public DefaultSqlServerComponent eipSqlServerComponent() {
        return new DefaultSqlServerComponent();
    }

}
