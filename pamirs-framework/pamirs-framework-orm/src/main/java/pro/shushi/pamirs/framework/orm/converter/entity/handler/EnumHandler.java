package pro.shushi.pamirs.framework.orm.converter.entity.handler;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.entry.NullValue;
import pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.orm.serialize.Serializer;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.SerializeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.NumberUtils;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 枚举转换处理
 * <p>
 * 非递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
@Slf4j
public class EnumHandler {

    @Resource
    private EnumNamedHandler enumNamedHandler;

    @Resource
    private Serializer<Object, Long> bitSerializeProcessor;

    public void toEnum(ModelFieldConfig fieldConfig, Map<String, Object> origin, int features) {
        Object value = origin.get(fieldConfig.getLname());
        if (value == null || NullValue.INSTANCE.equals(value)) {
            return;
        }
        try {
            if (!(value instanceof Number) || !"java.lang.Long".equals(fieldConfig.getLtype())) {
                value = enumNamedHandler.toEnum(fieldConfig, value, features);
                if (SerializeEnum.BIT.value().equals(fieldConfig.getRequestSerialize())
                        && !(value instanceof Number) && Long.class.getName().equals(fieldConfig.getLtype())) {
                    value = bitSerializeProcessor.serialize(fieldConfig.getLtype(), value);
                }
            }
            if (null != value) {
                origin.put(fieldConfig.getLname(), value);
            }
        } catch (Throwable e) {
            String errorMsg = String.format("EnumHandler toEnum method error, model：[%s],field：[%s],tType:[%s],lType:[%s],value:[%s],valueClass:[%s]", fieldConfig.getModel(), fieldConfig.getField(), fieldConfig.getTtype(), fieldConfig.getLtype(), value, value.getClass());
            log.error("EnumHandler toEnum method error", errorMsg);
            throw PamirsException.construct(OrmExpEnumerate.BASE_FIELD_CONVERTER_ERROR, e).appendMsg(errorMsg).errThrow();
        }

    }

    public void stringify(ModelFieldConfig fieldConfig, Map<String, Object> origin, int features) {
        Object value = origin.get(fieldConfig.getLname());
        if (value == null || NullValue.INSTANCE.equals(value)) {
            return;
        }
        try {
            if (SerializeEnum.BIT.value().equals(fieldConfig.getRequestSerialize())
                    && value instanceof Number && TtypeEnum.ENUM.value().equals(fieldConfig.getTtype())) {
                String ltype = fieldConfig.getLtype();
                if (Long.class.getName().equals(fieldConfig.getLtype())) {
                    ltype = List.class.getName();
                }
                value = bitSerializeProcessor.deserialize(ltype, fieldConfig.getLtypeT(),
                        NumberUtils.longValue((Number) value), fieldConfig.getDictionary());
            }
            value = enumNamedHandler.stringify(fieldConfig, value, features);
            if (null != value) {
                origin.put(fieldConfig.getLname(), value);
            }
        } catch (Throwable e) {
            String errorMsg = String.format("EnumHandler stringify method error, model：[%s],field：[%s],tType:[%s],lType:[%s],value:[%s],valueClass:[%s]", fieldConfig.getModel(), fieldConfig.getField(), fieldConfig.getTtype(), fieldConfig.getLtype(), value, value.getClass());
            log.error("EnumHandler stringify method error", errorMsg);
            throw PamirsException.construct(OrmExpEnumerate.BASE_FIELD_CONVERTER_ERROR, e).appendMsg(errorMsg).errThrow();
        }
    }
}
