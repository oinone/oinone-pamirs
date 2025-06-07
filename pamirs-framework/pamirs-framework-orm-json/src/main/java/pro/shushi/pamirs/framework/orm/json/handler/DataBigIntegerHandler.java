package pro.shushi.pamirs.framework.orm.json.handler;

import pro.shushi.pamirs.framework.orm.json.enmu.DataFeature;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * 大整数处理
 * <p>
 * 2021/9/24 3:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class DataBigIntegerHandler {

    public static void toBigInteger(ModelFieldConfig fieldConfig, Map<String, Object> origin, int features) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value) {
            return;
        }
        value = fieldToBigInteger(fieldConfig, value, features);
        origin.put(fieldConfig.getLname(), value);
    }

    public static void stringify(ModelFieldConfig fieldConfig, Map<String, Object> origin, int features) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value) {
            return;
        }
        value = fieldStringify(fieldConfig, value, features);
        origin.put(fieldConfig.getLname(), value);
    }

    public static Object fieldToBigInteger(ModelFieldConfig fieldConfig, Object fieldValue, int features) {
        boolean writeLongToString = !DataFeature.isEnabled(features, DataFeature.WriteLongUsingToLong);
        if (writeLongToString && Long.class.getName().equals(fieldConfig.getLtype())) {
            return Long.parseLong((String.valueOf(fieldValue)));
        } else if (BigInteger.class.getName().equals(fieldConfig.getLtype())
                && fieldValue instanceof BigDecimal
        ) {
            return ((BigDecimal) fieldValue).toBigInteger();
        }
        return fieldValue;
    }

    public static Object fieldStringify(ModelFieldConfig fieldConfig, Object fieldValue, int features) {
        boolean writeLongToString = !DataFeature.isEnabled(features, DataFeature.WriteLongUsingToLong);
        if (writeLongToString && Long.class.getName().equals(fieldConfig.getLtype())) {
            return String.valueOf(fieldValue);
        } else if (BigInteger.class.getName().equals(fieldConfig.getLtype())) {
            return new BigDecimal((BigInteger) fieldValue);
        }
        return fieldValue;
    }

}
