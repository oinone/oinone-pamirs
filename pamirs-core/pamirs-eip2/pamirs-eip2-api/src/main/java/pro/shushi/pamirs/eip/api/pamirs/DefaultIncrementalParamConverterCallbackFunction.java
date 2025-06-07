package pro.shushi.pamirs.eip.api.pamirs;

import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipIncrementalParam;
import pro.shushi.pamirs.eip.api.IEipIncrementalParamConverterCallback;

public class DefaultIncrementalParamConverterCallbackFunction<T> extends AbstractExecuteFunction implements IEipIncrementalParamConverterCallback<T> {

    public DefaultIncrementalParamConverterCallbackFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @Override
    public Object callback(IEipContext<T> context, IEipIncrementalParam convertParam, Object currentValue, Object object) {
        return ignoreHookCall(context, convertParam, currentValue, object);
    }
}
