package pro.shushi.pamirs.eip.api.service;

import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;

public interface EipService {

    /**
     * 注册接口
     *
     * @param eipApi 接口
     */
    void registerApi(IEipApi eipApi);

    /**
     * 注销接口
     *
     * @param eipApi 接口
     */
    void cancellationApi(IEipApi eipApi);

    /**
     * 注册集成接口
     */
    void registerInterface(EipIntegrationInterface eipInterface);

    /**
     * 注销集成接口
     */
    void cancellationInterface(EipIntegrationInterface eipInterface);

    /**
     * 注册路由定义
     */
    void registerRouteDefinition(EipRouteDefinition eipInterface);

    /**
     * 注销路由定义
     */
    void cancellationRouteDefinition(EipRouteDefinition eipInterface);

    /**
     * 注册开放接口
     */
    void registerOpenInterface(EipOpenInterface eipInterface);

    /**
     * 注销开放接口
     */
    void cancellationOpenInterface(EipOpenInterface eipInterface);
}
