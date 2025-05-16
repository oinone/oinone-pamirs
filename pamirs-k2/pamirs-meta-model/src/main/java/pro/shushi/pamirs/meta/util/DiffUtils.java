package pro.shushi.pamirs.meta.util;

import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

import java.util.*;

/**
 * 差量工具类
 * <p>
 * 2020/11/19 10:59 上午
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro
 * @version 1.0.0
 */
public class DiffUtils {

    public static Set<String> excludeFields = SetUtils.hashSet("_d_model", "_d_d_model",
            "id", "writeDate", "createDate", "writeUid", "createUid", "aggs", "authCondition", "META_BIT",
            "sign", "hash", "stringify", "size_pamirs");
    public static SimplePropertyPreFilter fieldFilter;

    static {
        fieldFilter = new SimplePropertyPreFilter();
        fieldFilter.getExcludes().addAll(excludeFields);
    }

    public static PropertyFilter emptyArrayFilter = (object, name, value) -> {
        if (null == value) {
            return false;
        }
        if (value.getClass().isArray()) {
            // false表示字段将被排除在外
            return !ArrayUtils.isEmpty((Object[]) value);
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
            return !CollectionUtils.isEmpty((Collection<?>) value);
        }
        return true;
    };

    /**
     * 是否存在差量
     *
     * @param t   元数据
     * @param <T> 元数据类型
     * @return 序列化元数据
     */
    public static <T extends MetaBaseModel> boolean diff(T t) {
        String hash = t.getHash();
        String stringify = t.stringify();
        String newHash = t.hashSum(stringify);
        if (null != hash) {
            if (hash.equals(newHash)) {
                if (stringify.equals(t.getStringify())) {
                    return false;
                }
            }
        }
        t.setHash(newHash);
        return true;
    }

    /**
     * 差量序列化
     *
     * @param t   元数据
     * @param <T> 元数据类型
     * @return 序列化元数据
     */
    public static <T extends MetaBaseModel> String stringify(T t, String... additionalExcludes) {
        SimplePropertyPreFilter fieldFilter;
        if (additionalExcludes.length == 0) {
            fieldFilter = DiffUtils.fieldFilter;
        } else {
            fieldFilter = new SimplePropertyPreFilter();
            fieldFilter.getExcludes().addAll(excludeFields);
            fieldFilter.getExcludes().addAll(Arrays.asList(additionalExcludes));
        }
        StringBuilder sb = stringify(t, fieldFilter);
        return sb.toString();
    }

    private static <T extends MetaBaseModel> StringBuilder stringify(T t, SimplePropertyPreFilter fieldFilter) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Map.Entry<String, Object> entry : new TreeMap<>(t.get_d()).entrySet()) {
            String paramKey = entry.getKey();
            if (fieldFilter.getExcludes().contains(paramKey)) {
                //过滤掉排除字段
                continue;
            }
            appendValue(sb, paramKey, entry.getValue());
        }
        sb.append("}");
        return sb;
    }

    @SuppressWarnings("ConstantConditions")
    private static void appendValue(StringBuilder sb, String paramKey, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof String) {
            if (StringUtils.isBlank((String) value)) {
                return;
            }
        }
        Class<?> clazz = value.getClass();
        if (D.class.isAssignableFrom(clazz)) {
            value = ((D) value).get_d();
            clazz = value.getClass();
        }
        if (clazz.isArray()) {
            if (!ArrayUtils.isEmpty((Object[]) value)) {
                sb.append(paramKey).append(":").append("[");
                for (Object obj : (Object[]) value) {
                    appendValue(sb, "", obj);
                }
                sb.append("]");
            }
        } else if (Collection.class.isAssignableFrom(clazz)) {
            if (!CollectionUtils.isEmpty((Collection<?>) value)) {
                sb.append(paramKey).append(":").append("[");
                for (Object obj : (Collection<?>) value) {
                    appendValue(sb, "", obj);
                }
                sb.append("]");
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            if (!MapUtils.isEmpty((Map<?, ?>) value)) {
                sb.append(paramKey).append(":").append("{");
                for (Map.Entry<?, ?> obj : new TreeMap<>((Map<?, ?>) value).entrySet()) {
                    appendValue(sb, String.valueOf(obj.getKey()), obj.getValue());
                }
                sb.append("}");
            }
        } else {
            appendKeyAndValue(sb, paramKey, value);
        }
    }

    private static void appendKeyAndValue(StringBuilder sb, String key, Object value) {
        String type = value.getClass().getTypeName();
        switch (type) {
            case "java.lang.String":
            case "java.lang.Integer":
            case "java.lang.Short":
            case "java.lang.Byte":
            case "java.lang.Long":
            case "java.math.BigInteger":
            case "java.math.BigDecimal":
            case "java.lang.Float":
            case "java.lang.Double":
            case "java.lang.Boolean":
            case "boolean":
            case "byte":
            case "char":
            case "double":
            case "float":
            case "int":
            case "long":
            case "short":
                sb.append(key).append(":").append(value);
                sb.append(",");
                return;
            case "java.util.Date":
            case "java.sql.Timestamp":
            case "java.sql.Date":
            case "java.sql.Time":
                sb.append(key).append(":").append(((Date) value).getTime());
                sb.append(",");
                return;
            default:
                if (IEnum.class.isAssignableFrom(value.getClass())) {
                    //如果是枚举
                    sb.append(key).append(":").append(((IEnum<?>) value).value());
                    sb.append(",");
                }
        }
    }
}
