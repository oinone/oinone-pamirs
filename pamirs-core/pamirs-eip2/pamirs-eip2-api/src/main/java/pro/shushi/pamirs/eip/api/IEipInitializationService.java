package pro.shushi.pamirs.eip.api;

import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public interface IEipInitializationService<T extends IEipSingletonConfig> {

    default Class<T> getTClass() {
        Type[] types = getClass().getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                return (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
            }
        }
        return null;
    }

    List<IEipApi> initEip(T t);

    default void isInit(T t) {
        if (t.singletonModel() == null) {
            throw PamirsException.construct(EipExpEnumerate.EIP_API_NOT_INIT).errThrow();
        }
    }

    default String genHeaderKey(String keyName) {
        return IEipContext.HEADER_PARAMS_KEY + "." + keyName;
    }

    default String eipSystem() {
        return getTClass().getSimpleName();
    }

}
