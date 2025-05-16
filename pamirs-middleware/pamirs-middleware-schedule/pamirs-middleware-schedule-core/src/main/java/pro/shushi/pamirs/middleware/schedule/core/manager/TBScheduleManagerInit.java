package pro.shushi.pamirs.middleware.schedule.core.manager;

import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.schedule.core.condition.ScheduleSwitchCondition;

/**
 * @author Adamancy Zhang
 * @date 2020-11-16 18:08
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@DependsOn(TBScheduleManagerBean.TB_SCHEDULE_MANAGER_FACTORY_BEAN_NAME)
@Conditional(ScheduleSwitchCondition.class)
public class TBScheduleManagerInit implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private ApplicationContext context;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            context.getBean(TBScheduleManagerFactory.class).init();
        } catch (Exception e) {
            throw new RuntimeException("tbschedule init error", e);
        }
    }
}
