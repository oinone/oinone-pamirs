package pro.shushi.pamirs.meta.common.enmu;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.util.ListUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IEnum枚举通用方法
 * 2021/1/11 5:45 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class Enums {

    @SuppressWarnings("rawtypes")
    private static <E extends IEnum> void checkEnumClass(Class<E> e) {
        if (null == e) {
            throw new IllegalArgumentException("The Enum Class must not be null");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E extends IEnum> E getEnum(Class<E> enumClass, String name) {
        checkEnumClass(enumClass);
        if (enumClass.isEnum()) {
            for (E em : enumClass.getEnumConstants()) {
                if (em.name().equals(name)) {
                    return em;
                }
            }
        } else {
            return (E) BaseEnum.getEnum((Class<BaseEnum>) enumClass, name);
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E extends IEnum, T extends Serializable> E getEnumByValue(Class<E> enumClass, T value) {
        checkEnumClass(enumClass);
        if (enumClass.isEnum()) {
            String valueString = String.valueOf(value);
            for (E em : enumClass.getEnumConstants()) {
                Object enumValue = em.value();
                if (enumValue == null) {
                    if (value == null) {
                        return em;
                    }
                } else {
                    if (String.valueOf(enumValue).equals(valueString)) {
                        return em;
                    }
                }
            }
        } else {
            return (E) BaseEnum.getEnumByValue((Class<BaseEnum>) enumClass, value);
        }
        return null;
    }

    /**
     * @param enumClass   枚举类
     * @param displayName 显示名称
     * @return 枚举
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E extends IEnum> E getEnumByDisplayName(Class<E> enumClass, String displayName) {
        checkEnumClass(enumClass);
        if (StringUtils.isBlank(displayName)) {
            return null;
        }
        if (enumClass.isEnum()) {
            for (E em : enumClass.getEnumConstants()) {
                if (em.displayName().equals(displayName)) {
                    return em;
                }
            }
        } else {
            return (E) BaseEnum.getEnumByDisplayName((Class<BaseEnum>) enumClass, displayName);
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <E extends IEnum> Map<String, E> getEnumMap(Class<E> enumClass) {
        if (enumClass.isEnum()) {
            Map<String, E> map = new HashMap<>();
            for (Object em : enumClass.getEnumConstants()) {
                IEnum iEnum = (IEnum) em;
                map.put(iEnum.name(), (E) iEnum);
            }
            return map;
        }
        return (Map<String, E>) BaseEnum.getEnumMap((Class<BaseEnum>) enumClass);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E extends IEnum> List<E> getEnumList(Class<E> enumClass) {
        if (enumClass.isEnum()) {
            return ListUtils.toList(enumClass.getEnumConstants());
        }
        return (List<E>) BaseEnum.getEnumList((Class<BaseEnum>) enumClass);
    }

    @SuppressWarnings("rawtypes")
    public static <E extends IEnum, T extends Serializable> String getNameByValue(Class<E> enumClass, T val) {
        if (null != val) {
            IEnum mEnum = getEnumByValue(enumClass, val);
            if (null != mEnum) {
                return mEnum.name();
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public static <E extends IEnum, T extends Serializable> String getDisplayNameByValue(Class<E> enumClass, T val, String defaultDisplayName) {
        if (null != val) {
            IEnum mEnum = getEnumByValue(enumClass, val);
            if (null != mEnum) {
                return mEnum.displayName();
            }
        }
        return defaultDisplayName;
    }

    @SuppressWarnings("rawtypes")
    public static <E extends IEnum, T extends Serializable> String getDisplayNameByValue(Class<E> enumClass, T val) {
        return getDisplayNameByValue(enumClass, val, "未定义");
    }

    @SuppressWarnings("rawtypes")
    public static <E extends IEnum> String getDisplayNameByName(Class<E> enumClass, String name, String defaultDisplayName) {
        if (StringUtils.isNotBlank(name)) {
            IEnum mEnum = getEnum(enumClass, name);
            if (null != mEnum) {
                return mEnum.displayName();
            }
        }
        return defaultDisplayName;
    }

    @SuppressWarnings("rawtypes")
    public static <E extends IEnum> String getDisplayNameByName(Class<E> enumClass, String name) {
        return getDisplayNameByName(enumClass, name, "未定义");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <E extends IEnum, T extends Serializable> T getValueByName(Class<E> enumClass, String name) {
        if (StringUtils.isNotBlank(name)) {
            IEnum mEnum = getEnum(enumClass, name);
            if (null != mEnum) {
                return (T) mEnum.value();
            }
        }
        return null;
    }

}
