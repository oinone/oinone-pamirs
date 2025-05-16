package pro.shushi.pamirs.meta.api.core.orm.template.function;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeContext;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.util.Map;

/**
 * 字段计算（使用ModelConfig，同{@link FieldComputeApi}）
 *
 * @author Adamancy Zhang at 10:05 on 2024-10-17
 */
public interface PersistenceFieldComputeApi extends CommonApi {

    /**
     * 字段计算
     *
     * @param config 字段配置
     * @param dMap   数据
     */
    void run(FieldComputeContext context, ModelConfig modelConfig, ModelFieldConfig config, Map<String, Object> dMap);

}