package pro.shushi.pamirs.framework.orm.serialize;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.orm.serialize.SerializeProcessor;
import pro.shushi.pamirs.meta.api.core.orm.template.DataComputeTemplate;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import javax.annotation.Resource;

/**
 * 序列化模板
 * <p>
 * 非递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class SerializeTemplate {

    @Resource
    private DataComputeTemplate dataComputeTemplate;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> T serialize(String model, T origin, boolean ignoreNonStoreField) {
        SerializeProcessor serializeProcessor = CommonApiFactory.getApi(SerializeProcessor.class);
        return dataComputeTemplate.compute(model, origin, (oModel, oObj) -> this.serialize(oModel, oObj, ignoreNonStoreField),
                (oModel, oObj) -> oObj, (oModel, oObj) -> oObj, (context, fieldConfig, dMap) -> {
                    if ((!ignoreNonStoreField || fieldConfig.getStore()) && !TypeUtils.isBaseType(fieldConfig.getLtype())) {
                        Object value = dMap.get(fieldConfig.getLname());
                        if (null != value && !(value instanceof String)) {
                            value = serializeProcessor.serialize(fieldConfig.getStoreSerialize(), fieldConfig.getLtype(), value);
                            dMap.put(fieldConfig.getLname(), value);
                        }
                    }
                });
    }

    @SuppressWarnings({"rawtypes"})
    public <T> T deserialize(String model, T origin, boolean ignoreNonStoreField) {
        SerializeProcessor serializeProcessor = Spider.getDefaultExtension(SerializeProcessor.class);
        return dataComputeTemplate.compute(model, origin, (oModel, oObj) -> this.serialize(oModel, oObj, ignoreNonStoreField),
                (oModel, oObj) -> oObj, (oModel, oObj) -> oObj, (context, fieldConfig, dMap) -> {
                    if ((!ignoreNonStoreField || fieldConfig.getStore())
                            && !TypeUtils.isStringType(fieldConfig.getLtype()) && !TtypeEnum.isStringType(fieldConfig.getTtype())) {
                        Object value = dMap.get(fieldConfig.getLname());
                        if (value instanceof String) {
                            if (StringUtils.isBlank((String) value)) {
                                return;
                            }
                            value = serializeProcessor.deserialize(fieldConfig.getStoreSerialize(), fieldConfig.getLtype(),
                                    fieldConfig.getLtypeT(), fieldConfig.getFormat(), (String) value);
                            dMap.put(fieldConfig.getLname(), value);
                        }
                    }
                });
    }

}
