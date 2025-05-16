package pro.shushi.pamirs.meta.api.core.orm.template.function;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeContext;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.util.Map;

/**
 * 字段校验
 * 2021/1/6 11:34 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface FieldCheckApi extends CommonApi {

    /**
     * 字段校验
     *
     * @param context 上下文
     * @param config  字段配置
     * @param dMap    数据
     */
    void check(FieldComputeContext context, ModelFieldConfig config, Map<String, Object> dMap);

}