package pro.shushi.pamirs.eip.api;

import java.util.List;

public interface IEipParamConverterProcessor<T> {

    /**
     * 自定义转换器
     */
    IEipConverter<T> getConverter();

    /**
     * 参数转换器
     */
    IEipParamConverter<T> getParamConverter();

    /**
     * 参数转换回调
     */
    IEipParamConverterCallback<T> getParamConverterCallback();

    /**
     * 转换参数列表
     */
    List<IEipConvertParam<T>> getConvertParamList();
}
