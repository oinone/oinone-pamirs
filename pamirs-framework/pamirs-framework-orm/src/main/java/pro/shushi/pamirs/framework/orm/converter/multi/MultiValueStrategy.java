package pro.shushi.pamirs.framework.orm.converter.multi;

import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 多值处理策略
 * 2021/1/12 4:49 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class MultiValueStrategy {

    public static Object submit(ModelFieldConfig fieldConfig,
                                Object value,
                                BiFunction<String, Object, Object> computeFunction) {
        return submit(fieldConfig, value, computeFunction, null, null, null, null);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object submit(ModelFieldConfig fieldConfig,
                                Object value,
                                BiFunction<String, Object, Object> computeFunction,
                                Function<Object, Object> objectPreAction,
                                Function<Object, Object> objectAfterAction,
                                Function<List<?>, Object> listAfterAction,
                                Function<Object[], Object> arrayAfterAction) {
        Boolean multi = fieldConfig.getMulti();
        if (null == value && null != multi && multi) {
            return null;
        }
        if (value instanceof List) {
            List list = new ArrayList();
            for (Object obj : (List) value) {
                list.add(computeFunction.apply(Optional.ofNullable(fieldConfig.getLtypeT()).orElse(fieldConfig.getLtype()), obj));
            }
            if (null == listAfterAction) {
                return list;
            }
            return listAfterAction.apply(list);
        } else if (null != value && value.getClass().isArray()) {
            Object[] values = (Object[]) value;
            Object[] array = new Object[values.length];
            for (int i = 0; i < array.length; i++) {
                array[i] = computeFunction.apply(Optional.ofNullable(fieldConfig.getLtypeT()).orElse(fieldConfig.getLtype()), values[i]);
            }
            if (null == arrayAfterAction) {
                return array;
            }
            return arrayAfterAction.apply(array);
        } else {
            Object result = Optional.ofNullable(objectPreAction).map(v -> v.apply(value)).orElse(value);
            final Object finalResult = computeFunction.apply(fieldConfig.getLtype(), result);
            return Optional.ofNullable(objectAfterAction).map(v -> v.apply(finalResult)).orElse(finalResult);
        }
    }

}
