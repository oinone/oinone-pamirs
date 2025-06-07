package pro.shushi.pamirs.meta.api.core.compute.data;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

/**
 * 字段计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface FieldComputer<T> extends CommonApi {

    /**
     * 字段计算
     *
     * @param context 上下文
     * @param field   字段配置
     * @param data    实体数据
     * @return 计算结果
     */
    Result<Void> compute(ComputeContext context, ModelFieldConfig field, T data);

}
