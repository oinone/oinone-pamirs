package pro.shushi.pamirs.framework.compute.serialize;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.utils.DictUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.orm.serialize.Serializer;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.enmu.SerializeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.List;

/**
 * 位表达式序列生成处理器实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@SuppressWarnings("rawtypes")
@Slf4j
@Component
public class BitSerializeProcessor implements Serializer<Object, Object> {

    @Override
    public Long serialize(String ltype, Object value) {
        if (null == value) {
            return null;
        }
        if (List.class.isAssignableFrom(value.getClass())) {
            List list = (List) value;
            Long sum = 0L;
            for (Object obj : list) {
                if (obj instanceof IEnum) {
                    sum |= (Long) ((IEnum) obj).value();
                } else if (obj instanceof Long) {
                    sum |= (Long) obj;
                } else if (obj instanceof Integer) {
                    sum |= (Integer) obj;
                }
            }
            return sum;
        } else {
            return (Long) value;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object deserialize(String ltype, String ltypeT, Object value, String dictionary) {
        if (null == value) {
            return null;
        }
        long longValue;
        if (value instanceof String) {
            longValue = Long.parseLong((String) value);
        } else {
            longValue = ((Number) value).longValue();
        }
        if (TypeUtils.isCollection(ltype)) {
            if (null == ltypeT && StringUtils.isNotBlank(dictionary)) {
                DataDictionary dataDictionary = PamirsSession.getContext().getDictionary(dictionary);
                ltypeT = dataDictionary.getLname();
                if (StringUtils.isBlank(ltypeT) || BaseEnum.class.getName().equals(ltypeT)) {
                    return DictUtils.fetchEnumsByBits(dictionary, longValue);
                }
            }
            if (null != ltypeT) {
                Class<?> valueClass = TypeUtils.getClass(ltypeT);
                if (IEnum.class.isAssignableFrom(valueClass)) {
                    return BaseEnum.getEnumsByBits((Class<IEnum>) valueClass, longValue);
                }
            }
        }
        return value;
    }

    @Override
    public String type() {
        return SerializeEnum.BIT.value();
    }

}
