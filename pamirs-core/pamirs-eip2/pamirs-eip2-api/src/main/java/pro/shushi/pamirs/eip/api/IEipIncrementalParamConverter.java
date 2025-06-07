package pro.shushi.pamirs.eip.api;

import java.util.List;

public interface IEipIncrementalParamConverter<T> {

    void convert(IEipContext<T> context, List<IEipIncrementalParam> convertParamList, IEipIncrementalParamConverterCallback<T> callback);

    default void convert(IEipContext<T> context, List<IEipIncrementalParam> convertParamList) {
        convert(context, convertParamList, null);
    }
}
