package pro.shushi.pamirs.trigger.tbschedule.spi;

import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.faas.spi.api.remote.utils.RegistryUtils;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.remote.RemoteRegistry;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.middleware.schedule.core.function.ScheduleRemoteRegistryApi;
import pro.shushi.pamirs.middleware.schedule.core.function.model.FunctionDefinition;

/**
 * 桥接
 *
 * @author cpc
 */
@Component
@Order(0)
@SPI.Service
public class BridgeScheduleRemoteRegistryApi implements ScheduleRemoteRegistryApi {

    @Override
    public GenericService registryConsumer(FunctionDefinition<?> functionDefinition) {
        Function function = PamirsSession.getContext().getFunction(functionDefinition.getInterfaceName(), functionDefinition.getMethodName());
        return CommonApiFactory.getApi(RemoteRegistry.class).registryConsumer(function);
    }

    @Override
    public String getGenericServiceMethodName(FunctionDefinition<?> functionDefinition) {
        Function function = PamirsSession.getContext().getFunction(functionDefinition.getInterfaceName(), functionDefinition.getMethodName());
        return RegistryUtils.getGenericServiceMethodName(function);
    }

    @Override
    public String[] getParameterTypes(FunctionDefinition<?> functionDefinition) {
        return functionDefinition.getParameterTypes();
    }
}
