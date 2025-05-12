package pro.shushi.pamirs.meta.api.core.orm.systems.relation;

import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.util.List;

/**
 * 关系管理器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
public interface RelationManager {

    /**
     * 是否变更关系字段值
     *
     * @param fieldConfig 关联关系字段配置
     * @param newData     新模型数据
     * @param oldData     老模型数据
     * @return 是否变更
     */
    boolean isRelationFieldChange(ModelFieldConfig fieldConfig, Object newData, Object oldData);

    /**
     * 关系字段值与关联字段值是否相等
     *
     * @param fieldConfig 关联关系字段配置
     * @param data        模型数据
     * @return 是否变更
     */
    boolean isToOneRelationChange(ModelFieldConfig fieldConfig, Object data);

    /**
     * 判断关系字段值是否有效
     *
     * @param fieldConfig 字段配置
     * @param data        实体数据
     * @return 是否有效
     */
    boolean isRelationFieldValid(ModelFieldConfig fieldConfig, Object data);

    /**
     * 判断关联字段值是否有效
     *
     * @param fieldConfig 字段配置
     * @param data        实体数据
     * @return 是否有效
     */
    boolean isReferenceFieldValid(ModelFieldConfig fieldConfig, Object data);

    /**
     * 字段集合是否存在交集
     *
     * @param fields1 字段集合1
     * @param fields2 字段集合2
     * @return 是否存在交集
     */
    boolean isIntersectionOf(List<String> fields1, List<String> fields2);

    /**
     * 获取关系字段值列表
     *
     * @param fieldConfig 关联关系字段配置
     * @param data        模型数据
     * @return 关系字段值列表
     */
    List<Object> fetchRelationFieldValues(ModelFieldConfig fieldConfig, Object data);

    /**
     * 获取关联字段值列表
     *
     * @param fieldConfig 关联关系字段配置
     * @param data        模型数据
     * @return 关联字段值列表
     */
    List<Object> fetchToOneReferenceFieldValues(ModelFieldConfig fieldConfig, Object data);

    /**
     * 获取中间模型主键值列表
     *
     * @param data                   数据实体
     * @param through                中间模型模型编码
     * @param throughRelationFields  中间模型关系字段
     * @param throughReferenceFields 中间模型关联字段
     * @return 中间模型主键值列表
     */
    List<Object> fetchThroughKeyFieldValues(Object data, String through, List<String> throughRelationFields, List<String> throughReferenceFields);

    /**
     * 获取待删除关联数据
     *
     * @param modelConfig         模型配置
     * @param existRelationList   已持久化全量关联数据
     * @param currentRelationList 新全量关联数据
     * @return 待删除关联数据
     */
    List<Object> fetchDeleteRelationList(ModelConfig modelConfig, List<Object> existRelationList, List<Object> currentRelationList);

    /**
     * 根据关系获取中间模型数据列表
     *
     * @param fieldConfig 字段配置
     * @param data        数据实体
     * @param fieldValues 关联关系字段值列表
     * @return 中间模型数据列表
     */
    List<Object> fetchThroughListFromRelation(ModelFieldConfig fieldConfig, Object data, List<Object> fieldValues);

    /**
     * 将关系或者关联字段置空
     *
     * @param model         模型编码
     * @param setNullFields 置空字段
     * @param data          模型数据对象或map
     */
    void setNullForRelation(String model, List<String> setNullFields, Object data);

    /**
     * 填充目标对象中的关系字段值
     *
     * @param fieldConfig 字段配置
     * @param originData  源纪录
     * @param destData    目标记录
     */
    void fillRelationFieldValuesFromOther(ModelFieldConfig fieldConfig, Object originData, Object destData);

    /**
     * 填充多对一字段
     *
     * @param fieldConfig 多对一字段配置
     * @param data        模型数据对象或map
     */
    void fillManyToOneValueFromRelation(ModelFieldConfig fieldConfig, Object data);

    /**
     * 从关联关系字段填充关系字段值
     *
     * @param fieldConfig 关联关系字段配置
     * @param data        模型数据对象或map
     */
    void fillRelationFieldValueFromRelation(ModelFieldConfig fieldConfig, Object data);

    /**
     * 从关联关系字段填充关联字段值
     *
     * @param fieldConfig 关联关系字段配置
     * @param data        模型数据对象或map
     */
    void fillReferenceFieldValueFromRelation(ModelFieldConfig fieldConfig, Object data);

}
