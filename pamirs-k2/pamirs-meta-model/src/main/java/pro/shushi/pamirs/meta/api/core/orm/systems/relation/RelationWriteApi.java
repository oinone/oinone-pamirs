package pro.shushi.pamirs.meta.api.core.orm.systems.relation;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.util.List;

/**
 * 关联关系写数据管理
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
public interface RelationWriteApi extends CommonApi {

    /**
     * 创建或更新字段数据及关联关系（增量）
     *
     * @param relation 关联关系字段配置
     * @param data     数据对象
     */
    <T> void fieldSave(ModelFieldConfig relation, T data);

    /**
     * 创建或更新字段数据及关联关系（增量）
     *
     * @param model    模型编码
     * @param relation 关联关系字段编码
     * @param data     数据对象
     */
    <T> void fieldSave(String model, String relation, T data);

    /**
     * 批量创建或更新字段数据及关联关系（增量）
     *
     * @param relation 关联关系字段配置
     * @param dataList 数据对象
     */
    <T> void listFieldSave(ModelFieldConfig relation, List<T> dataList);

    /**
     * 批量创建或更新字段数据及关联关系（增量）
     *
     * @param model    模型编码
     * @param relation 关联关系字段编码
     * @param dataList 数据对象
     */
    <T> void listFieldSave(String model, String relation, List<T> dataList);

    /**
     * 删除关系（增量）
     *
     * @param relation 关联关系字段配置
     * @param data     数据对象列表
     */
    <T> void relationDelete(ModelFieldConfig relation, T data);

    /**
     * 删除关系（增量）
     *
     * @param model    模型编码
     * @param relation 关联关系字段编码
     * @param data     数据对象列表
     */
    <T> void relationDelete(String model, String relation, T data);

    /**
     * 批量删除关系（增量）
     *
     * @param relation 关联关系字段配置
     * @param dataList 数据对象列表
     */
    <T> void listRelationDelete(ModelFieldConfig relation, List<T> dataList);

    /**
     * 批量删除关系（增量）
     *
     * @param model    模型编码
     * @param relation 关联关系字段编码
     * @param dataList 数据对象列表
     */
    <T> void listRelationDelete(String model, String relation, List<T> dataList);

}
