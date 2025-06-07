package pro.shushi.pamirs.middleware.schedule.deployer.spi;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.deployer.session.ScheduleRpcSession;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.spi.ScheduleTaskActionExecuteAroundApi;

import java.util.function.Supplier;

/**
 * @author Adamancy Zhang on 2021-04-28 18:37
 */
@Order
@SPI.Service
public class ScheduleTaskActionExecuteSession implements ScheduleTaskActionExecuteAroundApi {

    @Override
    public Result<Void> around(ScheduleItem task, String ownSign, Supplier<Result<Void>> supplier) {
        try {
            ScheduleRpcSession.clear();
            ScheduleRpcSession.setTenant(task.getTenant());
            ScheduleRpcSession.setEnv(task.getEnv());
            ScheduleRpcSession.setUserId(task.getUserId());
            ScheduleRpcSession.setUsername(task.getUsername());
            return supplier.get();
        } finally {
            ScheduleRpcSession.clear();
        }
    }
}
