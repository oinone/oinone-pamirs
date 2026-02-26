package pro.shushi.pamirs.meta.util;

import com.google.common.primitives.Primitives;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.entity.DMap;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.enmu.BitEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.enmu.MetaExpEnumerate;

import java.io.Serializable;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.*;

/**
 * 类型处理工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:20 下午
 */
@Slf4j
public class TypeUtils {

    private static final String UNKNOWN_TYPE = "?";

    public static String stringValueOf(Object obj) {
        if (null == obj) return null;
        return String.valueOf(obj);
    }

    public static String stringNullableValueOf(Object obj) {
        if (null == obj) return "";
        return String.valueOf(obj);
    }

    /**
     * 预处理字符串（当且仅当确认对象为字符串类型时才可以使用）
     * <p>支持String、byte[]、Byte[]、Clob转换</p>
     *
     * @param obj 可识别的字符串对象
     * @return 转换成功则返回字符串，否则返回null
     */
    public static String prepareString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            return new String((byte[]) obj, StandardCharsets.UTF_8);
        } else if (obj instanceof Byte[]) {
            return new String(ArrayUtils.toPrimitive((Byte[]) obj), StandardCharsets.UTF_8);
        } else if (obj instanceof Clob) {
            Clob clob = (Clob) obj;
            try {
                // @see org.apache.ibatis.type.ClobTypeHandler
                return clob.getSubString(1, (int) clob.length());
            } catch (SQLException e) {
                throw PamirsException.construct(BASE_CLOB_TO_STRING_ERROR, e).errThrow();
            }
        }
        return null;
    }

    public static <T> Class<T> getClass(String type) {
        return getClass(type, true);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getClass(String type, boolean judgeMap) {
        if (UNKNOWN_TYPE.equals(type)) {
            return (Class<T>) Object.class;
        }
        if (StringUtils.isBlank(type)) {
            return (Class<T>) Map.class;
        }
        Class<?> ltypeClazz = ClassCacheUtils.getClass(type);
        if (null == ltypeClazz) {
            ltypeClazz = getPrimitiveType(type);
            if (null == ltypeClazz) {
                if (judgeMap && isMap(type)) {
                    ltypeClazz = Map.class;
                } else {
                    if (type.contains(CharacterConstants.SEPARATOR_LT)) {
                        type = StringUtils.substringBefore(type, "<");
                    }
                    try {
                        ltypeClazz = Class.forName(type);
                    } catch (ClassNotFoundException e) {
                        throw PamirsException.construct(BASE_CLASS_IS_NOT_EXISTS_ERROR, e).errThrow();
                    }
                }
            }
            ClassCacheUtils.putClass(type, ltypeClazz);
        }
        return (Class<T>) ltypeClazz;
    }

    public static Object getNewInstance(String type) {
        Class<?> clazz = getClass(type);
        return getNewInstance(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getNewInstance(Class<T> clazz) {
        try {
            if (Map.class.isAssignableFrom(clazz)) {
                return (T) new HashMap<>();
            }
            if (List.class.isAssignableFrom(clazz)) {
                return (T) new ArrayList<>();
            }
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("new instance error. class: {}", clazz.getName());
            throw PamirsException.construct(BASE_INSTANTIATION_ERROR, e).errThrow();
        }
    }

    public static Type getType(Field field) {
        return field.getType();
    }

    @SuppressWarnings("rawtypes")
    public static String getTypeName(Type type) {
        if (type instanceof Class) {
            return ((Class) type).getName();
        } else {
            if (null == type) {
                return null;
            }
            return type.getTypeName();
        }
    }

    public static Type getActualType(Field field) {
        Type type = field.getGenericType();
        if (isCollection(field.getType())
                || type.getTypeName().startsWith(IEnum.class.getName())
                || type.getTypeName().startsWith(BaseEnum.class.getName())) {
            try {
                ParameterizedType listGenericType = (ParameterizedType) type;
                Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                return listActualTypeArguments[0];
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Invalid parameterized type. class: " + field.getDeclaringClass().getName() + "; field = " + field.getName(), e);
            }
        } else if (type instanceof Class && ((Class<?>) type).isArray()) {
            return ((Class<?>) type).getComponentType();
        } else if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        }
        return type;
    }

    public static Type rawType(Type type) {
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getRawType();
        }
        return type;
    }

    public static Type getActualType(Type type) {
        Type actualType = getGenericType(type);
        if (null == actualType) {
            return type;
        }
        return actualType;
    }

    public static Type getGenericType(Type type) {
        if (null == type) {
            return null;
        }
        String typeName = type.getTypeName();
        if ((null != typeName && 1 != typeName.length())
                && (isCollection(type)
                || typeName.startsWith(IWrapper.class.getName())
                || typeName.startsWith(IEnum.class.getName())
                || typeName.startsWith(BaseEnum.class.getName()))) {
            if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
                int typeIndex = 0;
                if (typeName.startsWith(BaseEnum.class.getName())) {
                    typeIndex = 1;
                }
                ParameterizedType listGenericType = (ParameterizedType) type;
                Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                return listActualTypeArguments[typeIndex];
            } else {
                return null;
            }
        }
        return null;
    }

    public static String fixActualType(Type type) {
        if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
            return ((ParameterizedType) type).getRawType().getTypeName();
        } else if (type instanceof Class) {
            return ((Class<?>) type).getName();
        } else {
            return type.getTypeName();
        }
    }

    public static String[] fixActualTypes(Type[] types) {
        String[] actualTypes = new String[types.length];
        int i = 0;
        for (Type type : types) {
            actualTypes[i] = fixActualType(type);
            i++;
        }
        return actualTypes;
    }

    public static Type[] getActualTypes(Type[] types) {
        Type[] actualTypes = new Type[types.length];
        int i = 0;
        for (Type type : types) {
            Type actualType = getActualType(type);
            actualTypes[i] = actualType;
            i++;
        }
        return actualTypes;
    }

    public static Type[] getGenericTypes(Type[] types) {
        Type[] genericTypes = new Type[types.length];
        int i = 0;
        for (Type type : types) {
            Type genericType = getGenericType(type);
            genericTypes[i] = genericType;
            i++;
        }
        return genericTypes;
    }

    public static Type fixType(Type actualType) {
        if (null != actualType && isModelGeneric(actualType.getTypeName())) {
            return Object.class;
        } else {
            return actualType;
        }
    }

    public static String fixType(String actualType) {
        if (isModelGeneric(actualType)) {
            return Object.class.getName();
        } else {
            return actualType;
        }
    }

    public static Type[] getActualTypes(Field[] fields) {
        Type[] actualTypes = new Type[fields.length];
        int i = 0;
        for (Field field : fields) {
            actualTypes[i] = getActualType(field);
            i++;
        }
        return actualTypes;
    }

    public static boolean isModelGeneric(String typeName) {
        if (null == typeName) {
            return false;
        }
        return "T".equals(typeName) || "O".equals(typeName);
    }

    public static Type getEnumValueType(String ltype) {
        Class<?> enumClass;
        try {
            enumClass = TypeUtils.getClass(ltype);
        } catch (Exception e) {
            log.error("get class error. ltype: {}", ltype);
            throw PamirsException.construct(BASE_ENUMERATE_ERROR, e).errThrow();
        }
        try {
            Type genericClass;
            boolean isEnum = enumClass.isEnum();
            if (IEnum.class.isAssignableFrom(enumClass)) {
                if (isEnum) {
                    do {
                        Type[] interfaces = enumClass.getInterfaces();
                        Type[] genericInterfaces = enumClass.getGenericInterfaces();
                        int i = 0;
                        for (Type t : interfaces) {
                            if (IEnum.class.getName().equals(t.getTypeName())) {
                                return TypeUtils.getActualType(genericInterfaces[i]);
                            } else if (BitEnum.class.getName().equals(t.getTypeName())) {
                                return Long.class;
                            }
                            i++;
                        }
                        enumClass = enumClass.getSuperclass();
                    } while (!enumClass.isEnum());
                } else {
                    do {
                        genericClass = enumClass.getGenericSuperclass();
                        enumClass = enumClass.getSuperclass();
                        if (BaseEnum.class.getName().equals(enumClass.getName())) {
                            return TypeUtils.getActualType(genericClass);
                        }
                    } while (!BaseEnum.class.equals(enumClass));
                }
            }
        } catch (Exception e) {
            log.error("get enumeration value type error. ltype: {}", ltype);
            throw PamirsException.construct(BASE_ENUMERATE_ERROR, e).errThrow();
        }
        return null;
    }

    /**
     * 提取泛型模型,多泛型的时候请将泛型T放在第一位
     *
     * @param mapperClass mapper 接口
     * @return mapper 泛型
     */
    public static Class<?> extractModelClass(Class<?> mapperClass) {
        Type[] types = mapperClass.getGenericInterfaces();
        ParameterizedType target = null;
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                Type[] typeArray = ((ParameterizedType) type).getActualTypeArguments();
                if (ArrayUtils.isNotEmpty(typeArray)) {
                    for (Type t : typeArray) {
                        if (!(t instanceof TypeVariable) && !(t instanceof WildcardType)) {
                            target = (ParameterizedType) type;
                            break;
                        }
                        break;
                    }
                }
                break;
            }
        }
        return target == null ? null : (Class<?>) target.getActualTypeArguments()[0];
    }

    public static Type getGenericType(Field field) {
        Class<?> fieldType = field.getType();
        if (isCollection(fieldType) || fieldType.isArray()) {
            return getActualType(field);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public static Type getInterfaceGenericType(Class clazz) {
        Type[] actualTypeArguments = getInterfaceGenericTypes(clazz);
        if (null != actualTypeArguments && 0 != actualTypeArguments.length) {
            return actualTypeArguments[0];
        } else {
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    public static Type getSuperClassGenericType(Class clazz) {
        Type[] actualTypeArguments = getSuperClassGenericTypes(clazz);
        if (null != actualTypeArguments && 0 != actualTypeArguments.length) {
            return actualTypeArguments[0];
        } else {
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    public static Type[] getInterfaceGenericTypes(Class clazz) {
        clazz = ClassUtils.getUserClass(clazz);
        ParameterizedType genericType = (ParameterizedType) (0 != clazz.getGenericInterfaces().length ? clazz.getGenericInterfaces()[0] : null);
        if (null == genericType) {
            return null;
        }
        return genericType.getActualTypeArguments();
    }

    @SuppressWarnings("rawtypes")
    public static Type[] getEnumInterfaceGenericTypes(Class clazz) {
        Type iEnum = getIEnumType(clazz);
        ParameterizedType genericType = (ParameterizedType) iEnum;
        if (null == genericType) {
            return null;
        }
        return genericType.getActualTypeArguments();
    }

    @SuppressWarnings("rawtypes")
    private static Type getIEnumType(Class clazz) {
        Type iEnum = null;
        for (Type type : clazz.getGenericInterfaces()) {
            if (type.getTypeName().startsWith(IEnum.class.getName())) {
                iEnum = type;
            }
        }
        if (null == iEnum) {
            Class<?>[] iclasses = clazz.getInterfaces();
            if (ArrayUtils.isEmpty(iclasses)) {
                return null;
            }
            for (Class<?> iclazz : iclasses) {
                iEnum = getIEnumType(iclazz);
                if (null != iEnum) {
                    return iEnum;
                }
            }
        }
        return iEnum;
    }

    @SuppressWarnings("rawtypes")
    public static Type[] getSuperClassGenericTypes(Class clazz) {
        Class current = clazz;
        Class superClass = clazz.getSuperclass();
        while (!BaseEnum.class.equals(superClass)
                && !IEnum.class.equals(superClass)
                && null != superClass) {
            current = superClass;
            superClass = superClass.getSuperclass();
        }
        ParameterizedType genericType = (ParameterizedType) current.getGenericSuperclass();
        if (null == genericType) {
            return null;
        }
        return genericType.getActualTypeArguments();
    }

    /**
     * valueOfPrimary
     *
     * @param type       java类型
     * @param value      字符值
     * @param dateFormat 时间格式化
     * @return 对象
     */
    public static Object valueOfPrimary(String type, String value, String dateFormat) {
        if (null == value) {
            return null;
        }
        Long date = null;
        switch (type) {
            case "java.lang.String":
                return value;
            case "java.lang.Integer":
                return Integer.valueOf(value);
            case "java.lang.Short":
                return Short.valueOf(value);
            case "java.lang.Byte":
                return Byte.valueOf(value);
            case "java.lang.Long":
                if (StringUtils.isNotBlank(dateFormat) && !DateFormatEnum.TIMESTAMP.value().equals(dateFormat)) {
                    return DateUtils.formatDate(value, dateFormat).getTime();
                }
                return Long.valueOf(value);
            case "java.math.BigInteger":
                return new BigInteger(value);
            case "java.math.BigDecimal":
                return new BigDecimal(value);
            case "java.lang.Float":
                return Float.valueOf(value);
            case "java.lang.Double":
                return Double.valueOf(value);
            case "java.lang.Boolean":
                return Boolean.valueOf(value);
            case "java.util.Date":
                if (NumberUtils.isDigits(value) && (StringUtils.isBlank(dateFormat) || dateFormat.length() != value.length())) {
                    return DateUtils.formatDate(Long.valueOf(value));
                } else {
                    return DateUtils.formatDate(value, Optional.ofNullable(dateFormat).orElse(DateFormatEnum.DATETIME.value()));
                }
            case "java.sql.Timestamp":
                date = fixDate(type, value, dateFormat);
                return null == date ? null : new Timestamp(date);
            case "java.sql.Date":
                date = fixDate(type, value, dateFormat);
                return null == date ? null : new java.sql.Date(date);
            case "java.sql.Time":
                date = fixDate(type, value, dateFormat);
                return null == date ? null : new java.sql.Time(date);
            default:
                return null;
        }
    }

    private static Long fixDate(String type, String value, String dateFormat) {
        Long date = null;
        boolean needFix = false;
        if ("java.sql.Timestamp".equals(type)) {
            needFix = true;
            dateFormat = Optional.ofNullable(dateFormat).orElse(DateFormatEnum.DATETIME.value());
        } else if ("java.sql.Date".equals(type)) {
            needFix = true;
            dateFormat = Optional.ofNullable(dateFormat).orElse(DateFormatEnum.DATE.value());
        } else if ("java.sql.Time".equals(type)) {
            needFix = true;
            dateFormat = Optional.ofNullable(dateFormat).orElse(DateFormatEnum.TIME.value());
        }
        if (needFix) {
            if (NumberUtils.isDigits(value) && (StringUtils.isBlank(dateFormat) || dateFormat.length() != value.length())) {
                date = Long.parseLong(value);
            } else {
                date = Objects.requireNonNull(DateUtils.formatDate(value, dateFormat)).getTime();
            }
        }
        return date;
    }

    public static boolean isValidLtype(String type) {
        return isValidLtypeT(type) || "java.util.List".equals(type) || isValidArrayType(type);
    }

    public static boolean isValidArrayType(String type) {
        if (type.startsWith("[L")) {
            String genericType = type.substring(2, type.length() - 1);
            return isValidLtypeT(genericType);
        } else {
            return false;
        }
    }

    public static boolean isValidLtypeT(Type type) {
        return isValidLtypeT(type.getTypeName());
    }

    public static boolean isValidLtypeT(String type) {
        return isMap(type) || isValidValueLtype(type) || TypeUtils.isModelClass(type);
    }

    public static boolean isValidValueLtype(String type) {
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
            case "java.util.Date":
            case "java.sql.Timestamp":
            case "java.sql.Date":
            case "java.sql.Time":
            case "java.lang.Object":
                return true;
            default:
                return TypeUtils.isIEnumClass(type);
        }
    }

    public static boolean isValidValueActualLtype(Field field) {
        String type = TypeUtils.getActualType(field).getTypeName();
        return isValidValueLtype(type);
    }

    public static boolean isArrayType(String type) {
        return !StringUtils.isBlank(type) && type.startsWith("[L");
    }

    @SuppressWarnings("rawtypes")
    public static boolean isMap(String mapClazz) {
        if (isPrimitive(mapClazz)) {
            return false;
        }
        if (mapClazz.startsWith(Map.class.getName())) {
            return true;
        }
        Class clazz = getClass(mapClazz, false);
        return isMap(clazz);
    }

    @SuppressWarnings("rawtypes")
    public static boolean isMap(Class mapClazz) {
        return Map.class.isAssignableFrom(mapClazz);
    }

    public static boolean isMapOrMapList(Field field) {
        Class<?> fieldType = field.getType();
        if (isCollection(fieldType) || fieldType.isArray()) {
            return isMap(getActualType(field).getTypeName());
        } else {
            return isMap(fieldType);
        }
    }

    public static boolean isMapOrMapList(Class<?> clazz) {
        if (isCollection(clazz) || clazz.isArray()) {
            return isMap(getActualType(clazz).getTypeName());
        } else {
            return isMap(clazz);
        }
    }

    public static boolean isCollection(Object obj) {
        return null != obj && isCollection(obj.getClass());
    }

    @SuppressWarnings("rawtypes")
    public static boolean isCollection(Type collectionType) {
        if (collectionType instanceof Class) {
            return isCollection((Class) collectionType);
        } else {
            if (ParameterizedType.class.isAssignableFrom(collectionType.getClass())) {
                Type type = ((ParameterizedType) collectionType).getRawType();
                return isCollection(type);
            } else {
                String type = collectionType.getTypeName();
                return isCollection(type);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static boolean isCollection(String collectionClazz) {
        Class clazz = TypeUtils.getClass(collectionClazz);
        return isCollection(clazz);
    }

    @SuppressWarnings("rawtypes")
    public static boolean isCollection(Class collectionClazz) {
        return Collection.class.isAssignableFrom(collectionClazz);
    }

    @SuppressWarnings("rawtypes")
    public static boolean isModelClass(String ltype) {
        Class clazz = getClass(ltype);
        return isModelClass(clazz);
    }

    public static boolean isModelClass(Class<?> clazz) {
        return D.class.isAssignableFrom(clazz);
    }

    public static boolean isModelOrMapModelClass(String ltype) {
        Class<?> clazz = getClass(ltype);
        return isModelClass(clazz) || isDataMap(clazz);
    }

    public static boolean isModelOrMapModelClass(Field field) {
        Class<?> clazz = field.getType();
        if (isCollection(clazz) || clazz.isArray()) {
            clazz = getClass(getActualType(field).getTypeName());
        }
        return isModelClass(clazz) || isDataMap(clazz);
    }

    public static boolean isIEnumClass(String ltype) {
        try {
            if (isPrimitive(ltype)) {
                return false;
            }
            Class<?> clazz = TypeUtils.getClass(ltype);
            return isIEnumClass(clazz);
        } catch (Exception e) {
            log.error("{} 不支持的类型: {}", MetaExpEnumerate.BASE_ENUM_CLASS_IS_NOT_EXISTS_ERROR, ltype, e);
            return false;
        }
    }

    public static boolean isIEnumClass(Class<?> ltypeClazz) {
        return isIEnumClass(ltypeClazz, null);
    }

    public static boolean isIEnumClass(Class<?> ltypeClazz, String ltype) {
        if (null == ltype) {
            ltype = ltypeClazz.getName();
        }
        Boolean isIEnum = ClassCacheUtils.isIEnum(ltype);
        if (null == isIEnum) {
            isIEnum = IEnum.class.isAssignableFrom(ltypeClazz) || ltypeClazz.isEnum();
            //noinspection StringOperationCanBeSimplified
            ClassCacheUtils.putIsIEnum(new String(ltype), isIEnum);
        }
        return isIEnum;
    }

    public static boolean isEnumClass(String ltype) {
        if (null == ltype) {
            return false;
        }
        Boolean isEnum = ClassCacheUtils.isEnum(ltype);
        if (null == isEnum) {
            try {
                isEnum = TypeUtils.getClass(ltype).isEnum();
            } catch (PamirsException cnf) {
                // 假设为远程枚举类
                if (BASE_CLASS_IS_NOT_EXISTS_ERROR.code() == cnf.getCode()) {
                    isEnum = true;
                } else {
                    throw cnf;
                }
            }
            //noinspection StringOperationCanBeSimplified
            ClassCacheUtils.putIsEnum(new String(ltype), isEnum);
        }
        return isEnum;
    }

    public static boolean isComplexType(Class<?> clazz) {
        if (String.class.equals(clazz)
                || Primitives.isWrapperType(clazz)
                || Primitives.allPrimitiveTypes().contains(clazz)) {
            return Boolean.FALSE;
        }

        if (isCollection(clazz)) {
            return Boolean.FALSE;
        }

        if (isPamirsAdditionalBaseType(clazz)) {
            return Boolean.FALSE;
        }

        /* 这里意味着，认不出的对象类类型会被作为complex type */

        return Boolean.TRUE;
    }

    public static boolean isComplexTypeExceptMap(String ltype) {
        if (isMap(ltype)) {
            return false;
        }
        Class<?> clazz = getClass(ltype);
        return isComplexType(clazz);
    }

    private static boolean isPamirsAdditionalBaseType(Class<?> clazz) {
        return additionalTypeMap.containsValue(clazz);
    }

    private static boolean isPamirsAdditionalBaseType(String clazzString) {
        return additionalTypeMap.containsKey(clazzString);
    }

    public static boolean isDMap(String clazz) {
        return DMap.class.getName().equals(clazz);
    }

    public static boolean isDMap(Class<?> clazz) {
        return DMap.class.isAssignableFrom(clazz);
    }

    @SuppressWarnings("unused")
    public static boolean isDataMap(String clazz) {
        if (DataMap.class.getName().equals(clazz)) {
            return true;
        }
        Class<?> mapClazz = getClass(clazz);
        return isDataMap(mapClazz);
    }

    public static boolean isDataMap(Class<?> clazz) {
        return DataMap.class.isAssignableFrom(clazz);
    }

    public static boolean isComplexType(String clazz) {
        if (isPrimitive(clazz)) {
            return false;
        }
        if (isMap(clazz) || TypeUtils.isModelClass(clazz)) {
            return true;
        }
        Class<?> clazzInst = getClass(clazz);
        return isComplexType(clazzInst);
    }

    public static boolean isStringType(Class<?> clazz) {
        return java.lang.String.class.isAssignableFrom(clazz);
    }

    public static boolean isStringType(String clazzString) {
        return "java.lang.String".equals(clazzString);
    }

    public static boolean isBaseType(String clazzString) {
        if (String.class.getName().equals(clazzString)
                || isPrimitive(clazzString)
                || isPamirsAdditionalBaseType(clazzString)
        ) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static boolean isBaseTypeWithoutString(String clazzString) {
        if (isPrimitive(clazzString) || isPamirsAdditionalBaseType(clazzString)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @SuppressWarnings("rawtypes")
    public static boolean isBaseType(Class clazz) {
        if (String.class.equals(clazz)
                || Primitives.isWrapperType(clazz)
                || Primitives.allPrimitiveTypes().contains(clazz)
                || isPamirsAdditionalBaseType(clazz)
        ) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static boolean isBool(String type) {
        return Boolean.class.getName().equals(type) || "boolean".equals(type);
    }

    @SuppressWarnings("rawtypes")
    public static boolean isBool(Class type) {
        return Boolean.class.equals(type) || "boolean".equals(type.getTypeName());
    }

    private static final Map<String, Class<?>> primitiveTypeMap = new HashMap<>(16);
    private static final Map<String, Class<?>> wrapTypeMap = new HashMap<>(16);
    private static final Map<String, Class<?>> additionalTypeMap = new HashMap<>(16);

    static {
        primitiveTypeMap.put(Boolean.class.getName(), Boolean.class);
        primitiveTypeMap.put(Byte.class.getName(), Byte.class);
        primitiveTypeMap.put(Character.class.getName(), Character.class);
        primitiveTypeMap.put(Double.class.getName(), Double.class);
        primitiveTypeMap.put(Float.class.getName(), Float.class);
        primitiveTypeMap.put(Integer.class.getName(), Integer.class);
        primitiveTypeMap.put(Long.class.getName(), Long.class);
        primitiveTypeMap.put(Short.class.getName(), Short.class);
        primitiveTypeMap.put(Void.class.getName(), Void.class);
        wrapTypeMap.put("boolean", Primitives.unwrap(Boolean.class));
        wrapTypeMap.put("byte", Primitives.unwrap(Byte.class));
        wrapTypeMap.put("char", Primitives.unwrap(Character.class));
        wrapTypeMap.put("double", Primitives.unwrap(Double.class));
        wrapTypeMap.put("float", Primitives.unwrap(Float.class));
        wrapTypeMap.put("int", Primitives.unwrap(Integer.class));
        wrapTypeMap.put("long", Primitives.unwrap(Long.class));
        wrapTypeMap.put("short", Primitives.unwrap(Short.class));
        wrapTypeMap.put("void", Primitives.unwrap(Void.class));
        additionalTypeMap.put(java.util.Date.class.getName(), java.util.Date.class);
        additionalTypeMap.put(java.sql.Timestamp.class.getName(), java.sql.Timestamp.class);
        additionalTypeMap.put(java.sql.Date.class.getName(), java.sql.Date.class);
        additionalTypeMap.put(java.sql.Time.class.getName(), java.sql.Time.class);
        additionalTypeMap.put(java.math.BigDecimal.class.getName(), java.math.BigDecimal.class);
        additionalTypeMap.put(java.math.BigInteger.class.getName(), java.math.BigInteger.class);
    }

    public static boolean isPrimitive(String type) {
        return primitiveTypeMap.containsKey(type) || wrapTypeMap.containsKey(type);
    }

    public static Class<?> getPrimitiveType(String type) {
        Class<?> clazz = primitiveTypeMap.get(type);
        if (null == clazz) {
            return wrapTypeMap.get(type);
        }
        return clazz;
    }

    public static boolean isPrimitiveOrString(String type) {
        return primitiveTypeMap.containsKey(type) || wrapTypeMap.containsKey(type) || String.class.getName().equals(type);
    }

    public static Long createLong(Object o) {
        if (ObjectUtils.isEmpty(o)) {
            return null;
        }
        if (o instanceof String) {
            if (o.toString().trim().length() == 0) {
                return null;
            }
            try {
                return Long.valueOf((String) o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (o instanceof Long) {
            return (Long) o;
        } else {
            return Long.valueOf(o + "");
        }
        return null;
    }

    public static String toString(Long o) {
        if (null == o) {
            return null;
        }
        return o.toString();
    }

    public static <T extends Serializable> T deserialize(String data) {
        String type = StringUtils.substringBefore(data, CharacterConstants.SEPARATOR_OCTOTHORPE);
        String dataString = StringUtils.substringAfter(data, CharacterConstants.SEPARATOR_OCTOTHORPE);
        Class<T> clazz = getClass(type);
        return JsonUtils.parseObject(dataString, clazz);
    }

    public static <T extends Serializable> String serialize(T obj) {
        String type = obj.getClass().getName();
        String dataString = JsonUtils.toJSONString(obj);
        return type + CharacterConstants.SEPARATOR_OCTOTHORPE + dataString;
    }

}
