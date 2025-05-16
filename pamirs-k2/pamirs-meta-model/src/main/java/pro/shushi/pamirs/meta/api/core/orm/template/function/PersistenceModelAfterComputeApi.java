package pro.shushi.pamirs.meta.api.core.orm.template.function;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;

/**
 * 模型后置计算（使用ModelConfig，同{@link ModelAfterComputeApi}）
 *
 * @author Adamancy Zhang at 10:02 on 2024-10-17
 */
public interface PersistenceModelAfterComputeApi<T> extends CommonApi {

    /**
     * 对象计算
     *
     * @param modelConfig 模型
     * @param obj         待计算对象
     * @return 计算结果
     */
    T after(ModelConfig modelConfig, T obj);

}