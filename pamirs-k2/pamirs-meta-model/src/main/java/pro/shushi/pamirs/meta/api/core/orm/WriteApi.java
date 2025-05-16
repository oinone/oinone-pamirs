package pro.shushi.pamirs.meta.api.core.orm;

import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;

/**
 * 模型数据库写API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface WriteApi {

    <T> T createOne(T data);

    <T> Integer createOrUpdate(T data);

    <T> Result<T> createOrUpdateWithResult(T data);

    <T> Integer updateByPk(T data);

    <T> Integer updateByUniqueField(T data);

    <T> Integer updateByEntity(T data, T query);

    <T> Integer updateByWrapper(T data, IWrapper<T> updateWrapper);

    <T> List<T> createBatch(List<T> dataList);

    <T> List<T> createBatchWithSize(List<T> dataList, Integer batchSize);

    <T> Integer createOrUpdateBatch(List<T> dataList);

    <T> Result<List<T>> createOrUpdateBatchWithResult(List<T> dataList);

    <T> Integer createOrUpdateBatchWithSize(List<T> dataList, Integer batchSize);

    <T> Result<List<T>> createOrUpdateBatchWithSizeWithResult(List<T> dataList, Integer batchSize);

    <T> Integer updateBatch(List<T> dataList);

    <T> Integer updateBatchWithSize(List<T> dataList, Integer batchSize);

    <T> Boolean deleteByPk(T data);

    <T> Boolean deleteByPks(List<T> dataList);

    <T> Boolean deleteByUniqueField(T data);

    <T> Boolean deleteByUniques(List<T> dataList);

    <T> Integer deleteByEntity(T query);

    <T> Integer deleteByWrapper(IWrapper<T> queryWrapper);

}
