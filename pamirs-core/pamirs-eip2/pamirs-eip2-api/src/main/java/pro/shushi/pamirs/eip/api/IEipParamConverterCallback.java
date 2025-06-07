package pro.shushi.pamirs.eip.api;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@FunctionalInterface
public interface IEipParamConverterCallback<T> {

    Object callback(IEipContext<T> context, IEipConvertParam<T> convertParam, List<AtomicInteger> inParamCounterList, Object object);
}
