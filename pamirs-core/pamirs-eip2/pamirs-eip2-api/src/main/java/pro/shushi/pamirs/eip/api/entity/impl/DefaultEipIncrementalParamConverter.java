package pro.shushi.pamirs.eip.api.entity.impl;

import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipIncrementalParam;
import pro.shushi.pamirs.eip.api.IEipIncrementalParamConverter;
import pro.shushi.pamirs.eip.api.IEipIncrementalParamConverterCallback;
import pro.shushi.pamirs.eip.api.util.EipParamConverterHelper;

import java.util.List;

public class DefaultEipIncrementalParamConverter<T> implements IEipIncrementalParamConverter<T> {

    @Override
    public void convert(IEipContext<T> context, List<IEipIncrementalParam> convertParamList, IEipIncrementalParamConverterCallback<T> callback) {
        for (IEipIncrementalParam incrementalParam : convertParamList) {
            Object currentValue = incrementalParam.getCurrentValue();
            if (currentValue == null) {
                currentValue = incrementalParam.getInitializationValue();
            }
            Object object = EipParamConverterHelper.getContextValue(incrementalParam.getOriginContextType(), context, incrementalParam.getInParam());
            if (currentValue == null || object == null) {
                continue;
            }
            if (callback != null) {
                object = callback.callback(context, incrementalParam, currentValue, object);
            }
            EipParamConverterHelper.putContextValue(incrementalParam.getTargetContextType(), context, incrementalParam.getOutParam(), object);
            incrementalParam.setCurrentValue(object);
        }
    }
}
