package pro.shushi.pamirs.eip.api.pamirs;

import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipContextSupplier;

/**
 * 实现平台Function机制
 */
public class DefaultContextSupplierFunction<T> extends AbstractExecuteFunction implements IEipContextSupplier<T> {

    public DefaultContextSupplierFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IEipContext<T> get(IEipApi eipApi, T executorContext, T interfaceContext) {
        return (IEipContext<T>) ignoreHookCall(eipApi, executorContext, interfaceContext);
    }
}
