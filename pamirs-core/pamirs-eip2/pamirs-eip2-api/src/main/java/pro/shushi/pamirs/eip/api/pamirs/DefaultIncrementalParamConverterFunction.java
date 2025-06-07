package pro.shushi.pamirs.eip.api.pamirs;

import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipIncrementalParam;
import pro.shushi.pamirs.eip.api.IEipIncrementalParamConverter;
import pro.shushi.pamirs.eip.api.IEipIncrementalParamConverterCallback;

import java.util.List;

public class DefaultIncrementalParamConverterFunction<T> extends AbstractExecuteFunction implements IEipIncrementalParamConverter<T> {

    public DefaultIncrementalParamConverterFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @Override
    public void convert(IEipContext<T> context, List<IEipIncrementalParam> convertParamList, IEipIncrementalParamConverterCallback<T> callback) {
        ignoreHookCall(context, convertParamList, callback);
    }
}
