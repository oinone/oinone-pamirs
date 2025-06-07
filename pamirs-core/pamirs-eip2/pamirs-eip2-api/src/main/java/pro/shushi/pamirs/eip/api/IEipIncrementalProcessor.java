package pro.shushi.pamirs.eip.api;

import java.util.List;

public interface IEipIncrementalProcessor<T> {

    /**
     * 自定义转换器
     */
    IEipConverter<T> getConverter();

    /**
     * 增量参数转换器
     */
    IEipIncrementalParamConverter<T> getIncrementalParamConverter();

    /**
     * 增量参数转换回调
     */
    IEipIncrementalParamConverterCallback<T> getIncrementalParamConverterCallback();

    /**
     * 增量参数列表
     */
    List<IEipIncrementalParam> getIncrementalParamList(String tags);

    /**
     * 提交并保存增量条件
     */
    void commit(IEipContext<T> context);
}
