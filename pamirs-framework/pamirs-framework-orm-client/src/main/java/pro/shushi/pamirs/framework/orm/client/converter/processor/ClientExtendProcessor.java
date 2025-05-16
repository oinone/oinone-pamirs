package pro.shushi.pamirs.framework.orm.client.converter.processor;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.core.orm.spi.ClientFieldExtendConverter;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.function.FieldConverterApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.List;
import java.util.Map;

/**
 * 前端扩展转换服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class ClientExtendProcessor implements FieldConverterApi {

    @Override
    public void in(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> data) {
        List<ClientFieldExtendConverter> converterList = Spider.getLoader(ClientFieldExtendConverter.class).getOrderedExtensions();
        if (!CollectionUtils.isEmpty(converterList)) {
            for (ClientFieldExtendConverter converter : converterList) {
                converter.in(context, fieldConfig, data);
            }
        }
    }

    @Override
    public void out(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> data) {
        List<ClientFieldExtendConverter> converterList = Spider.getLoader(ClientFieldExtendConverter.class).getOrderedExtensions();
        if (!CollectionUtils.isEmpty(converterList)) {
            for (ClientFieldExtendConverter converter : converterList) {
                converter.out(context, fieldConfig, data);
            }
        }
    }

}
