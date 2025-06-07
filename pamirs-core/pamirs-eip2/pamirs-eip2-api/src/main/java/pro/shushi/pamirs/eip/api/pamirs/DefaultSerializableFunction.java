package pro.shushi.pamirs.eip.api.pamirs;

import pro.shushi.pamirs.eip.api.IEipSerializable;

/**
 * 实现平台Function机制
 */
public class DefaultSerializableFunction<T> extends AbstractExecuteFunction implements IEipSerializable<T> {

    public DefaultSerializableFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T serializable(Object inObject) {
        return (T) ignoreHookCall(inObject);
    }
}
