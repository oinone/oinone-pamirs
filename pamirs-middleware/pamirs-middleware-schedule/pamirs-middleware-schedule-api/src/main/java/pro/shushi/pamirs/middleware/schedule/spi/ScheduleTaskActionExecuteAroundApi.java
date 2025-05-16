package pro.shushi.pamirs.middleware.schedule.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

import java.util.function.Supplier;

/**
 * 执行环绕扩展点
 *
 * @author Adamancy Zhang on 2021-04-27 16:31
 */
@SPI
public interface ScheduleTaskActionExecuteAroundApi {

    /**
     * 执行环绕扩展点
     *
     * @param task     任务
     * @param ownSign  所有者标记
     * @param supplier 切点
     * @return 执行结果
     */
    Result<Void> around(ScheduleItem task, String ownSign, Supplier<Result<Void>> supplier);
}
