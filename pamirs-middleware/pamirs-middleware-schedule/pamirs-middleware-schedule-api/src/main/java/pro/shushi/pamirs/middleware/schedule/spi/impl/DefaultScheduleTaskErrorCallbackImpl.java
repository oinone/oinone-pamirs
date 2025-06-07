package pro.shushi.pamirs.middleware.schedule.spi.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.spi.ScheduleTaskErrorCallbackApi;

/**
 * @author Adamancy Zhang at 17:38 on 2021-09-12
 */
@SPI.Service
@Component
@Order(Integer.MAX_VALUE) //默认优先级最低，业务配置需要配置成为优先级高
public class DefaultScheduleTaskErrorCallbackImpl implements ScheduleTaskErrorCallbackApi {

    @Override
    public void handleWhenExecuteError(ScheduleItem task) {
        // do nothing.
    }
}
