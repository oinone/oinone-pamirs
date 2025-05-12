package pro.shushi.pamirs.meta.api.core.orm.template.function;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;

/**
 * 模型后置计算
 * 2021/1/6 11:34 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface PersistenceModelAfterComputeWithContextApi<T> extends CommonApi {

    /**
     * 对象计算
     *
     * @param totalContext 上下文
     * @param modelConfig  模型
     * @param obj          待计算对象
     * @return 计算结果
     */
    T after(ModelComputeContext totalContext, ModelConfig modelConfig, T obj);

}