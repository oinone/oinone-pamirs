package pro.shushi.pamirs.trigger.tbschedule.spi;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.dto.fun.Arg;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.middleware.schedule.core.function.ScheduleRemoteInvokeApi;
import pro.shushi.pamirs.middleware.schedule.core.function.model.FunctionDefinition;

import java.util.Optional;

/**
 * Schedule远程调用实现
 *
 * @author Adamancy Zhang at 15:08 on 2024-08-27
 */
@Order(0)
@Component
@SPI.Service
public class BridgeScheduleRemoteInvokeApi implements ScheduleRemoteInvokeApi {

    @Override
    public Object invoke(FunctionDefinition<?> functionDefinition, Object[] args) {
        Function function = PamirsSession.getContext().getFunction(functionDefinition.getInterfaceName(), functionDefinition.getMethodName());
        Arg firstArg = Optional.ofNullable(function.getArguments())
                .filter(CollectionUtils::isNotEmpty)
                .map(v -> v.get(0))
                .orElse(null);
        if (firstArg == null) {
            return Fun.run(function);
        }
        return Fun.run(function, args);
    }

}
