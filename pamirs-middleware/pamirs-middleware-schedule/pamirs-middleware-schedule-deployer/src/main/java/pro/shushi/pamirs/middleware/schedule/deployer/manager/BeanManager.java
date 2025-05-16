package pro.shushi.pamirs.middleware.schedule.deployer.manager;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.common.manager.DataSourceManager;
import pro.shushi.pamirs.middleware.schedule.core.dao.sharding.ShardTableInterceptor;
import pro.shushi.pamirs.middleware.schedule.core.dialect.config.ScheduleDialectConfiguration;

import javax.sql.DataSource;

/**
 * @author Adamancy Zhang
 * @date 2021-01-27 11:46
 */
@Component
@DependsOn({DataSourceManager.BEAN_NAME})
public class BeanManager {

    public static final String SCHEDULE_SQL_SESSION_FACTORY_BEAN_NAME = "scheduleSqlSessionFactory";

    @Autowired
    private ScheduleDialectConfiguration scheduleDialectConfiguration;

    @Bean(name = SCHEDULE_SQL_SESSION_FACTORY_BEAN_NAME)
    public SqlSessionFactory scheduleSqlSessionFactory(@Qualifier(DataSourceManager.DATA_SOURCE_BEAN_NAME) DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        Configuration configuration = new Configuration();
        configuration.setUseGeneratedKeys(true);
        sqlSessionFactoryBean.setConfiguration(configuration);
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/ScheduleItemMapper.xml"));
        Interceptor[] plugins = new Interceptor[]{new ShardTableInterceptor(scheduleDialectConfiguration.getDialectVersion())};
        sqlSessionFactoryBean.setPlugins(plugins);
        return sqlSessionFactoryBean.getObject();
    }
}
