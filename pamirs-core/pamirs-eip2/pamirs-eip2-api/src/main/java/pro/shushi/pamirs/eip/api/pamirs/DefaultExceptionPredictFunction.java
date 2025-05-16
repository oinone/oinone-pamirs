package pro.shushi.pamirs.eip.api.pamirs;

import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipExceptionPredict;

/**
 * 实现平台Function机制
 */
public class DefaultExceptionPredictFunction<T> extends AbstractExecuteFunction implements IEipExceptionPredict<T> {

    public DefaultExceptionPredictFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @Override
    public boolean test(IEipContext<T> context) {
        return (Boolean) ignoreHookCall(context);
    }
}
