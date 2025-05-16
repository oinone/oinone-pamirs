package pro.shushi.pamirs.eip.api.entity;

import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.eip.api.IEipContext;

public abstract class AbstractEipContext<T> implements IEipContext<T> {

    private final IEipApi eipApi;

    private final T executorContext;

    private final T interfaceContext;

    public AbstractEipContext(IEipApi eipApi, T executorContext, T interfaceContext) {
        this.eipApi = eipApi;
        this.executorContext = executorContext;
        this.interfaceContext = interfaceContext;
    }

    @Override
    public IEipApi getApi() {
        return eipApi;
    }

    @Override
    public T getExecutorContext() {
        return executorContext;
    }

    @Override
    public T getInterfaceContext() {
        return interfaceContext;
    }
}
