package pro.shushi.pamirs.middleware.schedule.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

@SPI
public interface ScheduleRemoteTaskFunctionApi {

    ScheduleItem handleBeforeExecuteFun(ScheduleItem task);

}