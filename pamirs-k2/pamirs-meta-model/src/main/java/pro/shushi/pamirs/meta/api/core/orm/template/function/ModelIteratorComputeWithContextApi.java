package pro.shushi.pamirs.meta.api.core.orm.template.function;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;

/**
 * 模型遍历计算
 * 2021/1/6 11:34 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ModelIteratorComputeWithContextApi extends CommonApi {

    /**
     * 遍历计算
     *
     * @param totalContext 上下文
     * @param model        模型编码
     * @param origin       待计算对象
     * @return 计算结果
     */
    Object run(ModelComputeContext totalContext, String model, Object origin);

}