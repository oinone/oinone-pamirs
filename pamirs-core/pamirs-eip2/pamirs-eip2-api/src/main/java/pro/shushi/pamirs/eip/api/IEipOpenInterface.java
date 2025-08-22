package pro.shushi.pamirs.eip.api;

import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;

public interface IEipOpenInterface<T> extends IEipApi, IEipAfterProperty<IEipOpenInterface<T>> {

    @Override
    default InterfaceTypeEnum getType() {
        return InterfaceTypeEnum.OPEN;
    }

    /**
     * 交换处理器
     */
    IEipProcessor<IEipOpenInterface<T>> getProcessor();

    /**
     * 上下文提供者
     */
    IEipContextSupplier<T> getContextSupplier();

    /**
     * 请求预处理处理器
     */
    IEipDecryptProcessor getRequestDecryptProcessor();

    /**
     * 请求参数处理器
     */
    IEipOpenParamProcessor<T> getRequestParamProcessor();

    /**
     * 响应参数处理器
     */
    IEipOpenParamProcessor<T> getResponseParamProcessor();

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
     * 其他自定义转换器（结果处理器）
     */
    IEipConverter<T> getConverter();

    /**
     * 获取最终结果键值
     */
    String getFinalResultKey();

    /**
     * 输入输出转换器
     */
    IEipInOutConverter getInOutConverter();

    /**
     * 响应预处理处理器
     */
    IEipEncryptionProcessor getResponseEncryptionProcessor();
}
