package pro.shushi.pamirs.eip.api;

public interface IEipParamProcessor<T> extends IEipParamConverterProcessor<T>, IEipAfterProperty<IEipParamProcessor<T>> {

    /**
     * 交换处理器
     */
    IEipProcessor<IEipIntegrationInterface<T>> getProcessor();

    /**
     * 认证处理器
     */
    IEipAuthenticationProcessor<T> getAuthenticationProcessor();

    /**
     * 序列化
     */
    IEipSerializable<T> getSerializable();

    /**
     * 反序列化
     */
    IEipDeserialization<T> getDeserialization();

    /**
     * 获取最终结果键值
     */
    String getFinalResultKey();

    /**
     * 输入输出转换器
     */
    IEipInOutConverter getInOutConverter();
}
