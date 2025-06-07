package pro.shushi.pamirs.middleware.schedule.core.function.impl;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.middleware.schedule.core.function.FunctionReturnResultConverter;
import pro.shushi.pamirs.middleware.schedule.core.function.FunctionService;
import pro.shushi.pamirs.middleware.schedule.core.function.ScheduleRemoteInvokeApi;
import pro.shushi.pamirs.middleware.schedule.core.function.model.FunctionDefinition;

/**
 * @author Adamancy Zhang
 * @date 2020-10-22 09:52
 */
@Service(RemoteFunctionServiceImpl.BEAN_NAME)
public class RemoteFunctionServiceImpl implements FunctionService {

    public static final String BEAN_NAME = "remoteFunctionService";

    private static final HoldKeeper<ScheduleRemoteInvokeApi> remoteInvokeApiHolder = new HoldKeeper<>();

    private static ScheduleRemoteInvokeApi getRemoteInvokeApi() {
        return remoteInvokeApiHolder.supply(() -> Spider.getDefaultExtension(ScheduleRemoteInvokeApi.class));
    }

    @Override
    public <T> T execute(FunctionDefinition<T> functionDefinition, Object... args) {
        FunctionReturnResultConverter<T> converter = functionDefinition.getReturnResultConverter();
        Object result = execute0(functionDefinition, args);
        if (converter == null) {
            //noinspection unchecked
            return (T) result;
        }
        return converter.convert(result);
    }

    @Override
    public void executeWithoutResult(FunctionDefinition<Void> functionDefinition, Object... args) {
        execute0(functionDefinition, args);
    }

    private Object execute0(FunctionDefinition<?> functionDefinition, Object... args) {
        return getRemoteInvokeApi().invoke(functionDefinition, args);
//        ScheduleRemoteRegistryApi remoteRegistryApi = getRemoteRegistryApi();
//        ScheduleRemoteArgumentHandleApi remoteArgumentHandleApi = getRemoteArgumentHandleApi();
//        GenericService genericService = remoteRegistryApi.registryConsumer(functionDefinition);
//        Object res = genericService.$invoke(remoteRegistryApi.getGenericServiceMethodName(functionDefinition), remoteRegistryApi.getParameterTypes(functionDefinition), args);
//        return remoteArgumentHandleApi.responseHandle(functionDefinition, res);
    }

}
