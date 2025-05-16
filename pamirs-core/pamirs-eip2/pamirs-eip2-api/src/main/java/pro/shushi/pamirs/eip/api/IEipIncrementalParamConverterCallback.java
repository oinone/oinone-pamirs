package pro.shushi.pamirs.eip.api;

@FunctionalInterface
public interface IEipIncrementalParamConverterCallback<T> {

    Object callback(IEipContext<T> context, IEipIncrementalParam convertParam, Object currentValue, Object object);
}
