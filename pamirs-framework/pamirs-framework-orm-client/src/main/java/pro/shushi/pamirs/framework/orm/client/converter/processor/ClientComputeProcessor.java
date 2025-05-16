package pro.shushi.pamirs.framework.orm.client.converter.processor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.function.FieldConverterApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.util.Map;

/**
 * 前端计算处理
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class ClientComputeProcessor implements FieldConverterApi {

    @Override
    public void in(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        String compute = fieldConfig.getCompute();
        if (StringUtils.isBlank(compute)) {
            return;
        }
        Object value = origin.get(fieldConfig.getLname());
        value = Fun.run(fieldConfig.getModel(), compute, value);
        if (null == value) {
            return;
        }
        origin.put(fieldConfig.getLname(), value);
    }

}
