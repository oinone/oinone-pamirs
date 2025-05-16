package pro.shushi.pamirs.framework.orm.client.checker;

import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;

/**
 * 模型校验服务API
 * <p>
 * 2022/5/11 2:28 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ClientCheckServiceApi {

    /**
     * 校验服务
     *
     * @param model 模型编码
     * @param obj   待校验数据
     * @param <T>   泛型类型
     * @return 待校验数据
     */
    <T> T check(String model, T obj);

    /**
     * 校验服务
     *
     * @param model   模型编码
     * @param argName 参数名
     * @param obj     待校验数据
     * @param <T>     泛型类型
     * @return 待校验数据
     */
    <T> T check(String model, String argName, T obj);

    /**
     * 校验服务
     *
     * @param totalContext 上下文
     * @param model        模型编码
     * @param obj          待校验数据
     * @param <T>          泛型类型
     * @return 待校验数据
     */
    <T> T check(ModelComputeContext totalContext, String model, T obj);

}
