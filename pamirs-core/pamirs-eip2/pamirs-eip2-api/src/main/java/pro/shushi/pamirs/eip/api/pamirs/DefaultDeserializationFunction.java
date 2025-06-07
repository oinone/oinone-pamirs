package pro.shushi.pamirs.eip.api.pamirs;

import pro.shushi.pamirs.eip.api.IEipDeserialization;

/**
 * 实现平台Function机制
 */
public class DefaultDeserializationFunction<T> extends AbstractExecuteFunction implements IEipDeserialization<T> {

    public DefaultDeserializationFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @Override
    public Object deserialization(T outObject) {
        return ignoreHookCall(outObject);
    }
}
