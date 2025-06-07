package pro.shushi.pamirs.framework.orm.client.converter.processor;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.function.FieldConverterApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.List;
import java.util.Map;

/**
 * 前端数组转换服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class ClientArrayProcessor implements FieldConverterApi {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void in(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        Object value = origin.get(fieldConfig.getLname());
        if (TypeUtils.isArrayType(fieldConfig.getLtype()) && value instanceof List) {
            origin.put(fieldConfig.getLname(), ListUtils.toArray((List) value));
        }
    }

}
