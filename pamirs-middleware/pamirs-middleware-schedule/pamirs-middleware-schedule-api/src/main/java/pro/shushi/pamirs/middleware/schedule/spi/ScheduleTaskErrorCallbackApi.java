package pro.shushi.pamirs.middleware.schedule.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

/**
 * 定时任务异常回调API
 *
 * @author Adamancy Zhang at 17:35 on 2021-09-12
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ScheduleTaskErrorCallbackApi {

    /**
     * 任务执行失败后的处理
     *
     * @param task {@link ScheduleItem}
     */
    void handleWhenExecuteError(ScheduleItem task);
}
