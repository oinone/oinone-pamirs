package pro.shushi.pamirs.eip.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

/**
 * @author Adamancy Zhang at 16:47 on 2025-08-14
 */
@Fun(EipAsyncService.FUN_NAMESPACE)
public interface EipAsyncService {

    String FUN_NAMESPACE = "pamirs.eip.EipAsyncService";

    /**
     * 注册集成接口
     */
    @Function
    void registerInterface(String interfaceName);

    /**
     * 注销集成接口（删除时不可用）
     */
    @Function
    void cancellationInterface(String interfaceName);

    /**
     * 注册路由定义
     */
    @Function
    void registerRouteDefinition(String interfaceName);

    /**
     * 注销路由定义（删除时不可用）
     */
    @Function
    void cancellationRouteDefinition(String interfaceName);

    /**
     * 注册开放接口
     */
    @Function
    void registerOpenInterface(String interfaceName);

    /**
     * 注销开放接口（删除时不可用）
     */
    @Function
    void cancellationOpenInterface(String interfaceName);

}
