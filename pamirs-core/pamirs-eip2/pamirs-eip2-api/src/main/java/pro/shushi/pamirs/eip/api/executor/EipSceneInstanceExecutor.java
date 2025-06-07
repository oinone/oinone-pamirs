package pro.shushi.pamirs.eip.api.executor;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.service.EipSceneInstanceExecuteService;
import pro.shushi.pamirs.meta.api.CommonApiFactory;

/**
 * 默认SuperMap上下文执行器
 */
public class EipSceneInstanceExecutor {

    private final SuperMap executorContext;

    private EipSceneInstanceExecutor(SuperMap executorContext) {
        this.executorContext = executorContext;
    }

    /**
     * 获取执行器，并自动构建执行器上下文
     *
     * @return 执行器
     */
    public static EipSceneInstanceExecutor newInstance() {
        return newInstance(new SuperMap());
    }

    /**
     * 获取执行器，并指定执行器上下文
     *
     * @param executorContext 执行器上下文
     * @return 执行器
     */
    public static EipSceneInstanceExecutor newInstance(SuperMap executorContext) {
        if (executorContext == null) {
            executorContext = new SuperMap();
        }
        return new EipSceneInstanceExecutor(executorContext);
    }


    /**
     * 无参调用
     *
     * @param sceneInstanceCode 接口名称
     * @return 结果集
     */
    public EipResult<SuperMap> call(String sceneInstanceCode) {
        return CommonApiFactory.getApi(EipSceneInstanceExecuteService.class).callByCode(sceneInstanceCode, executorContext, null);
    }

    /**
     * 有参调用
     *
     * @param sceneInstanceCode 接口名称
     * @param body              入参
     * @return 结果集
     */
    public EipResult<SuperMap> call(String sceneInstanceCode, Object body) {
        return CommonApiFactory.getApi(EipSceneInstanceExecuteService.class).callByCode(sceneInstanceCode, executorContext, body);
    }
}
