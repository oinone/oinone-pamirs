package pro.shushi.pamirs.eip.api;

import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;

public interface IEipIntegrationInterface<T> extends IEipApi, IEipAfterProperty<IEipIntegrationInterface<T>> {

    @Override
    default InterfaceTypeEnum getType() {
        return InterfaceTypeEnum.INTEGRATION;
    }

    /**
     * 上下文提供者
     */
    IEipContextSupplier<T> getContextSupplier();

    /**
     * 请求参数处理器
     */
    IEipParamProcessor<T> getRequestParamProcessor();

    /**
     * 响应参数处理器
     */
    IEipParamProcessor<T> getResponseParamProcessor();

    /**
     * 异常参数处理器
     */
    IEipExceptionParamProcessor<T> getExceptionParamProcessor();

    /**
     * 分页器
     */
    IEipPaging<T> getPaging();

    /**
     * 增量处理器
     */
    IEipIncrementalProcessor<T> getIncrementalProcessor();

    /**
     * 是否是动态集成接口
     *
     * @return 是否是动态集成接口
     */
    Boolean getIsDynamic();

    /**
     * 动态协议缓存的最大值
     *
     * @return 动态协议缓存的最大值
     */
    Integer getDynamicProtocolCacheSize();
}
