package pro.shushi.pamirs.meta.api.core.orm;

import pro.shushi.pamirs.meta.api.CommonApi;

import java.util.List;

/**
 * 递归写数据管理
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
public interface WriteWithFieldApi extends CommonApi {

    /**
     * 新增本模型数据，递归处理（新增、更新）字段数据及关联关系
     *
     * @param data 实体数据
     * @param <T>  模型类型
     * @return 实体数据
     */
    <T> T createWithField(T data);

    /**
     * 批量新增本模型数据，递归处理（新增、更新）字段数据及关联关系
     *
     * @param dataList 实体数据列表
     * @param <T>      模型类型
     * @return 实体数据列表
     */
    <T> List<T> createWithFieldBatch(List<T> dataList);

    /**
     * 更新本模型数据，递归处理（新增、更新或删除）字段数据及关联关系
     *
     * @param data 实体数据
     * @param <T>  模型类型
     * @return 实体数据
     */
    <T> T updateWithField(T data);

    /**
     * 批量更新本模型数据，递归处理（新增、更新或删除）字段数据及关联关系
     *
     * @param dataList 实体数据列表
     * @param <T>      模型类型
     * @return 实体数据列表
     */
    <T> List<T> updateWithFieldBatch(List<T> dataList);

    /**
     * 新增或更新本模型数据，递归处理（新增、更新）字段数据及关联关系
     *
     * @param data 实体数据
     * @param <T>  模型类型
     * @return 实体数据
     */
    <T> T createOrUpdateWithField(T data);

    /**
     * 批量新增或更新本模型数据，递归处理（新增、更新或删除）字段数据及关联关系
     *
     * @param dataList 实体数据列表
     * @param <T>      模型类型
     * @return 实体数据列表
     */
    <T> List<T> createOrUpdateWithFieldBatch(List<T> dataList);

    /**
     * 删除本模型数据及关联关系
     *
     * @param data 实体数据
     * @param <T>  模型类型
     * @return 实体数据
     */
    <T> T deleteWithField(T data);

    /**
     * 批量删除本模型数据及关联关系
     *
     * @param dataList 实体数据列表
     * @param <T>      模型类型
     * @return 实体数据列表
     */
    <T> List<T> deleteWithFieldBatch(List<T> dataList);

}
