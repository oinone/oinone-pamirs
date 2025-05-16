package pro.shushi.pamirs.core.common;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Adamancy Zhang on 2021-01-30 18:52
 */
public class ObjectHelper {

    private ObjectHelper() {
        //reject create object
    }

    public static <T> T getOrDefault(T object, T defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        return object;
    }

    public static Long getLongValue(Object target, String field) {
        return getLongValue(target, field, null);
    }

    public static Long getLongValue(Object target, String field, Long defaultValue) {
        return getValue(target, field, defaultValue, value -> {
            if (value instanceof Long) {
                return (Long) value;
            } else if (value instanceof Number) {
                return ((Number) value).longValue();
            } else {
                if (value instanceof String) {
                    if (NumberHelper.isNumber((String) value)) {
                        return Long.valueOf((String) value);
                    }
                }
            }
            return defaultValue;
        });
    }

    public static String getStringValue(Object target, String field) {
        return getStringValue(target, field, null);
    }

    public static String getStringValue(Object target, String field, String defaultValue) {
        return getValue(target, field, defaultValue, value -> {
            if (value instanceof String) {
                return (String) value;
            }
            return defaultValue;
        });
    }

    private static <T> T getValue(Object target, String field, T defaultValue, Function<Object, T> getter) {
        if (target == null) {
            return defaultValue;
        }
        Object value = FieldUtils.getFieldValue(target, field);
        if (value == null) {
            return defaultValue;
        }
        return getter.apply(value);
    }

    public static boolean isBlank(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return StringUtils.isBlank((String) obj);
        }
        if (obj instanceof Collection) {
            return CollectionUtils.isEmpty((Collection<?>) obj);
        }
        if (obj instanceof Map) {
            return MapUtils.isEmpty((Map<?, ?>) obj);
        }
        return false;
    }

    public static boolean isNotBlank(Object obj) {
        return !isBlank(obj);
    }

    public static <T> boolean isRepeat(Set<T> traversalSet, T value) {
        return isRepeat(traversalSet, value, v -> v);
    }

    public static <V, T> boolean isRepeat(Set<T> traversalSet, V value, Function<V, T> converter) {
        int lastSize = traversalSet.size();
        traversalSet.add(converter.apply(value));
        return lastSize == traversalSet.size();
    }

    public static <T> boolean isNotRepeat(Set<T> traversalSet, T value) {
        return !isRepeat(traversalSet, value, v -> v);
    }

    public static <V, T> boolean isNotRepeat(Set<T> traversalSet, V value, Function<V, T> converter) {
        return !isRepeat(traversalSet, value, converter);
    }
}
