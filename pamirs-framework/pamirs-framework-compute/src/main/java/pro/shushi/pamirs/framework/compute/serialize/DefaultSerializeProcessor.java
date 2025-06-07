package pro.shushi.pamirs.framework.compute.serialize;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.orm.serialize.SerializeProcessor;
import pro.shushi.pamirs.meta.api.core.orm.serialize.Serializer;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.enmu.SerializeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_SERIALIZER_ERROR;
import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_UN_SERIALIZER_ERROR;

/**
 * 序列生成处理器默认实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
@SPI.Service
@Component
public class DefaultSerializeProcessor<T> implements SerializeProcessor<T> {

    private static Map<String, Serializer> processorMap;

    @SuppressWarnings("unused")
    @EventListener
    @Order(5)
    public void init(ApplicationStartedEvent event) {
        processorMap = new HashMap<>();
        Map<String, Serializer> beanMap = BeanDefinitionUtils.getBeansOfType(Serializer.class);
        for (Serializer serializer : Objects.requireNonNull(beanMap).values()) {
            processorMap.put(serializer.type(), serializer);
        }
    }

    @Override
    public Object serialize(String serializeType, String ltype, Object value) {
        if (null == value) {
            return null;
        }
        Class<?> valueClass = value.getClass();
        if (BaseEnum.class.isAssignableFrom(valueClass)) {
            return ((BaseEnum) value).value();
        } else if (IEnum.class.isAssignableFrom(valueClass)) {
            return ((IEnum) value).value();
        }
        if (value instanceof String) {
            return value;
        } else if (value instanceof Long) {
            return value;
        }
        if (StringUtils.isBlank(serializeType) || Field.serialize.NON.equals(serializeType)) {
            serializeType = SerializeEnum.JSON.value();
        }
        Serializer serializer = processorMap.get(serializeType);
        if (null == serializer) {
            throw PamirsException.construct(BASE_SERIALIZER_ERROR)
                    .appendMsg("type:" + serializeType).errThrow();
        }
        return serializer.serialize(ltype, value);
    }

    @Override
    public T deserialize(String serializeType, String ltype, String ltypeT, String format, String value) {
        Class<?> ltypeClazz = TypeUtils.getClass(ltype);
        if (TypeUtils.isIEnumClass(ltypeClazz)) {
            return (T) Enums.getEnumByValue((Class<IEnum>) ltypeClazz, value);
        }
        if (null == value) {
            return null;
        }
        if (StringUtils.isBlank(serializeType) || Field.serialize.NON.equals(serializeType)) {
            serializeType = SerializeEnum.JSON.value();
        }
        Serializer serializer = processorMap.get(serializeType);
        if (null == serializer) {
            throw PamirsException.construct(BASE_UN_SERIALIZER_ERROR).errThrow();
        }
        return (T) serializer.deserialize(ltype, ltypeT, value, format);
    }

}
