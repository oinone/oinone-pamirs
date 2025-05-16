package pro.shushi.pamirs.framework.orm.client.converter.processor;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.client.converter.field.ClientSerializeConvertor;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.function.FieldConverterApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 前端序列化与反序列化转换处理
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class ClientSerializeProcessor implements FieldConverterApi {

    @Resource
    private ClientSerializeConvertor frontEndSerializeConvertor;

    @Override
    public void in(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value) {
            return;
        }
        value = frontEndSerializeConvertor.in(fieldConfig, value);
        origin.put(fieldConfig.getLname(), value);
    }

    @Override
    public void out(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value) {
            return;
        }
        value = frontEndSerializeConvertor.out(fieldConfig, value);
        origin.put(fieldConfig.getLname(), value);
    }

}
