package pro.shushi.pamirs.framework.orm.converter.processor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.orm.convert.DataConverter;
import pro.shushi.pamirs.meta.api.core.orm.serialize.SerializeProcessor;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 持久化层序列化模板
 * <p>
 * 非递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class PersistenceSerializeProcessor {

    private static final HoldKeeper<SerializeProcessor<?>> holder = new HoldKeeper<>();

    private static SerializeProcessor<?> getApi() {
        return holder.supply(() -> Spider.getDefaultExtension(SerializeProcessor.class));
    }

    @Resource
    private DataConverter persistenceDataConverter;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void serialize(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        if (fieldConfig.getStore() && !TypeUtils.isBaseType(fieldConfig.getLtype())) {
            Object value = origin.get(fieldConfig.getLname());
            if (null != value && !TypeUtils.isBaseType(value.getClass())) {
                SerializeProcessor serializeProcessor = Spider.getDefaultExtension(SerializeProcessor.class);
                value = serializeProcessor.serialize(fieldConfig.getStoreSerialize(), fieldConfig.getLtype(), value);
                origin.put(fieldConfig.getLname(), value);
            }
        }
    }

    @SuppressWarnings({"rawtypes"})
    public void deserialize(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        if (fieldConfig.getStore() && !TypeUtils.isStringType(fieldConfig.getLtype())) {
            Object value = origin.get(fieldConfig.getLname());
            if (null == value) {
                return;
            }
            String ttype = fieldConfig.getTtype();
            if (TtypeEnum.isRelationType(ttype) || TtypeEnum.MAP.value().equals(ttype)) {
                String stringValue = TypeUtils.prepareString(value);
                if (stringValue != null) {
                    value = stringValue;
                }
            } else if (TtypeEnum.ENUM.value().equals(ttype) && value instanceof Number) {
                value = String.valueOf(value);
            }
            if (!TypeUtils.isPrimitiveOrString(value.getClass().getName())
                    || StringUtils.isBlank(fieldConfig.getStoreSerialize()) && !(value instanceof String)
                    && !TypeUtils.isEnumClass(fieldConfig.getLtype())
                    || TtypeEnum.isDateType(fieldConfig.getTtype())
            ) {
                return;
            }

            String valueString = TypeUtils.stringValueOf(value);
            if (StringUtils.isBlank(valueString)) {
                origin.remove(fieldConfig.getLname());
                return;
            }

            SerializeProcessor serializeProcessor = getApi();
            value = serializeProcessor.deserialize(fieldConfig.getStoreSerialize(), fieldConfig.getLtype(),
                    fieldConfig.getLtypeT(), fieldConfig.getFormat(), valueString);

            if (TtypeEnum.isRelationType(fieldConfig.getTtype()) && PamirsSession.directive().isFromClient()) {
                persistenceDataConverter.out(fieldConfig.getReferences(), value);
            }

            origin.put(fieldConfig.getLname(), value);
        }
    }

}
