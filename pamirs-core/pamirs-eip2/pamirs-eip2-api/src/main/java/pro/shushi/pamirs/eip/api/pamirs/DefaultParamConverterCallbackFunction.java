package pro.shushi.pamirs.eip.api.pamirs;

import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConvertParam;
import pro.shushi.pamirs.eip.api.IEipParamConverterCallback;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 实现平台Function机制
 */
public class DefaultParamConverterCallbackFunction<T> extends AbstractExecuteFunction implements IEipParamConverterCallback<T> {

    public DefaultParamConverterCallbackFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @Override
    public Object callback(IEipContext<T> context, IEipConvertParam<T> convertParam, List<AtomicInteger> inParamCounterList, Object object) {
        return ignoreHookCall(context, convertParam, inParamCounterList, object);
    }
}
