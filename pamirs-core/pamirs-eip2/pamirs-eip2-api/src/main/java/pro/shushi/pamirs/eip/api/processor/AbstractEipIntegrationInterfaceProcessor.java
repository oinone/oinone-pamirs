package pro.shushi.pamirs.eip.api.processor;

import com.alibaba.fastjson.JSON;
import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.core.common.entry.InitializationBody;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"rawtypes"})
public abstract class AbstractEipIntegrationInterfaceProcessor<T> extends AbstractProcessor<IEipIntegrationInterface<T>> implements IEipProcessor<IEipIntegrationInterface<T>> {

    public AbstractEipIntegrationInterfaceProcessor(IEipIntegrationInterface<T> integrationInterface) {
        super(integrationInterface);
    }

    protected IEipContext<T> refreshExecutorContext(ExtendedExchange exchange, IEipContext<T> context, T interfaceContext) {
        IEipContext<T> executorContext = getApi().getContextSupplier().get(getApi(), context.getExecutorContext(), interfaceContext);
        EipInterfaceContext.setExecutorContext(exchange, executorContext);
        return executorContext;
    }

    @SuppressWarnings("unchecked")
    protected InitializationBody<String, Object> idempotent(IEipContext<T> context, ExtendedExchange exchange, Object result, boolean isSerializable) {
        Type type = (Type) context.getExecutorContextValue(IEipContext.RESPONSE_IDEMPOTENT_PROCESSOR_TYPE_PREFIX + context.getApi().getInterfaceName());
        IEipIdempotentProcessor<T> idempotentProcessor = (IEipIdempotentProcessor<T>) context.getExecutorContextValue(IEipContext.RESPONSE_IDEMPOTENT_PROCESSOR_PREFIX + context.getApi().getInterfaceName());
        boolean isFilter = Boolean.FALSE;
        if (idempotentProcessor != null) {
            if (type != null && !isSerializable) {
                result = JSON.parseObject(JSON.toJSONString(result), type);
                isSerializable = Boolean.TRUE;
            }
            isFilter = idempotentProcessor.matches(context, exchange, result);
        }
        InitializationBody<String, Object> initializationBody = new InitializationBody<>(null, isFilter ? null : result);
        if (isSerializable) {
            initializationBody.processed();
        }
        return initializationBody;
    }

    @SuppressWarnings("unchecked")
    protected InitializationBody<String, Object> idempotentEach(IEipContext<T> context, ExtendedExchange exchange, Object result, boolean isSerializable) {
        Class<?> type = (Class<?>) context.getExecutorContextValue(IEipContext.RESPONSE_IDEMPOTENT_PROCESSOR_EACH_TYPE_PREFIX + context.getApi().getInterfaceName());
        IEipIdempotentProcessor<T> idempotentProcessor = (IEipIdempotentProcessor<T>) context.getExecutorContextValue(IEipContext.RESPONSE_IDEMPOTENT_PROCESSOR_EACH_PREFIX + context.getApi().getInterfaceName());
        boolean isFilter = Boolean.FALSE;
        List<Object> finalResultList = new ArrayList<>();
        if (idempotentProcessor != null && result instanceof List) {
            isFilter = Boolean.TRUE;
            if (type != null && !isSerializable) {
                result = JSON.parseArray(JSON.toJSONString(result), type);
                isSerializable = Boolean.TRUE;
            }
            List resultList = (List) result;
            for (Object object : resultList) {
                if (!idempotentProcessor.matches(context, exchange, object)) {
                    finalResultList.add(object);
                }
            }
        }
        InitializationBody<String, Object> initializationBody = new InitializationBody<>(null, isFilter ? finalResultList : result);
        if (isSerializable) {
            initializationBody.processed();
        }
        return initializationBody;
    }

    @SuppressWarnings("unchecked")
    protected void callback(IEipContext<T> context, ExtendedExchange exchange, Object result, boolean isSerializable) {
        Type type = (Type) context.getExecutorContextValue(IEipContext.RESPONSE_CALLBACK_TYPE_PREFIX + context.getApi().getInterfaceName());
        IEipProcessCallback<T> callback = (IEipProcessCallback<T>) context.getExecutorContextValue(IEipContext.RESPONSE_CALLBACK_PREFIX + context.getApi().getInterfaceName());
        if (callback != null) {
            if (type != null && !isSerializable) {
                result = JSON.parseObject(JSON.toJSONString(result), type);
            }
            callback.callback(context, exchange, result);
        }
    }

    @SuppressWarnings("unchecked")
    protected void callbackEach(IEipContext<T> context, ExtendedExchange exchange, Object result, boolean isSerializable) {
        Class<?> type = (Class<?>) context.getExecutorContextValue(IEipContext.RESPONSE_CALLBACK_EACH_TYPE_PREFIX + context.getApi().getInterfaceName());
        IEipProcessCallback<T> callback = (IEipProcessCallback<T>) context.getExecutorContextValue(IEipContext.RESPONSE_CALLBACK_EACH_PREFIX + context.getApi().getInterfaceName());
        if (callback != null && result instanceof List) {
            if (type != null && !isSerializable) {
                result = JSON.parseArray(JSON.toJSONString(result), type);
            }
            List resultList = (List) result;
            for (Object object : resultList) {
                callback.callback(context, exchange, object);
            }
        }
    }
}
