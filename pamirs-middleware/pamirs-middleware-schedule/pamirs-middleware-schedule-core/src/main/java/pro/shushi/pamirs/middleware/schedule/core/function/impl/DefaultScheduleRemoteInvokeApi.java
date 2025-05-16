package pro.shushi.pamirs.middleware.schedule.core.function.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.middleware.schedule.core.function.ScheduleRemoteInvokeApi;
import pro.shushi.pamirs.middleware.schedule.core.function.model.FunctionDefinition;

/**
 * @author Adamancy Zhang at 15:06 on 2024-08-27
 */
@Order
@Component
@SPI.Service
public class DefaultScheduleRemoteInvokeApi implements ScheduleRemoteInvokeApi {

    @Override
    public Object invoke(FunctionDefinition<?> functionDefinition, Object[] args) {
        throw new UnsupportedOperationException();
    }

}
