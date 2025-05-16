package pro.shushi.pamirs.meta.api.core.orm.serialize.filter;

import com.alibaba.fastjson.serializer.ValueFilter;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * JSON序列化大数过滤器
 * <p>
 * 2021/9/27 12:02 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class BigDecimalSerializeFilter implements ValueFilter {

    @Override
    public Object process(Object object, String name, Object value) {
        if (value instanceof BigDecimal) {
            if (new BigDecimal(((BigDecimal) value).intValue()).compareTo((BigDecimal) value) == 0) {
                return ((BigDecimal) value).setScale(6, ROUND_HALF_UP);
            }
        }
        return value;
    }

}
