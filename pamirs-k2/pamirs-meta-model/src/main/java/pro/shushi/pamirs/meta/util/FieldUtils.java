package pro.shushi.pamirs.meta.util;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.util.UnsafeUtil;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_FETCH_REFERENCE_MODEL_ERROR;

/**
 * Java字段工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:20 下午
 */
public class FieldUtils {

    @Nullable
    public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, @Nullable Class<A> annotationType) {
        return AnnotationUtils.findAnnotation(annotatedElement, annotationType);
    }

    @Nullable
    public static Class<?> findAnnotationDeclaringClass(Class<? extends Annotation> annotationType, @Nullable Field field) {
        return field == null ? null : Optional.ofNullable((Field) MergedAnnotations.from(field, MergedAnnotations.SearchStrategy.SUPERCLASS)
                .get(annotationType, MergedAnnotation::isDirectlyPresent).getSource()).map(Field::getDeclaringClass).orElse(null);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean containsFieldValue(Object javaObject, String fieldName) {
        Map<String, Object> _dMap;
        if (javaObject instanceof Map) {
            _dMap = (Map) javaObject;
        } else if (D.class.isAssignableFrom(javaObject.getClass())) {
            _dMap = (Map<String, Object>) UnsafeUtil.getValue(javaObject, FieldConstants._dFieldName);
        } else {
            return false;
        }
        if (_dMap == null) {
            return false;
        }
        return _dMap.containsKey(fieldName);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object getFieldValue(Object javaObject, String fieldName) {
        Map<String, Object> _dMap;
        if (javaObject instanceof Map) {
            _dMap = (Map) javaObject;
        } else if (D.class.isAssignableFrom(javaObject.getClass())) {
            _dMap = ((D) javaObject).get_d();
        } else {
            return UnsafeUtil.getValue(javaObject, fieldName);
        }
        if (_dMap == null) {
            return null;
        }
        return _dMap.get(fieldName);
    }

    public static Object getFieldValues(List<Object> javaObjectList, String fieldName) {
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object setFieldValue(Object javaObject, String fieldName, Object value) {
        Map<String, Object> _dMap;
        if (javaObject instanceof Map) {
            _dMap = (Map) javaObject;
        } else if (D.class.isAssignableFrom(javaObject.getClass())) {
            _dMap = (Map<String, Object>) UnsafeUtil.getValue(javaObject, FieldConstants._dFieldName);
        } else {
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

    @SuppressWarnings("rawtypes")
    public static Object setDValue(Object javaObject, Map value) {
        UnsafeUtil.setValue(javaObject, FieldConstants._dFieldName, value);
        return javaObject;
    }

    public static Field getDeclaredField(Class<?> clazz, String fieldName) {

        List<Field> fields = new ArrayList<>();
        Class<?> tmpClazz = clazz;
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

    public static List<Field> getDeclaredFields(Object object) {
        return getDeclaredFieldsByClass(object.getClass());
    }

    public static List<Field> getDeclaredFieldsByClass(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        Set<String> fieldSet = new HashSet<>();
        Class<?> tempClass = clazz;
        while (tempClass != null) {
            for (Field field : tempClass.getDeclaredFields()) {
                if (!field.getName().contains(CharacterConstants.SEPARATOR_DOLLAR)
                        && !Modifier.isStatic(field.getModifiers()) && !fieldSet.contains(field.getName())) {
                    fieldList.add(field);
                    fieldSet.add(field.getName());
                }
            }
            tempClass = tempClass.getSuperclass();
        }
        return fieldList;
    }

    public static List<Field> getDeclaredFieldsByClassForMultipleInheritance(Class<?> clazz,
                                                                             Function<String, ModelDefinition> modelFetcher) {
        List<Field> fieldList = new ArrayList<>();
        Set<String> fieldSet = new HashSet<>();
        return getDeclaredFieldsByClassForMultipleInheritance(clazz, fieldSet, fieldList, modelFetcher);
    }

    private static List<Field> getDeclaredFieldsByClassForMultipleInheritance(Class<?> clazz,
                                                                              Set<String> fieldSet,
                                                                              List<Field> fieldList,
                                                                              Function<String, ModelDefinition> modelFetcher) {
        Class<?> tempClass = clazz;
        while (tempClass != null) {
            for (Field field : tempClass.getDeclaredFields()) {
                if (!field.getName().contains(CharacterConstants.SEPARATOR_DOLLAR)
                        && !Modifier.isStatic(field.getModifiers()) && !fieldSet.contains(field.getName())) {
                    fieldList.add(field);
                    fieldSet.add(field.getName());
                }
            }
            Model.Advanced modelAdvancedAnnotation = AnnotationUtils.getAnnotation(tempClass, Model.Advanced.class);
            Set<String> inherited = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::inherited)
                    .map(v -> new HashSet<>(Arrays.asList(v))).orElse(null);
            if (null != inherited) {
                for (String model : inherited) {
                    ModelDefinition modelDefinition = modelFetcher.apply(model);
                    String lname = modelDefinition.getLname();
                    Class<?> modelClass;
                    if (HashMap.class.getName().equals(lname)) {
                        continue;
                    } else {
                        modelClass = TypeUtils.getClass(lname);
                        if (tempClass.equals(modelClass) || !TypeUtils.isModelClass(modelClass)) {
                            continue;
                        }
                    }
                    getDeclaredFieldsByClassForMultipleInheritance(modelClass, fieldSet, fieldList, modelFetcher);
                }
            }
            tempClass = tempClass.getSuperclass();
        }
        return fieldList;
    }

    public static String getReferenceModel(String clazz) {
        try {
            Class<?> source = TypeUtils.getClass(clazz);
            Model.model modelModelAnnotation = AnnotationUtils.getAnnotation(source, Model.model.class);
            return Optional.ofNullable(modelModelAnnotation).map(Model.model::value).orElse(source.getName());
        } catch (Exception e) {
            throw PamirsException.construct(BASE_FETCH_REFERENCE_MODEL_ERROR, e).errThrow();
        }
    }

    public static boolean isConstantRelationFieldValue(String relationField) {
        return relationField.startsWith(CharacterConstants.SEPARATOR_OCTOTHORPE)
                && relationField.endsWith(CharacterConstants.SEPARATOR_OCTOTHORPE);
    }

    public static Object getRelationFieldValue(Object selfObj, String model, String relationField) {
        if (isConstantRelationFieldValue(relationField)) {
            return relationField.substring(1, relationField.length() - 1);
        } else {
            ModelFieldConfig relationFieldConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelField(model, relationField);
            return getFieldValue(selfObj, relationFieldConfig.getLname());
        }
    }

    public static Object getReferenceFieldValue(Object refObj, String references, String referenceField) {
        return Optional.ofNullable(PamirsSession.getContext())
                .map(v -> v.getModelField(references, referenceField))
                .map(f -> FieldUtils.getFieldValue(refObj, f.getLname()))
                .orElse(null);
    }

    public static void replaceModelField(ModelDefinition modelDefinition, ModelField modelField) {
        int existIndex = -1;
        int i = 0;
        for (ModelField m : modelDefinition.getModelFields()) {
            if (m.getField().equals(modelField.getField())) {
                existIndex = i;
            }
            i++;
        }
        if (-1 == existIndex) {
            modelDefinition.getModelFields().add(modelField);
        } else {
            modelDefinition.getModelFields().remove(existIndex);
            modelDefinition.getModelFields().add(existIndex, modelField);
        }
    }

    public static <T, R> ModelFieldConfig fetchModelField(Getter<T, R> getter) {
        Class<?> tClass = LambdaUtil.fetchClazz(getter);
        String model = Models.api().getModel(tClass);
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return PamirsSession.getContext().getModelFieldByFieldName(model, fieldName);
    }

    public static <T, R> String fetchFieldField(Getter<T, R> getter) {
        return Optional.ofNullable(fetchModelField(getter)).map(ModelFieldConfig::getField).orElse(null);
    }

    public static boolean isMulti(Field field, pro.shushi.pamirs.meta.annotation.Field fieldAnnotation) {
        boolean notMulti = TypeUtils.isMapOrMapList(field) && !TypeUtils.isModelOrMapModelClass(field);
        return !notMulti && (TypeUtils.isCollection(field.getType()) || field.getType().isArray() || fieldAnnotation.multi());
    }

}
