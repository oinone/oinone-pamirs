package pro.shushi.pamirs.framework.configure.annotation.core.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * 字段元信息缓存
 * <p>
 * 2021/4/14 11:35 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class FieldMetaCache {

    private static final Cache<String, Long> fieldSlot =
            Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();

    public static Cache<String, Long> getFieldSlot() {
        return fieldSlot;
    }

    public static Long getFieldSlot(Field field) {
        String key = fieldSlotKey(field);
        return getFieldSlot().get(key, k -> MetaDefaultConstants.PRIORITY_VALUE);
    }

    public static void setFieldSlot(Field field, Number slot) {
        String key = fieldSlotKey(field);
        getFieldSlot().put(key, slot.longValue());
    }

    private static String fieldSlotKey(Field field) {
        return field.getDeclaringClass().getName() + CharacterConstants.SEPARATOR_OCTOTHORPE + field.getName();
    }

}
