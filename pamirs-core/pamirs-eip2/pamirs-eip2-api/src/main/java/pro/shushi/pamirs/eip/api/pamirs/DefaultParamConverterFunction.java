package pro.shushi.pamirs.eip.api.pamirs;

import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConvertParam;
import pro.shushi.pamirs.eip.api.IEipParamConverter;
import pro.shushi.pamirs.eip.api.IEipParamConverterCallback;

import java.util.List;

/**
 * 实现平台Function机制
 */
public class DefaultParamConverterFunction<T> extends AbstractExecuteFunction implements IEipParamConverter<T> {

    public DefaultParamConverterFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @Override
    public void convert(IEipContext<T> context, List<IEipConvertParam<T>> convertParamList, IEipParamConverterCallback<T> callback) {
        ignoreHookCall(context, convertParamList, callback);
    }
}
