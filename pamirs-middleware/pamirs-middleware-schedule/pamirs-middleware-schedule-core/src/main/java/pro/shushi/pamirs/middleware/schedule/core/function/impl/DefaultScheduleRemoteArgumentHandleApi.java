package pro.shushi.pamirs.middleware.schedule.core.function.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.middleware.schedule.core.function.ScheduleRemoteArgumentHandleApi;
import pro.shushi.pamirs.middleware.schedule.core.function.model.FunctionDefinition;

/**
 * 默认Schedule远程调用参数处理实现
 *
 * @author Adamancy Zhang at 14:52 on 2024-08-27
 */
@Order
@Component
@SPI.Service
public class DefaultScheduleRemoteArgumentHandleApi implements ScheduleRemoteArgumentHandleApi {

    @Override
    public Object[] requestHandle(FunctionDefinition<?> functionDefinition, Object[] args) {
        return args;
    }

    @Override
    public Object responseHandle(FunctionDefinition<?> functionDefinition, Object result) {
        return result;
    }

}
