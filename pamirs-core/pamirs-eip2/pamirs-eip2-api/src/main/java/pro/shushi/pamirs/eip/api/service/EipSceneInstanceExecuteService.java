package pro.shushi.pamirs.eip.api.service;

import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

/**
 * eip 执行服务
 *
 * @param <T> 上下文承载对象类型
 * @author Adamancy Zhang
 * @date 2020-11-30 10:56
 */
@Fun(EipSceneInstanceExecuteService.FUN_NAMESPACE)
public interface EipSceneInstanceExecuteService<T> {

    String FUN_NAMESPACE = "pamirs.eip.EipSceneInstanceExecuteService";

    @Function
    EipResult<T> callByCodeNoBody(String interfaceName);

    @Function
    EipResult<T> callByCodeAndBody(String interfaceName, Object body);

    @Function
    EipResult<T> callByCode(String sceneInstanceCode, T executorContext, Object body);

}
