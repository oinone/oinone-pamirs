package pro.shushi.pamirs.framework.orm.converter.processor;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.orm.spi.PersistenceFieldExtendConverter;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeContext;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.List;
import java.util.Map;

/**
 * 后端扩展转换服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class PersistenceExtendProcessor {

    private static final HoldKeeper<List<PersistenceFieldExtendConverter>> holder = new HoldKeeper<>();

    private static List<PersistenceFieldExtendConverter> getConverters() {
        return holder.supply(() -> Spider.getLoader(PersistenceFieldExtendConverter.class).getOrderedExtensions());
    }

    public void in(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> data) {
        List<PersistenceFieldExtendConverter> converterList = getConverters();
        if (!converterList.isEmpty()) {
            for (PersistenceFieldExtendConverter converter : converterList) {
                converter.in(context, fieldConfig, data);
            }
        }
    }

    public void out(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> data) {
        List<PersistenceFieldExtendConverter> converterList = getConverters();
        if (!converterList.isEmpty()) {
            for (PersistenceFieldExtendConverter converter : converterList) {
                converter.out(context, fieldConfig, data);
            }
        }
    }

}
