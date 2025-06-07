package pro.shushi.pamirs.meta.base.manager.data;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.ReadApi;
import pro.shushi.pamirs.meta.api.core.orm.WriteApi;
import pro.shushi.pamirs.meta.api.core.orm.WriteWithFieldApi;
import pro.shushi.pamirs.meta.api.core.orm.clone.ReferenceUtils;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.base.AbstractModel;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.constant.FunctionConstants;

import java.util.List;

/**
 * 数据管理器
 * <p>
 * 2020/5/7 11:58 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class DataManager implements ReadApi, WriteApi, WriteWithFieldApi, FunctionConstants {

    protected String getModel(Object... modelObject) {
        for (Object obj : modelObject) {
            String model = Models.api().getDataModel(obj);
            if (StringUtils.isNotBlank(model)) {
                return model;
            }
        }
        return null;
    }

    public static DataManager getInstance() {
        return Models.data();
    }

    public static OriginDataManager origin() {
        return Models.origin();
    }

    @Override
    public <T> T createOne(T data) {
        origin().excludeList(data);
        T result = Fun.run(getModel(data), createOne, data);
        ReferenceUtils.deal(result, data);
        return data;
    }

    @Override
    public <T> Integer createOrUpdate(T data) {
        Result<T> result = createOrUpdateWithResult(data);
        return result.getEffectRows();
    }

    @Override
    public <T> Result<T> createOrUpdateWithResult(T data) {
        origin().excludeList(data);
        Result<T> result = Fun.run(getModel(data), createOrUpdateWithResult, data);
        ReferenceUtils.deal(result.getData(), data);
        return result;
    }

    @Override
    public <T> Integer updateByPk(T data) {
        origin().excludeList(data);
        return Fun.run(getModel(data), updateByPk, data);
    }

    @Override
    public <T> Integer updateByUniqueField(T data) {
        origin().excludeList(data);
        return Fun.run(getModel(data), updateByUniqueField, data);
    }

    @Override
    public <T> Integer updateByEntity(T data, T query) {
        origin().excludeList(data);
        origin().excludeList(query);
        return Fun.run(getModel(data), updateByEntity, data, query);
    }

    @Override
    public <T> Integer updateByWrapper(T data, IWrapper<T> updateWrapper) {
        origin().excludeList(data);
        return Fun.run(getModel(data), updateByWrapper, data, updateWrapper);
    }

    @Override
    public <T> List<T> createBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        List<T> result = Fun.run(getModel(dataList), createBatch, dataList);
        ReferenceUtils.dealList(result, dataList);
        return dataList;
    }

    @Override
    public <T> List<T> createBatchWithSize(List<T> dataList, Integer batchSize) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        List<T> result = Fun.run(getModel(dataList), createBatchWithSize, dataList, batchSize);
        ReferenceUtils.dealList(result, dataList);
        return dataList;
    }

    @Override
    public <T> Integer createOrUpdateBatch(List<T> dataList) {
        Result<List<T>> result = createOrUpdateBatchWithResult(dataList);
        return result.getEffectRows();
    }

    @Override
    public <T> Result<List<T>> createOrUpdateBatchWithResult(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return new Result<>();
        }
        Result<List<T>> result = Fun.run(getModel(dataList), createOrUpdateBatchWithResult, dataList);
        ReferenceUtils.dealList(result.getData(), dataList);
        return result;
    }

    @Override
    public <T> Integer createOrUpdateBatchWithSize(List<T> dataList, Integer batchSize) {
        Result<List<T>> result = createOrUpdateBatchWithSizeWithResult(dataList, batchSize);
        return result.getEffectRows();
    }

    @Override
    public <T> Result<List<T>> createOrUpdateBatchWithSizeWithResult(List<T> dataList, Integer batchSize) {
        if (CollectionUtils.isEmpty(dataList)) {
            return new Result<>();
        }
        Result<List<T>> result = Fun.run(getModel(dataList), createOrUpdateBatchWithSizeWithResult, dataList, batchSize);
        ReferenceUtils.dealList(result.getData(), dataList);
        return result;
    }

    @Override
    public <T> Integer updateBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return 0;
        }
        return Fun.run(getModel(dataList), updateBatch, dataList);
    }

    @Override
    public <T> Integer updateBatchWithSize(List<T> dataList, Integer batchSize) {
        if (CollectionUtils.isEmpty(dataList)) {
            return 0;
        }
        return Fun.run(getModel(dataList), updateBatchWithSize, dataList, batchSize);
    }

    @Override
    public <T> Boolean deleteByPk(T data) {
        origin().excludeList(data);
        return Fun.run(getModel(data), deleteByPk, data);
    }

    @Override
    public <T> Boolean deleteByPks(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return true;
        }
        return Fun.run(getModel(dataList), deleteByPks, dataList);
    }

    @Override
    public <T> Boolean deleteByUniqueField(T data) {
        origin().excludeList(data);
        return Fun.run(getModel(data), deleteByUniqueField, data);
    }

    @Override
    public <T> Boolean deleteByUniques(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return true;
        }
        return Fun.run(getModel(dataList), deleteByUniques, dataList);
    }

    @Override
    public <T> Integer deleteByEntity(T query) {
        origin().excludeList(query);
        return Fun.run(getModel(query), deleteByEntity, query);
    }

    @Override
    public <T> Integer deleteByWrapper(IWrapper<T> queryWrapper) {
        return Fun.run(getModel(queryWrapper), deleteByWrapper, queryWrapper);
    }

    @Override
    public <T> T queryByPk(T query) {
        origin().excludeList(query);
        return Fun.run(getModel(query), queryByPk, query);
    }

    @Override
    public <T> T queryOne(T query) {
        origin().excludeList(query);
        return Fun.run(getModel(query), queryByEntity, query);
    }

    @Override
    public <T> T queryOneByWrapper(IWrapper<T> queryWrapper) {
        return Fun.run(getModel(queryWrapper), queryByWrapper, queryWrapper);
    }

    @Override
    public <T> List<T> queryListByEntity(T query) {
        origin().excludeList(query);
        return Fun.run(getModel(query), queryListByEntity, query);
    }

    @Override
    public <T> List<T> queryListByEntityWithBatchSize(T query, Integer batchSize) {
        origin().excludeList(query);
        return Fun.run(getModel(query), queryListByEntityWithBatchSize, query, batchSize);
    }

    @Override
    public <T> List<T> queryListByWrapper(IWrapper<T> queryWrapper) {
        return Fun.run(getModel(queryWrapper), queryListByWrapper, queryWrapper);
    }

    @Override
    public <T> List<T> queryListByEntity(Pagination<T> page, T query) {
        origin().excludeList(query);
        return Fun.run(getModel(query), queryListByPage, page, query);
    }

    @Override
    public <T> List<T> queryListByWrapper(Pagination<T> page, IWrapper<T> queryWrapper) {
        return Fun.run(getModel(page, queryWrapper), queryListByPageAndWrapper, page, queryWrapper);
    }

    @Override
    public <T> Pagination<T> queryPage(Pagination<T> page, IWrapper<T> queryWrapper) {
        return Fun.run(getModel(page, queryWrapper), queryPage, page, queryWrapper);
    }

    @Override
    public <T> Long count(T query) {
        origin().excludeList(query);
        return Fun.run(getModel(query), count, query);
    }

    @Override
    public <T> Long count(IWrapper<T> queryWrapper) {
        return Fun.run(getModel(queryWrapper), countByWrapper, queryWrapper);
    }

    public <T, D extends AbstractModel> T fieldQuery(T data, Getter<D, ?> getter) {
        return origin().fieldQuery(data, getter);
    }

    public <T> T fieldQuery(T data, String fieldName) {
        return origin().fieldQuery(data, fieldName);
    }

    public <T, D extends AbstractModel> List<T> listFieldQuery(List<T> dataList, Getter<D, ?> getter) {
        return origin().listFieldQuery(dataList, getter);
    }

    public <T> List<T> listFieldQuery(List<T> dataList, String fieldName) {
        return origin().listFieldQuery(dataList, fieldName);
    }

    public <T, D extends AbstractModel> T fieldSave(T data, Getter<D, ?> getter) {
        return origin().fieldSave(data, getter);
    }

    public <T> T fieldSave(T data, String fieldName) {
        return origin().fieldSave(data, fieldName);
    }

    public <T, D extends AbstractModel> List<T> listFieldSave(List<T> dataList, Getter<D, ?> getter) {
        return origin().listFieldSave(dataList, getter);
    }

    public <T> List<T> listFieldSave(List<T> dataList, String fieldName) {
        return origin().listFieldSave(dataList, fieldName);
    }

    public <T, D extends AbstractModel> T relationDelete(T data, Getter<D, ?> getter) {
        return origin().relationDelete(data, getter);
    }

    public <T> T relationDelete(T data, String fieldName) {
        return origin().relationDelete(data, fieldName);
    }

    public <T, D extends AbstractModel> List<T> listRelationDelete(List<T> dataList, Getter<D, ?> getter) {
        return origin().listRelationDelete(dataList, getter);
    }

    public <T> List<T> listRelationDelete(List<T> dataList, String fieldName) {
        return origin().listRelationDelete(dataList, fieldName);
    }

    @Override
    public <T> T createWithField(T data) {
        origin().excludeList(data);
        T result = Models.modelDirective().clearAfterAll(() -> Fun.run(getModel(data), create, data), data);
        ReferenceUtils.deal(result, data);
        return data;
    }

    @Override
    public <T> List<T> createWithFieldBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        List<T> result = Models.modelDirective().clearAfterAll(() -> Fun.run(getModel(dataList), createWithFieldBatch, dataList), dataList);
        ReferenceUtils.dealList(result, dataList);
        return dataList;
    }

    @Override
    public <T> T updateWithField(T data) {
        origin().excludeList(data);
        T result = Models.modelDirective().clearAfterAll(() -> Fun.run(getModel(data), update, data), data);
        ReferenceUtils.deal(result, data);
        return data;
    }

    @Override
    public <T> List<T> updateWithFieldBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        List<T> result = Models.modelDirective().clearAfterAll(() -> Fun.run(getModel(dataList), updateWithFieldBatch, dataList), dataList);
        ReferenceUtils.dealList(result, dataList);
        return dataList;
    }

    @Override
    public <T> T createOrUpdateWithField(T data) {
        origin().excludeList(data);
        T result = Models.modelDirective().clearAfterAll(() -> Fun.run(getModel(data), createOrUpdateWithField, data), data);
        ReferenceUtils.deal(result, data);
        return data;
    }

    @Override
    public <T> List<T> createOrUpdateWithFieldBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        List<T> result = Models.modelDirective().clearAfterAll(() -> Fun.run(getModel(dataList), createOrUpdateWithFieldBatch, dataList), dataList);
        ReferenceUtils.dealList(result, dataList);
        return dataList;
    }

    @Override
    public <T> T deleteWithField(T data) {
        origin().excludeList(data);
        return Fun.run(getModel(data), delete, data);
    }

    @Override
    public <T> List<T> deleteWithFieldBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        return Fun.run(getModel(dataList), deleteWithFieldBatch, dataList);
    }

}
