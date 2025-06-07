package pro.shushi.pamirs.trigger.tbschedule.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import pro.shushi.pamirs.middleware.schedule.core.dao.sharding.ShardTableInterceptor;
import pro.shushi.pamirs.middleware.schedule.core.dialect.config.ScheduleDialectConfiguration;

/**
 * @author Adamancy Zhang
 * @date 2020-11-16 18:12
 */
@Component
public class TBScheduleBeans {

    @Autowired
    private ScheduleDialectConfiguration scheduleDialectConfiguration;

    @Bean
    @Order(999999)
    public ShardTableInterceptor tbScheduleShardTableInterceptor() {
        return new ShardTableInterceptor(scheduleDialectConfiguration.getDialectVersion());
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionTemplate scheduleTransactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
