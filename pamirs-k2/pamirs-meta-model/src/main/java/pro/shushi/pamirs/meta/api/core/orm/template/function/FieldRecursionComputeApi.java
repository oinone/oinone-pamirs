package pro.shushi.pamirs.meta.api.core.orm.template.function;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.util.Map;

/**
 * 字段递归计算
 * 2021/1/6 11:34 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("rawtypes")
public interface FieldRecursionComputeApi extends CommonApi {

    /**
     * 字段计算
     *
     * @param subModelComputeProcessor 递归子处理器
     * @param config                   字段配置
     * @param dMap                     数据
     */
    default void run(ModelRecursionComputeApi subModelComputeProcessor, ModelFieldConfig config, Map<String, Object> dMap) {
    }

}