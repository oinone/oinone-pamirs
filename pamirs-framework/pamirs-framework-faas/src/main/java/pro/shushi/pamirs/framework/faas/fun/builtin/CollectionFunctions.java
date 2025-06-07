package pro.shushi.pamirs.framework.faas.fun.builtin;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum.COLLECTION;
import static pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum.JAVA;
import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.LOCAL;
import static pro.shushi.pamirs.meta.enmu.FunctionSceneEnum.EXPRESSION;


/**
 * 集合函数
 * <p>
 * 2020/6/4 2:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Fun(NamespaceConstants.expression)
public class CollectionFunctions {

    @Function.Advanced(
            displayName = "获取集合(或数组)元素", language = JAVA,
            builtin = true, category = COLLECTION
    )
    @Function.fun("LIST_GET")
    @Function(name = "LIST_GET", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LIST_GET(list,index)\n函数说明: 获取集合list中索引为数字index的元素"
    )
    public static Object listGet(List list, Integer index) {
        if (null == list) {
            return null;
        }
        if (null == index || index >= list.size() || index < 0) {
            return null;
        }
        return list.get(index);
    }

    public static Object listGet(Object[] array, Integer index) {
        if (null == array || 0 == array.length) {
            return null;
        }
        if (null == index || index >= array.length || index < 0) {
            return null;
        }
        return array[index];
    }

    @Function.Advanced(
            displayName = "判断集合(或数组)是否为空", language = JAVA,
            builtin = true, category = COLLECTION
    )
    @Function.fun("LIST_IS_EMPTY")
    @Function(name = "LIST_IS_EMPTY", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LIST_IS_EMPTY(list)\n函数说明: 传入一个对象集合，判断是否为空"
    )
    public static Boolean listIsEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static Boolean listIsEmpty(Object[] array) {
        return array == null || 0 == array.length;
    }

    @Function.Advanced(
            displayName = "判断集合(或数组)是否包含元素", language = JAVA,
            builtin = true, category = COLLECTION
    )
    @Function.fun("LIST_CONTAINS")
    @Function(name = "LIST_CONTAINS", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LIST_CONTAINS(list,item)\n函数说明: 判断集合list是否包含元素item"
    )
    public static Boolean listContains(List list, Object item) {
        if (null == list) {
            return false;
        }
        return list.contains(item);
    }

    public static Boolean listContains(Object[] array, Object item) {
        return ArrayUtils.contains(array, item);
    }

    @Function.Advanced(
            displayName = "将元素添加到集合(或数组)", language = JAVA,
            builtin = true, category = COLLECTION
    )
    @Function.fun("LIST_ADD")
    @Function(name = "LIST_ADD", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LIST_ADD(list,item)\n函数说明: 将元素item添加到集合list"
    )
    public static List listAdd(List list, Object item) {
        if (null == list) {
            return null;
        }
        list.add(item);
        return list;
    }

    public static Object[] listAdd(Object[] array, Object item) {
        return ArrayUtils.add(array, item);
    }

    @Function.Advanced(
            displayName = "将元素添加到集合(或数组)的指定位置", language = JAVA,
            builtin = true, category = COLLECTION
    )
    @Function.fun("LIST_ADD_BY_INDEX")
    @Function(name = "LIST_ADD_BY_INDEX", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LIST_ADD_BY_INDEX(list,index,item)\n函数说明: 将元素item添加到集合list的索引index处"
    )
    public static List listAddByIndex(List list, Integer index, Object item) {
        if (null == list) {
            return null;
        }
        if (null == index) {
            return list;
        }
        list.add(index, item);
        return list;
    }

    public static Object[] listAddByIndex(Object[] array, Integer index, Object item) {
        if (null == array) {
            return null;
        }
        if (null == index) {
            return array;
        }
        return ArrayUtils.add(array, index, item);
    }

    @Function.Advanced(
            displayName = "移除集合(或数组)中的元素", language = JAVA,
            builtin = true, category = COLLECTION
    )
    @Function.fun("LIST_REMOVE")
    @Function(name = "LIST_REMOVE", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LIST_REMOVE(list,item)\n函数说明: 从集合list中移除元素item"
    )
    public static List listRemove(List list, Object item) {
        if (null == list) {
            return null;
        }
        list.remove(item);
        return list;
    }

    public static Object[] listRemove(Object[] array, Integer index) {
        return ArrayUtils.remove(array, index);
    }

    @Function.Advanced(
            displayName = "获取集合(或数组)元素数量", language = JAVA,
            builtin = true, category = COLLECTION
    )
    @Function.fun("LIST_COUNT")
    @Function(name = "LIST_COUNT", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LIST_COUNT(list)\n函数说明: 传入一个对象集合，获取集合元素数量"
    )
    public static Integer listCount(List list) {
        if (null == list) {
            return 0;
        }
        return list.size();
    }

    public static Integer listCount(Object[] array) {
        if (null == array) {
            return 0;
        }
        return array.length;
    }

    @Function.Advanced(
            displayName = "获取集合中的所有id", language = JAVA,
            builtin = true, category = COLLECTION
    )
    @Function.fun("LIST_IDS")
    @Function(name = "LIST_IDS", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LIST_IDS(list)\n函数说明: 传入一个对象集合，获取集合中的所有ID组成的列表"
    )
    public static List<Long> listIds(List list) {
        return fetchListIds(list);
    }

    public static List<Long> fetchListIds(List list) {
        List<Long> ids = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            ids = (List<Long>) list.stream().map(_v -> FieldUtils.getFieldValue(_v, "id"))
                    .collect(Collectors.<Long>toList());
        }
        return ids;
    }

    public static Long[] listIds(Object[] array) {
        return fetchListIds(array);
    }

    public static Long[] fetchListIds(Object[] array) {
        if (ArrayUtils.isNotEmpty(array)) {
            Long[] ids = new Long[array.length];
            int index = 0;
            for (Object item : array) {
                ids[index] = (Long) FieldUtils.getFieldValue(item, "id");
                index++;
            }
            return ids;
        }
        return new Long[]{};
    }

    @Function.Advanced(
            displayName = "将对象集合转化为属性集合", language = JAVA,
            builtin = true, category = COLLECTION
    )
    @Function.fun("LIST_FIELD_VALUES")
    @Function(name = "LIST_FIELD_VALUES", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LIST_FIELD_VALUES(list,model,field)\n函数说明: 传入一个对象集合，该对象的模型和属性字段，返回属性值集合"
    )
    public static List listFieldValues(List list, String model, String field) {
        List fieldList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            ModelFieldConfig modelField = ObjectFunctions.getModelFieldConfig(model, field);
            fieldList = (List) list.stream().map(_v -> FieldUtils.getFieldValue(_v, modelField.getLname()))
                    .collect(Collectors.toList());
        }
        return fieldList;
    }

    public static Object[] listFieldValues(Object[] array, String model, String field) {
        if (ArrayUtils.isNotEmpty(array)) {
            Object[] fieldList = new Object[array.length];
            ModelFieldConfig modelField = ObjectFunctions.getModelFieldConfig(model, field);
            int index = 0;
            for (Object item : array) {
                fieldList[index] = FieldUtils.getFieldValue(item, modelField.getLname());
                index++;
            }
            return fieldList;
        }
        return new Object[]{};
    }

    @Function.Advanced(
            displayName = "判断对象集合(或数组)中属性值匹配情况", language = JAVA,
            builtin = true, category = COLLECTION
    )
    @Function.fun("LIST_FIELD_EQUALS")
    @Function(name = "LIST_FIELD_EQUALS", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LIST_FIELD_EQUALS(list,model,field,value)\n函数说明: 判断对象集合(或数组)中属性值匹配情况，返回布尔集合"
    )
    public static List<Boolean> listFieldEquals(List list, String model, String field, String value) {
        List<Boolean> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            ModelFieldConfig modelField = ObjectFunctions.getModelFieldConfig(model, field);
            resultList = (List) list.stream()
                    .map(_v -> ObjectUtils.equals(FieldUtils.getFieldValue(_v, modelField.getLname()), value))
                    .collect(Collectors.toList());
        }
        return resultList;
    }

    public static Object[] listFieldEquals(Object[] array, String model, String field, String value) {
        if (ArrayUtils.isNotEmpty(array)) {
            Object[] resultList = new Object[array.length];
            ModelFieldConfig modelField = ObjectFunctions.getModelFieldConfig(model, field);
            int index = 0;
            for (Object item : array) {
                resultList[index] = ObjectUtils.equals(FieldUtils.getFieldValue(item, modelField.getLname()), value);
                index++;
            }
            return resultList;
        }
        return new Object[]{};
    }

    @Function.Advanced(
            displayName = "判断对象集合(或数组)中属性值不匹配情况", language = JAVA,
            builtin = true, category = COLLECTION
    )
    @Function.fun("LIST_FIELD_NOT_EQUALS")
    @Function(name = "LIST_FIELD_NOT_EQUALS", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LIST_FIELD_NOT_EQUALS(list,model,field,value)\n函数说明: 判断对象集合(或数组)中属性值不匹配情况，返回布尔集合"
    )
    public static List<Boolean> listFieldNotEquals(List list, String model, String field, String value) {
        List<Boolean> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            ModelFieldConfig modelField = ObjectFunctions.getModelFieldConfig(model, field);
            resultList = (List) list.stream()
                    .map(_v -> !ObjectUtils.equals(FieldUtils.getFieldValue(_v, modelField.getLname()), value))
                    .collect(Collectors.toList());
        }
        return resultList;
    }

    public static Object[] listFieldNotEquals(Object[] array, String model, String field, String value) {
        if (ArrayUtils.isNotEmpty(array)) {
            Object[] resultList = new Object[array.length];
            ModelFieldConfig modelField = ObjectFunctions.getModelFieldConfig(model, field);
            int index = 0;
            for (Object item : array) {
                resultList[index] = !ObjectUtils.equals(FieldUtils.getFieldValue(item, modelField.getLname()), value);
                index++;
            }
            return resultList;
        }
        return new Object[]{};
    }

    @Function.Advanced(
            displayName = "判断对象集合(或数组)中属性值是否在指定集合(或数组)中", language = JAVA,
            builtin = true, category = COLLECTION
    )
    @Function.fun("LIST_FIELD_IN")
    @Function(name = "LIST_FIELD_IN", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LIST_FIELD_IN(list,model,field,list)\n函数说明: 判断对象集合(或数组)中属性值是否在指定集合(或数组)中，返回布尔集合"
    )
    public static List<Boolean> listFieldIn(List list, String model, String field, List values) {
        List<Boolean> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(values)) {
            return resultList;
        }
        if (CollectionUtils.isNotEmpty(list)) {
            ModelFieldConfig modelField = ObjectFunctions.getModelFieldConfig(model, field);
            resultList = (List) list.stream()
                    .map(_v -> values.contains(FieldUtils.getFieldValue(_v, modelField.getLname())))
                    .collect(Collectors.toList());
        }
        return resultList;
    }

    public static List<Boolean> listFieldIn(List list, String model, String field, Object[] values) {
        List<Boolean> resultList = new ArrayList<>();
        if (ArrayUtils.isEmpty(values)) {
            return resultList;
        }
        if (CollectionUtils.isNotEmpty(list)) {
            ModelFieldConfig modelField = ObjectFunctions.getModelFieldConfig(model, field);
            resultList = (List) list.stream()
                    .map(_v -> Arrays.stream(values).anyMatch(__v -> ObjectUtils.equals(__v, FieldUtils.getFieldValue(_v, modelField.getLname()))))
                    .collect(Collectors.toList());
        }
        return resultList;
    }

    public static Object[] listFieldIn(Object[] array, String model, String field, List values) {
        if (CollectionUtils.isEmpty(values)) {
            return new Object[]{};
        }
        if (ArrayUtils.isNotEmpty(array)) {
            Object[] resultList = new Object[array.length];
            ModelFieldConfig modelField = ObjectFunctions.getModelFieldConfig(model, field);
            int index = 0;
            for (Object item : array) {
                resultList[index] = values.contains(FieldUtils.getFieldValue(item, modelField.getLname()));
                index++;
            }
            return resultList;
        }
        return new Object[]{};
    }

    public static Object[] listFieldIn(Object[] array, String model, String field, Object[] values) {
        if (ArrayUtils.isEmpty(values)) {
            return new Object[]{};
        }
        if (ArrayUtils.isNotEmpty(array)) {
            Object[] resultList = new Object[array.length];
            ModelFieldConfig modelField = ObjectFunctions.getModelFieldConfig(model, field);
            int index = 0;
            for (Object item : array) {
                resultList[index] = Arrays.stream(values).anyMatch(v -> ObjectUtils.equals(v, FieldUtils.getFieldValue(item, modelField.getLname())));
                index++;
            }
            return resultList;
        }
        return new Object[]{};
    }

    @Function.Advanced(
            displayName = "判断对象集合(或数组)中属性值是否不在指定集合(或数组)中", language = JAVA,
            builtin = true, category = COLLECTION
    )
    @Function.fun("LIST_FIELD_NOT_IN")
    @Function(name = "LIST_FIELD_NOT_IN", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LIST_FIELD_NOT_IN(list,model,field,list)\n函数说明: 判断对象集合(或数组)中属性值是否不在指定集合(或数组)中，返回布尔集合"
    )
    public static List<Boolean> listFieldNotIn(List list, String model, String field, List values) {
        List<Boolean> resultList = new ArrayList<>();
        boolean notIn = CollectionUtils.isEmpty(values);
        if (CollectionUtils.isNotEmpty(list)) {
            ModelFieldConfig modelField = ObjectFunctions.getModelFieldConfig(model, field);
            resultList = (List) list.stream()
                    .map(_v -> notIn || !values.contains(FieldUtils.getFieldValue(_v, modelField.getLname())))
                    .collect(Collectors.toList());
        }
        return resultList;
    }

    public static List<Boolean> listFieldNotIn(List list, String model, String field, Object[] values) {
        List<Boolean> resultList = new ArrayList<>();
        boolean notIn = ArrayUtils.isEmpty(values);
        if (CollectionUtils.isNotEmpty(list)) {
            ModelFieldConfig modelField = ObjectFunctions.getModelFieldConfig(model, field);
            resultList = (List) list.stream()
                    .map(_v -> notIn || Arrays.stream(values).noneMatch(__v -> ObjectUtils.equals(__v, FieldUtils.getFieldValue(_v, modelField.getLname()))))
                    .collect(Collectors.toList());
        }
        return resultList;
    }

    public static Object[] listFieldNotIn(Object[] array, String model, String field, List values) {
        boolean notIn = CollectionUtils.isEmpty(values);
        if (ArrayUtils.isNotEmpty(array)) {
            Object[] resultList = new Object[array.length];
            ModelFieldConfig modelField = ObjectFunctions.getModelFieldConfig(model, field);
            int index = 0;
            for (Object item : array) {
                resultList[index] = notIn || !values.contains(FieldUtils.getFieldValue(item, modelField.getLname()));
                index++;
            }
            return resultList;
        }
        return new Object[]{};
    }

    public static Object[] listFieldNotIn(Object[] array, String model, String field, Object[] values) {
        boolean notIn = ArrayUtils.isEmpty(values);
        if (ArrayUtils.isNotEmpty(array)) {
            Object[] resultList = new Object[array.length];
            ModelFieldConfig modelField = ObjectFunctions.getModelFieldConfig(model, field);
            int index = 0;
            for (Object item : array) {
                resultList[index] = notIn || Arrays.stream(values).noneMatch(v -> ObjectUtils.equals(v, FieldUtils.getFieldValue(item, modelField.getLname())));
                index++;
            }
            return resultList;
        }
        return new Object[]{};
    }

    @Function.Advanced(
            displayName = "将一个布尔集合进行逻辑与运算", language = JAVA,
            builtin = true, category = COLLECTION
    )
    @Function.fun("LIST_AND")
    @Function(name = "LIST_AND", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LIST_AND(list)\n函数说明: 将一个布尔集合进行逻辑与运算，返回布尔值"
    )
    public static Boolean listAnd(List list) {
        if (null == list || 0 == list.size()) {
            return false;
        }
        Boolean result = true;
        for (Object obj : list) {
            result &= (Boolean) obj;
        }
        return result;
    }

    public static Boolean listAnd(Object[] array) {
        if (null == array || 0 == array.length) {
            return false;
        }
        Boolean result = true;
        for (Object obj : array) {
            result &= (Boolean) obj;
        }
        return result;
    }

    @Function.Advanced(
            displayName = "将一个布尔集合进行逻辑或运算", language = JAVA,
            builtin = true, category = COLLECTION
    )
    @Function.fun("LIST_OR")
    @Function(name = "LIST_OR", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LIST_OR(list)\n函数说明: 将一个布尔集合进行逻辑或运算，返回布尔值"
    )
    public static Boolean listOr(List list) {
        if (null == list || 0 == list.size()) {
            return false;
        }
        Boolean result = false;
        for (Object obj : list) {
            result |= (Boolean) obj;
        }
        return result;
    }

    public static Boolean listOr(Object[] array) {
        if (null == array || 0 == array.length) {
            return false;
        }
        Boolean result = false;
        for (Object obj : array) {
            result |= (Boolean) obj;
        }
        return result;
    }

    @Function.fun("CONCAT")
    @Function(name = "CONCAT", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CONCAT(list, split)\n函数说明: 将一个字符串集合使用分隔符进行连接，返回拼接后的字符串"
    )
    public static String concat(List<?> list, String split) {
        if (null == list || 0 == list.size()) {
            return CharacterConstants.SEPARATOR_EMPTY;
        }
        return list.stream().map(String::valueOf).collect(Collectors.joining(split));
    }

}
