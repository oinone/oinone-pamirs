package pro.shushi.pamirs.meta.util;

import com.google.common.primitives.Primitives;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.ObjectUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static pro.shushi.pamirs.meta.enumclass.ExpEnumerate.*;

/**
 *
 * 类型处理工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:20 下午
 */
@Slf4j
public class TypeUtils {

    public static Class<?> getClass(String type){
        if(StringUtils.isBlank(type) || isMap(type)){
            return Map.class;
        }
        try {
            return Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Type getType(Field field){
        return field.getType();
    }

    public static String getTypeName(Type type) {
        if(type instanceof Class){
            return ((Class)type).getName();
        }else{
            return type.getTypeName();
        }
    }

    public static Type getActualType(Field field){
        Type type = field.getGenericType();
        if(isCollection(field.getType())
                || type.getTypeName().startsWith(IEnum.class.getName())
                || type.getTypeName().startsWith(BaseEnum.class.getName())){
            ParameterizedType listGenericType = (ParameterizedType) field.getGenericType();
            Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
            return listActualTypeArguments[0];
        }
        return type;
    }

    public static Type getActualType(Type type){
        if(!"T".equals(type.getTypeName()) && (isCollection(type)
                || type.getTypeName().startsWith(IEnum.class.getName())
                || type.getTypeName().startsWith(BaseEnum.class.getName()))){
            if(ParameterizedType.class.isAssignableFrom(type.getClass())){
                ParameterizedType listGenericType = (ParameterizedType) type;
                Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                return listActualTypeArguments[0];
            }else{
                return type;
            }
        }
        return type;
    }

    public static Type[] getActualTypes(Type[] types){
        Type[] actualTypes = new Type[types.length];
        int i = 0;
        for(Type type : types){
            Type actualType = getActualType(type);
            if(actualType.getTypeName().equals("T")){
                actualTypes[i] = BaseModel.class;
            }
            i++;
        }
        return actualTypes;
    }

    public static Type[] getActualTypes(Field[] fields){
        Type[] actualTypes = new Type[fields.length];
        int i = 0;
        for(Field field : fields){
            actualTypes[i] = getActualType(field);
            i++;
        }
        return actualTypes;
    }

    public static Type getEnumValueType(String ltype){
        try {
            Class enumClass = Class.forName(ltype);
            Type genericClass;
            boolean isEnum = enumClass.isEnum();
            if(IEnum.class.isAssignableFrom(enumClass)){
                if(isEnum){
                    do{
                        Type[] interfaces = enumClass.getInterfaces();
                        Type[] genericInterfaces = enumClass.getGenericInterfaces();
                        int i = 0;
                        for(Type t : interfaces){
                            if(IEnum.class.getName().equals(t.getTypeName())){
                                return TypeUtils.getActualType(genericInterfaces[i]);
                            }
                            i++;
                        }
                        enumClass = enumClass.getSuperclass();
                    }while (!enumClass.isEnum());
                }else{
                    do{
                        genericClass = enumClass.getGenericSuperclass();
                        enumClass = enumClass.getSuperclass();
                        if(BaseEnum.class.getName().equals(enumClass.getName())){
                            return TypeUtils.getActualType(genericClass);
                        }
                    }while (!BaseEnum.class.getName().equals(enumClass));
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Type getGenericType(Field field){
        if(isCollection(field.getType())){
            return getActualType(field);
        }
        return null;
    }

    public static Type getInterfaceGenericType(Class clazz){
        Type[] actualTypeArguments = getInterfaceGenericTypes(clazz);
        if(null != actualTypeArguments && 0 != actualTypeArguments.length){
            return actualTypeArguments[0];
        }else{
            return null;
        }
    }

    public static Type[] getInterfaceGenericTypes(Class clazz){
        ParameterizedType genericType = (ParameterizedType) (0 != clazz.getGenericInterfaces().length?clazz.getGenericInterfaces()[0]:null);
        if(null == genericType){
            return null;
        }
        Type[] actualTypeArguments = genericType.getActualTypeArguments();
        return actualTypeArguments;
    }

    public static Object valueOf(String type, String value, String dateFormat){
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
            case "java.sql.Timestamp":
            case "java.sql.Time":
                if(NumberUtils.isDigits(value)){
                    return DateUtils.formatDate(Long.valueOf(value));
                }
                return DateUtils.formatDate(value, Optional.ofNullable(dateFormat).orElse(DateUtils.yyyyMMddHHmmss));
            default:
                try {
                    return BaseEnum.valueFor((Class<BaseEnum>)Class.forName(type), value);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    public static boolean isValidLtype(String type) {
        return isValidLtypeT(type) || "java.util.List".equals(type);
    }

    public static boolean isValidLtypeT(Type type){
        return isValidLtypeT(type.getTypeName());
    }

    public static boolean isValidLtypeT(String type){
        return isMap(type) || isValidValueLtype(type) || TypeUtils.isModelClass(type);
    }

    public static boolean isValidValueLtype(String type){
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
            case "java.sql.Time":
                return true;
            default:
                return TypeUtils.isEnumClass(type);
        }
    }

    public static boolean isMap(String mapClazz) {
        if(isPrimitive(mapClazz)){
            return false;
        }
        if("java.util.Map<java.lang.String,java.lang.Object>".equals(mapClazz.replace(CharacterConstants.SEPARATOR_BLANK, CharacterConstants.SEPARATOR_EMPTY))
                || "java.util.Map".equals(mapClazz)){
            return true;
        }
        Class clazz = null;
        try {
            clazz = Class.forName(mapClazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return isMap(clazz);
    }

    public static boolean isMap(Class mapClazz){
        return Map.class.isAssignableFrom(mapClazz);
    }

    public static boolean isCollection(Type collectionType) {
        if(collectionType instanceof Class){
            return isCollection((Class)collectionType);
        }else{
            if(ParameterizedType.class.isAssignableFrom(collectionType.getClass())){
                Type type = ((ParameterizedType)collectionType).getRawType();
                return isCollection(type);
            }else{
                String type = collectionType.getTypeName();
                return isCollection(type);
            }
        }
    }

    public static boolean isCollection(String collectionClazz) {
        try {
            collectionClazz = StringUtils.substringBefore(collectionClazz, "<");
            Class clazz = Class.forName(collectionClazz);
            return isCollection(clazz);
        } catch (ClassNotFoundException e) {
            log.error(BASE_CLASS_IS_NOT_EXISTS_ERROR, e).appendMsg("不支持的类型:" + collectionClazz);
            return false;
        }
    }

    public static boolean isCollection(Class collectionClazz){
        return Collection.class.isAssignableFrom(collectionClazz);
    }

    public static boolean isModelClass(String ltype){
        try {
            if(isPrimitive(ltype)){
                return false;
            }
            if(isMap(ltype)){
                return false;
            }
            Class clazz = Class.forName(ltype);
            if(D.class.isAssignableFrom(clazz)){
                return true;
            }else{
                return false;
            }
        } catch (ClassNotFoundException e) {
            log.error(BASE_MODEL_CLASS_IS_NOT_EXISTS_ERROR, e).appendMsg("不支持的类型:" + ltype);
            return false;
        }
    }

    public static boolean isEnumClass(String ltype){
        try {
            if(isPrimitive(ltype)){
                return false;
            }
            Class clazz = Class.forName(ltype);
            return isEnumClass(clazz);
        } catch (ClassNotFoundException e) {
            log.error(BASE_ENUM_CLASS_IS_NOT_EXISTS_ERROR, e).appendMsg("不支持的类型:" + ltype);
            return false;
        }
    }

    public static boolean isEnumClass(Class ltype){
        if(IEnum.class.isAssignableFrom(ltype)){
            return true;
        }else{
            return false;
        }
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

        if (java.util.Date.class.equals(clazz) || java.sql.Timestamp.class.equals(clazz) || java.sql.Time.class.equals(clazz) ) {
            return Boolean.FALSE;
        }

        /* 这里意味着，认不出的对象类类型会被作为complex type */

        return Boolean.TRUE;
    }

    public static boolean isComplexType(String clazz) {
        if(isPrimitive(clazz)){
            return false;
        }
        if(isMap(clazz) || TypeUtils.isModelClass(clazz)){
            return true;
        }
        Class<?> clazzInst;
        try {
            clazzInst = Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            throw log.error(BASE_FIELD_LTTYPE_CONFIG_ERROR, e, BASE_FIELD_LTTYPE_CONFIG_ERROR.msg(), e).appendMsg("不支持的类型:" + clazz).errThrow();
        }
        return isComplexType(clazzInst);
    }

    public static boolean isBaseType(Class clazz){
        if (String.class.equals(clazz)
                || Primitives.isWrapperType(clazz)
                || Primitives.allPrimitiveTypes().contains(clazz)
                || java.util.Date.class.equals(clazz)
        ) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static boolean isBool(String type){
        return Boolean.class.getName().equals(type) || "boolean".equals(type);
    }

    public static boolean isBool(Class type){
        return Boolean.class.equals(type) || "boolean".equals(type.getTypeName());
    }

    public static boolean isPrimitive(String type){
        for(Class v : Primitives.allPrimitiveTypes()){
            if(v.getName().equals(type)){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public static Class getPrimitiveType(String type){
        for(Class v : Primitives.allPrimitiveTypes()){
            if(v.getName().equals(type)){
                return v;
            }
        }
        return null;
    }

    public static Long createLong(Object o){
        if(ObjectUtils.isEmpty(o)){
            return null;
        }
        if(o instanceof String){
            if(o.toString().trim().length() == 0){
                return null;
            }
            try {
                return Long.valueOf((String) o);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(o instanceof Long){
            return (Long)o;
        }else{
            return Long.valueOf(o + "");
        }
        return null;
    }

}
