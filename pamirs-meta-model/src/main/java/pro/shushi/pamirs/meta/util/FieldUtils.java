package pro.shushi.pamirs.meta.util;

import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.UnsafeUtil;
import pro.shushi.pamirs.meta.constant.FieldConstants;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Java字段工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:20 下午
 */
public class FieldUtils {

    public static Object getFieldValue(Object javaObject, String fieldName) {
        Map<String, Object> _dMap;
        if(javaObject instanceof Map){
            _dMap = (Map)javaObject;
        }else if(D.class.isAssignableFrom(javaObject.getClass())){
            _dMap = (Map<String, Object>) UnsafeUtil.getValue(javaObject, FieldConstants._dFieldName);
        }else{
            return UnsafeUtil.getValue(javaObject, fieldName);
        }
        if (_dMap == null) {
            return null;
        }
        return _dMap.get(fieldName);
    }

    public static Object setFieldValue(Object javaObject, String fieldName, Object value) {
        Map<String, Object> _dMap;
        if(javaObject instanceof Map) {
            _dMap = (Map) javaObject;
        }else if(D.class.isAssignableFrom(javaObject.getClass())){
            _dMap = (Map<String, Object>) UnsafeUtil.getValue(javaObject, FieldConstants._dFieldName);
        }else{
            UnsafeUtil.setValue(javaObject, fieldName, value);
            return javaObject;
        }
        if (_dMap == null) {
            _dMap = new HashMap<>();
            UnsafeUtil.setValue(javaObject, FieldConstants._dFieldName, _dMap);
        }
        _dMap.put(fieldName, value);
        return javaObject;
    }

    public static Object getDValue(Object javaObject) {
        return UnsafeUtil.getValue(javaObject, FieldConstants._dFieldName);
    }

    public static Object setDValue(Object javaObject, Map value) {
        UnsafeUtil.setValue(javaObject, FieldConstants._dFieldName, value);
        return javaObject;
    }

    public static Field getDeclaredField(Class clazz, String fieldName) {

        List<Field> fields = new ArrayList<>();
        Class tmpClazz = clazz;
        while (tmpClazz != null) {
            fields.addAll(Arrays.asList(tmpClazz.getDeclaredFields()));
            tmpClazz = tmpClazz.getSuperclass();
        }

        for (Field f : fields) {
            if (f.getName().equalsIgnoreCase(fieldName)) {
                return f;
            }
        }

        return null;
    }

    public static List<Field> getDeclaredFields(Object object){
        List<Field> fieldList = new ArrayList<>() ;
        Class tempClass = object.getClass();
        while (tempClass != null) {
            fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()).stream().filter(v->!v.getName().contains(CharacterConstants.SEPARATOR_DOLLAR)).filter(v-> !Modifier.isStatic(v.getModifiers())).collect(Collectors.toList()));
            tempClass = tempClass.getSuperclass();
        }
        return fieldList;
    }

    public static String getReferenceModel(String clazz){
        try {
            Class source = Class.forName(clazz);
            Model.model modelModelAnnotation = AnnotationUtils.getAnnotation(source, Model.model.class);
            return Optional.ofNullable(modelModelAnnotation).map(v->v.value()).orElse(source.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
