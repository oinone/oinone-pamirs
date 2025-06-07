package pro.shushi.pamirs.eip.api.service;

import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.IEipOpenInterface;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;
import pro.shushi.pamirs.meta.api.dto.common.Result;

/**
 * 提供三种类型接口的管理方法
 */
@SuppressWarnings("rawtypes")
public interface EipInterfaceService {

    /**
     * 注册接口
     *
     * @param eipApi 接口
     * @return 注册结果
     */
    Result<String> registerApi(IEipApi eipApi);

    /**
     * 注销接口
     *
     * @param eipApi 接口
     * @return 注销结果
     */
    Result<String> cancellationApi(IEipApi eipApi);

    /**
     * 注册集成接口
     *
     * @param eipInterface 集成接口
     * @return 注册结果
     */
    Result<String> registerInterface(IEipIntegrationInterface eipInterface);

    /**
     * 注销集成接口
     *
     * @param eipInterface 集成接口
     * @return 注销结果
     */
    Result<String> cancellationInterface(IEipIntegrationInterface eipInterface);

    /**
     * 注册路由定义
     *
     * @param eipInterface 路由定义
     * @return 注册结果
     */
    Result<String> registerRouteDefinition(EipRouteDefinition eipInterface);

    /**
     * 注销路由定义
     *
     * @param eipInterface 路由定义
     * @return 注销结果
     */
    Result<String> cancellationRouteDefinition(EipRouteDefinition eipInterface);

    /**
     * 注册开放接口
     *
     * @param eipInterface 开放接口
     * @return 注册结果
     */
    Result<String> registerOpenInterface(IEipOpenInterface eipInterface);

    /**
     * 注销开放接口
     *
     * @param eipInterface 开放接口
     * @return 注销结果
     */
    Result<String> cancellationOpenInterface(IEipOpenInterface eipInterface);
}
