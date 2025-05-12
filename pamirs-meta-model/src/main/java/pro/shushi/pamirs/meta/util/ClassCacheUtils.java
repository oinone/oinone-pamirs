package pro.shushi.pamirs.meta.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类缓存工具类
 * <p>
 * 2021/2/19 8:16 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ClassCacheUtils {

    private static final Map<String, Class<?>> ltypeMap = new ConcurrentHashMap<>(256);
    private static final Map<String, Boolean> enumClazzMap = new ConcurrentHashMap<>(256);
    private static final Map<String, Boolean> iEnumClazzMap = new ConcurrentHashMap<>(256);

    public static Class<?> getClass(String ltype) {
        return ltypeMap.get(ltype);
    }

    public static void putClass(String ltype, Class<?> ltypeClazz) {
        //noinspection StringOperationCanBeSimplified
        ltypeMap.put(new String(ltype), ltypeClazz);
    }

    public static Boolean isEnum(String ltype) {
        return enumClazzMap.get(ltype);
    }

    public static void putIsEnum(String ltype, Boolean isEnum) {
        //noinspection StringOperationCanBeSimplified
        enumClazzMap.put(new String(ltype), isEnum);
    }

    public static Boolean isIEnum(String ltype) {
        return iEnumClazzMap.get(ltype);
    }

    public static void putIsIEnum(String ltype, Boolean isIEnum) {
        //noinspection StringOperationCanBeSimplified
        iEnumClazzMap.put(new String(ltype), isIEnum);
    }

}
