package pro.shushi.pamirs.eip.api;

import java.util.List;

@FunctionalInterface
public interface IEipParamConverter<T> {

    void convert(IEipContext<T> context, List<IEipConvertParam<T>> convertParamList, IEipParamConverterCallback<T> callback);

    default void convert(IEipContext<T> context, List<IEipConvertParam<T>> convertParamList) {
        convert(context, convertParamList, null);
    }
}
