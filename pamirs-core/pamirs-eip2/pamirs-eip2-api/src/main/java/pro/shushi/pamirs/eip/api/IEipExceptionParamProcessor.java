package pro.shushi.pamirs.eip.api;

public interface IEipExceptionParamProcessor<T> extends IEipParamConverterProcessor<T>, IEipAfterProperty<IEipExceptionParamProcessor<T>> {

    /**
     * 交换处理器
     */
    IEipProcessor<IEipIntegrationInterface<T>> getProcessor();

    /**
     * 异常判定
     */
    IEipExceptionPredict<T> getExceptionPredict();
}
