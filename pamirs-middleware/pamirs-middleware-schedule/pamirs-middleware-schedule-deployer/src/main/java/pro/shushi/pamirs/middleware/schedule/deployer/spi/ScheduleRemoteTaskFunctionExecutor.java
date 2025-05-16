package pro.shushi.pamirs.middleware.schedule.deployer.spi;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.spi.ScheduleRemoteTaskFunctionApi;

@Order
@SPI.Service
public class ScheduleRemoteTaskFunctionExecutor implements ScheduleRemoteTaskFunctionApi {

    @Override
    public ScheduleItem handleBeforeExecuteFun(ScheduleItem task) {
        return task;
    }
}
