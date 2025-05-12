package pro.shushi.pamirs.meta.api.core.orm.systems;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.AbstractModel;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.*;

import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_EQUAL_PK_CONFIG_IS_NOT_EXISTS_ERROR;
import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_PK_CONFIG_IS_NOT_EXISTS_ERROR;

/**
 * 模型运算接口
 * <p>
 * 2020/6/30 2:55 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI
public interface ModelComputeApi {

    String VALUES_SEPARATOR = "#^#";

    /**
     * 根据唯一索引判断对象是否相同
     *
     * @param obj1 对象一
     * @param obj2 对象二
     * @return 是否相同
     */
    @SuppressWarnings("unused")
    default boolean equalsByUniqueKey(Object obj1, Object obj2) {
        String model = Models.api().getModel(obj1);
        ModelConfig modelConfig = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(model)).orElse(null);
        return equalsByUniqueKey(modelConfig, obj1, obj2);
    }

    /**
     * 主键值是否相等
     *
     * @param modelConfig 模型配置
     * @param obj1        模型数据1
     * @param obj2        模型数据2
     * @return 主键值是否相等
     */
    default boolean equalsByPks(ModelConfig modelConfig, Object obj1, Object obj2) {
        List<String> pks = modelConfig.getPk();
        if (CollectionUtils.isEmpty(pks)) {
            return false;
        }
        for (String pk : pks) {
            ModelFieldConfig pkFieldConfig = Optional.ofNullable(PamirsSession.getContext())
                    .map(v -> v.getModelField(modelConfig.getModel(), pk)).orElse(null);
            if (null == pkFieldConfig) {
                throw PamirsException.construct(BASE_EQUAL_PK_CONFIG_IS_NOT_EXISTS_ERROR).appendMsg("model:" + modelConfig.getModel() + ", pkField:" + pk).errThrow();
            }
            Object originPkValue = FieldUtils.getFieldValue(obj1, pkFieldConfig.getLname());
            Object destinationPkValue = FieldUtils.getFieldValue(obj2, pkFieldConfig.getLname());
            if (null == originPkValue || !originPkValue.equals(destinationPkValue)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 可以用于比较复合主键或者复合唯一索引是否相等
     *
     * @param key1 键值列表1
     * @param key2 键值列表2
     * @return 是否相等
     */
    default boolean equalsByKeys(List<Object> key1, List<Object> key2) {
        int i = 0;
        for (Object key : key1) {
            if (null == key || null == key2) {
                return false;
            }
            if (!key.equals(key2.get(i))) {
                return false;
            }
            i++;
        }
        return true;
    }

    /**
     * 将源对象的主键值设置到目标对象中
     *
     * @param dest   目标对象
     * @param origin 源对象
     */
    default void setPk(Object dest, Object origin) {
        String model = Models.api().getModel(dest);
        ModelConfig modelConfig = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(model)).orElse(null);
        setPk(modelConfig, dest, origin);
    }

    /**
     * 校验字段值是否存在，值可以为null
     *
     * @param data      数据
     * @param fieldName 属性名
     * @return 是否存在
     */
    default boolean hasFieldValue(Object data, String fieldName) {
        if (null == data) {
            return false;
        }
        if (Map.class.isAssignableFrom(data.getClass())) {
            //noinspection rawtypes
            return ((Map) data).containsKey(fieldName);
        } else if (D.class.isAssignableFrom(data.getClass())) {
            return ((D) data).get_d().containsKey(fieldName);
        }
        return false;
    }

    /**
     * 根据唯一索引判断对象是否包含至少一个非空唯一键值
     *
     * @param obj 对象
     * @return 是否包含至少一个非空唯一键值
     */
    default boolean isUniqueKeyValueValid(Object obj) {
        if (obj instanceof Collection) {
            //noinspection unchecked
            return isListUniqueKeyValueValid((List<Object>) obj);
        }
        String model = Models.api().getModel(obj);
        ModelConfig modelConfig = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(model)).orElse(null);
        if (null != modelConfig) {
            List<String> uniques = fetchUniqueKeyGroups(modelConfig);
            if (CollectionUtils.isEmpty(uniques)) {
                return false;
            }
            xxx:
            for (String uniqueString : uniques) {
                String[] uniqueNames = uniqueString.split(CharacterConstants.SEPARATOR_ESCAPE_COMMA);
                for (String uniqueName : uniqueNames) {
                    final String cleanUniqueName = uniqueName.trim();
                    Object v = FieldUtils.getFieldValue(obj, cleanUniqueName);
                    if (null == v) {
                        continue xxx;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 根据唯一索引判断列表中是否所有元素包含至少一个非空唯一键值
     *
     * @param list 列表
     * @return 所有元素是否包含至少一个非空唯一键值
     */
    default boolean isListUniqueKeyValueValid(List<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        boolean valid = true;
        for (Object obj : list) {
            valid = valid && isUniqueKeyValueValid(obj);
        }
        return valid;
    }

    /**
     * 根据唯一索引判断列表中是否所有元素包含至少一个非空唯一键值
     *
     * @param list 列表
     * @return 所有元素包含至少一个非空唯一键值
     */
    default String[] findFirstValidUniqueKey(List<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        String model = Models.api().getModel(list);
        ModelConfig modelConfig = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(model)).orElse(null);
        if (null != modelConfig) {
            List<String> uniques = fetchUniqueKeyGroups(modelConfig);
            List<String> pks = modelConfig.getPk();
            if (!CollectionUtils.isEmpty(pks)) {
                List<String> pkLnames = new ArrayList<>();
                for (String pkField : pks) {
                    ModelFieldConfig pkFieldConfig = Optional.ofNullable(PamirsSession.getContext())
                            .map(v -> v.getModelField(modelConfig.getModel(), pkField)).orElse(null);
                    if (null == pkFieldConfig) {
                        throw PamirsException.construct(BASE_PK_CONFIG_IS_NOT_EXISTS_ERROR).errThrow();
                    }
                    pkLnames.add(pkFieldConfig.getLname());
                }
                uniques.add(0, StringUtils.join(pkLnames, CharacterConstants.SEPARATOR_COMMA));
            }
            if (CollectionUtils.isEmpty(uniques)) {
                return null;
            }
            xxx:
            for (String uniqueString : uniques) {
                String[] uniqueNames = uniqueString.split(CharacterConstants.SEPARATOR_ESCAPE_COMMA);
                for (Object obj : list) {
                    for (String uniqueName : uniqueNames) {
                        final String cleanUniqueName = uniqueName.trim();
                        Object v = FieldUtils.getFieldValue(obj, cleanUniqueName);
                        if (null == v) {
                            continue xxx;
                        }
                    }
                }
                return uniqueNames;
            }
        }
        return null;
    }

    /**
     * 生成唯一索引的值字符串
     *
     * @param model        模型编码
     * @param uniqueFields 唯一索引
     * @param obj          对象
     * @return 唯一索引的值字符串
     */
    default String/*value1#value2#...*/ generateValidUniqueFieldValue(String model, String[] uniqueFields, Object obj) {
        List<Object> values = new ArrayList<>();
        for (String uniqueField : uniqueFields) {
            String uniqueLName = Optional.ofNullable(PamirsSession.getContext())
                    .map(v -> v.getModelField(model, uniqueField)).map(ModelFieldConfig::getLname).orElse(null);
            Object v = FieldUtils.getFieldValue(obj, uniqueLName);
            values.add(v);
        }
        return StringUtils.join(values, VALUES_SEPARATOR);
    }

    /**
     * 生成唯一索引的值字符串
     *
     * @param uniqueNames 唯一索引
     * @param obj         对象
     * @return 唯一索引的值字符串
     */
    default String/*value1#value2#...*/ generateValidUniqueKeyValue(String[] uniqueNames, Object obj) {
        List<Object> values = new ArrayList<>();
        for (String uniqueName : uniqueNames) {
            Object v = FieldUtils.getFieldValue(obj, uniqueName);
            values.add(v);
        }
        return StringUtils.join(values, VALUES_SEPARATOR);
    }

    /**
     * 生成主键的值字符串
     *
     * @param model 模型编码
     * @param obj   对象
     * @return 唯一索引的值字符串
     */
    default String/*value1#value2#...*/ generateValidPksValue(String model, Object obj) {
        List<String> pks = Optional.ofNullable(PamirsSession.getContext())
                .map(v -> v.getModelConfig(model)).map(ModelConfig::getPk).orElse(null);
        if (CollectionUtils.isEmpty(pks)) {
            return null;
        }
        return generateValidUniqueFieldValue(model, ListUtils.toArray(pks), obj);
    }

    /**
     * 根据主键或唯一索引判断列表中是否所有元素包含至少一个非空唯一键值
     *
     * @param list 列表
     * @return 所有元素是否包含至少一个非空唯一键值
     */
    default boolean isListPkOrUniqueKeyValueValid(List<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        boolean valid = true;
        for (Object obj : list) {
            valid = valid && (isUniqueKeyValueValid(obj) || isPkValueValid(obj));
        }
        return valid;
    }

    /**
     * 根据唯一索引判断对象主键值非空
     *
     * @param obj 对象
     * @return 对象主键值非空
     */
    default boolean isPkValueValid(Object obj) {
        if (obj instanceof Collection) {
            //noinspection unchecked
            return isListPkValueValid((List<Object>) obj);
        }
        String model = Models.api().getModel(obj);
        ModelConfig modelConfig = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(model)).orElse(null);
        if (null != modelConfig) {
            List<String> pks = modelConfig.getPk();
            if (CollectionUtils.isEmpty(pks)) {
                return false;
            }
            for (String pk : pks) {
                final String cleanPk = pk.trim();
                Object pkValue = FieldUtils.getFieldValue(obj, Optional.ofNullable(PamirsSession.getContext())
                        .map(v -> v.getModelField(modelConfig.getModel(), cleanPk))
                        .map(ModelFieldConfig::getLname).orElseThrow(() -> new RuntimeException("model: " + modelConfig.getModel() + ", no pk config")));
                if (null == pkValue) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 根据唯一索引判断列表中是否所有元素主键值非空
     *
     * @param list 列表
     * @return 所有元素是否主键值非空
     */
    default boolean isListPkValueValid(List<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        boolean valid = true;
        for (Object obj : list) {
            valid = valid && isPkValueValid(obj);
        }
        return valid;
    }

    /**
     * 根据唯一索引判断对象是否相同
     *
     * @param obj1 对象一
     * @param obj2 对象二
     * @return 是否相同
     */
    default boolean equalsByUniqueKey(ModelConfig modelConfig, Object obj1, Object obj2) {
        if (null != modelConfig) {
            List<String> uniques = fetchUniqueKeyGroups(modelConfig);
            if (CollectionUtils.isEmpty(uniques)) {
                return false;
            }
            xxx:
            for (String uniqueString : uniques) {
                String[] uniqueNames = uniqueString.split(CharacterConstants.SEPARATOR_ESCAPE_COMMA);
                for (String uniqueName : uniqueNames) {
                    final String cleanUniqueName = uniqueName.trim();
                    Object v1 = FieldUtils.getFieldValue(obj1, cleanUniqueName);
                    Object v2 = FieldUtils.getFieldValue(obj2, cleanUniqueName);
                    if (null == v1 || !v1.equals(v2)) {
                        continue xxx;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 获取模型的所有唯一索引，复合索引不会拆开成独立的item
     *
     * @param modelConfig 模型配置
     * @return 模型的所有唯一索引
     */
    default List<String> fetchUniqueKeyGroups(ModelConfig modelConfig) {
        Set<String> allUniqueKeys = new LinkedHashSet<>();
        if (null != modelConfig) {
            List<String> uniques = modelConfig.getUniques();
            if (!CollectionUtils.isEmpty(uniques)) {
                for (String uniqueString : uniques) {
                    String[] uniqueFields = uniqueString.split(CharacterConstants.SEPARATOR_ESCAPE_COMMA);
                    List<String> uniqueLnames = new ArrayList<>();
                    for (String uniqueField : uniqueFields) {
                        final String cleanUniqueField = uniqueField.trim();
                        uniqueLnames.add(Optional.ofNullable(PamirsSession.getContext())
                                .map(v -> v.getModelField(modelConfig.getModel(), cleanUniqueField))
                                .map(ModelFieldConfig::getLname).orElseThrow(() -> new RuntimeException("model: " + modelConfig.getModel() + ", no unique key config")));
                    }
                    allUniqueKeys.add(String.join(CharacterConstants.SEPARATOR_COMMA, uniqueLnames));
                }
            }
            modelConfig.getModelFieldConfigList().stream()
                    .filter(v -> null != v.getUnique() && v.getUnique()).forEach(v -> allUniqueKeys.add(v.getLname()));
        }
        return ListUtils.toList(allUniqueKeys);
    }

    /**
     * 获取模型的所有唯一索引，复合索引拆开成独立的item
     *
     * @param modelConfig 模型配置
     * @return 模型的所有唯一索引
     */
    default Set<String> fetchAllUniqueKeys(ModelConfig modelConfig) {
        Set<String> allUniqueKeys = new HashSet<>();
        if (null != modelConfig) {
            List<String> uniques = modelConfig.getUniques();
            if (!CollectionUtils.isEmpty(uniques)) {
                for (String uniqueString : uniques) {
                    String[] uniqueFields = uniqueString.split(CharacterConstants.SEPARATOR_ESCAPE_COMMA);
                    for (String uniqueField : uniqueFields) {
                        final String cleanUniqueField = uniqueField.trim();
                        Optional.ofNullable(PamirsSession.getContext())
                                .map(v -> v.getModelField(modelConfig.getModel(), cleanUniqueField))
                                .ifPresent(modelFieldConfig -> allUniqueKeys.add(modelFieldConfig.getLname()));
                    }
                }
            }
            modelConfig.getModelFieldConfigList().stream()
                    .filter(v -> null != v.getUnique() && v.getUnique()).forEach(v -> allUniqueKeys.add(v.getLname()));
        }
        return allUniqueKeys;
    }

    /**
     * 将源对象的主键值设置到目标对象中
     *
     * @param dest   目标对象
     * @param origin 源对象
     */
    default void setPk(ModelConfig modelConfig, Object dest, Object origin) {
        if (null != modelConfig && modelConfig.havePk()) {
            List<String> pks = modelConfig.getPkProperties();
            for (String pk : pks) {
                FieldUtils.setFieldValue(dest, pk, FieldUtils.getFieldValue(origin, pk));
            }
        }
    }

    /**
     * 如果源对象主键为空，将源对象的主键值设置到目标对象中
     *
     * @param dest   目标对象
     * @param origin 源对象
     */
    default void setPkIfPresent(ModelConfig modelConfig, Object dest, Object origin) {
        if (null != modelConfig && modelConfig.havePk()) {
            List<String> pks = modelConfig.getPkProperties();
            for (String pk : pks) {
                if (null == FieldUtils.getFieldValue(dest, pk)) {
                    FieldUtils.setFieldValue(dest, pk, FieldUtils.getFieldValue(origin, pk));
                }
            }
        }
    }

    /**
     * 如果源对象自动生成编码为空，将源对象的自动生成编码设置到目标对象中
     *
     * @param dest   目标对象
     * @param origin 源对象
     */
    default void setCodeIfPresent(ModelConfig modelConfig, Object dest, Object origin) {
        if (null != modelConfig) {
            SequenceConfig sequenceConfig = modelConfig.getSequenceConfig();
            if (null != sequenceConfig && null == FieldUtils.getFieldValue(dest, FieldConstants.CODE)) {
                FieldUtils.setFieldValue(dest, FieldConstants.CODE, FieldUtils.getFieldValue(origin, FieldConstants.CODE));
            }
            if (null != modelConfig.getModelFieldConfigList()) {
                for (ModelFieldConfig modelFieldConfig : modelConfig.getModelFieldConfigList()) {
                    sequenceConfig = modelFieldConfig.getSequenceConfig();
                    String fieldName = modelFieldConfig.getLname();
                    if (null != sequenceConfig && null == FieldUtils.getFieldValue(dest, fieldName)) {
                        FieldUtils.setFieldValue(dest, fieldName, FieldUtils.getFieldValue(origin, fieldName));
                    }
                }
            }
        }
    }

    /**
     * 模型数据有且仅有指定字段列表非空
     *
     * @param model        模型编码
     * @param fields       字段列表
     * @param ignoreFields 忽略字段列表
     * @param data         模型数据
     * @return 满足条件
     */
    default boolean isOnlyNonEmptyFields(String model, List<String> fields, List<String> ignoreFields, Object data) {
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(model);
        for (ModelFieldConfig modelFieldConfig : modelConfig.getModelFieldConfigList()) {
            if (null != FieldUtils.getFieldValue(data, modelFieldConfig.getLname())) {
                if (!fields.contains(modelFieldConfig.getField())) {
                    return false;
                }
            } else {
                if (fields.contains(modelFieldConfig.getField())
                        && null != ignoreFields && !ignoreFields.contains(modelFieldConfig.getField())) {
                    return false;
                }
            }
        }
        return true;
    }

    default boolean hasField(Object data, String fieldName) {
        if (null == data) {
            return false;
        }
        if (Map.class.isAssignableFrom(data.getClass())) {
            //noinspection rawtypes
            return ((Map) data).containsKey(fieldName);
        } else if (AbstractModel.class.isAssignableFrom(data.getClass())) {
            return ((AbstractModel) data).get_d().containsKey(fieldName);
        }
        return false;
    }

    default ModelConfig convert(ModelDefinition modelDefinition) {
        if (null == modelDefinition) {
            return null;
        }
        return new ModelConfig(modelDefinition);
    }

    default ModelFieldConfig convert(ModelField modelField) {
        if (null == modelField) {
            return null;
        }
        return new ModelFieldConfig(modelField);
    }

    default <T> T copyFieldValues(Object source, T destination, String model, List<String> fields) {
        for (String field : fields) {
            if (FieldUtils.isConstantRelationFieldValue(field)) {
                continue;
            }
            String name = PamirsSession.getContext().getModelField(model, field).getLname();
            Object value = FieldUtils.getFieldValue(source, name);
            FieldUtils.setFieldValue(destination, name, value);
        }
        return destination;
    }

    default <T> T copyFieldValues(Object source, T destination, List<String> fieldNames) {
        for (String name : fieldNames) {
            Object value = FieldUtils.getFieldValue(source, name);
            FieldUtils.setFieldValue(destination, name, value);
        }
        return destination;
    }

}
