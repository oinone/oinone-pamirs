package pro.shushi.pamirs.framework.orm;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.common.utils.header.ExceptionHelper;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.PamirsMapperConfigurationProxy;
import pro.shushi.pamirs.meta.api.core.orm.WriteApi;
import pro.shushi.pamirs.meta.api.core.orm.convert.DataConverter;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.dto.entity.MapWrapper;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.domain.model.RelationDataModel;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 函数API实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@Fun(BaseModel.MODEL_MODEL)
@Component
public class DefaultWriteApi extends AbstractReadWriteApi implements WriteApi, FunctionConstants {

    @Resource
    private GenericMapper genericMapper;

    @Resource
    private DataConverter persistenceDataConverter;

    @Resource
    private PamirsMapperConfigurationProxy pamirsMapperConfigurationProxy;

    @Function.Advanced(displayName = "创建记录", managed = true)
    @Function.fun(createOne)
    @Function
    @Override
    public <T> T createOne(T data) {
        if (null == data) {
            return null;
        }
        String model = getModel(data);
        int count;
        try {
            count = genericMapper.insert(persistenceDataConverter.in(model, data));
        } catch (Throwable e) {
            if (ExceptionHelper.isDuplicateKeyException(e)) {
                throw PamirsException.construct(OrmExpEnumerate.BASE_DATA_DUPLICATION_ERROR, e).errThrow();
            }
            throw e;
        }

        if (1 != count) {
            throw PamirsException.construct(OrmExpEnumerate.BASE_DATA_INSERT_ERROR).errThrow();
        }
        return persistenceDataConverter.out(model, data);
    }

    @Function.Advanced(displayName = "创建或更新记录，返回记录数", managed = true)
    @Function.fun(createOrUpdate)
    @Function
    @Override
    public <T> Integer createOrUpdate(T data) {
        Result<T> result = createOrUpdateWithResult(data);
        return result.getEffectRows();
    }

    @Function.Advanced(displayName = "创建或更新记录，返回对象", managed = true)
    @Function.fun(createOrUpdateWithResult)
    @Function
    @Override
    public <T> Result<T> createOrUpdateWithResult(T data) {
        Result<T> result = new Result<>();
        String model = getModel(data);
        DataMap map = MapWrapper.wrap(persistenceDataConverter.in(model, data)).getDataMap();
        int count;
        if (!Models.compute().isPkValueValid(map)) {
            if (Models.compute().isUniqueKeyValueValid(map)) {
                DataMap existItem = genericMapper.selectOneByUniqueKey(map);
                if (null != existItem) {
                    ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
                    Models.compute().setPkIfPresent(modelConfig, map, existItem);
                    Models.compute().setCodeIfPresent(modelConfig, map, existItem);
                    count = genericMapper.updateByUniqueKey(map);
                } else {
                    count = genericMapper.insert(map);
                }
            } else {
                count = genericMapper.insert(map);
            }
        } else {
            DataMap existItem = genericMapper.selectByPk(map);
            if (null == existItem) {
                count = genericMapper.insert(map);
            } else {
                count = genericMapper.updateByPk(map);
            }
        }
        persistenceDataConverter.out(model, data);
        result.setEffectRows(count);
        result.setData(data);
        return result;
    }

    @Function.Advanced(displayName = "根据主键更新记录", managed = true)
    @Function.fun(updateByPk)
    @Function
    @Override
    public <T> Integer updateByPk(T data) {
        if (null == data) {
            return 0;
        }
        String model = getModel(data);
        int count;
        try {
            count = genericMapper.updateByPk(persistenceDataConverter.in(model, data));
        } catch (Throwable e) {
            if (ExceptionHelper.isDuplicateKeyException(e)) {
                throw PamirsException.construct(OrmExpEnumerate.BASE_DATA_DUPLICATION_ERROR, e).errThrow();
            }
            throw e;
        }

        persistenceDataConverter.out(model, data);
        return count;
    }

    @Function.Advanced(displayName = "根据唯一索引更新记录", managed = true)
    @Function.fun(updateByUniqueField)
    @Function
    @Override
    public <T> Integer updateByUniqueField(T data) {
        if (null == data) {
            return 0;
        }
        String model = getModel(data);
        int count = genericMapper.updateByUniqueKey(persistenceDataConverter.in(model, data));
        persistenceDataConverter.out(model, data);
        return count;
    }

    @Function.Advanced(displayName = "匹配条件更新记录", managed = true)
    @Function.fun(updateByEntity)
    @Function
    @Override
    public <T> Integer updateByEntity(T data, T query) {
        if (null == data) {
            return 0;
        }
        String model = getModel(data);
        DataMap updateEntity = MapWrapper.wrap(persistenceDataConverter.in(model, data)).getDataMap();
        DataMap queryEntity = MapWrapper.wrap(persistenceDataConverter.in(model, query)).getDataMap();
        int count = genericMapper.update(updateEntity, Pops.query(queryEntity).setModel(model));
        persistenceDataConverter.out(model, updateEntity);
        persistenceDataConverter.out(model, query);
        return count;
    }

    @Function.Advanced(displayName = "根据条件更新记录", managed = true)
    @Function.fun(updateByWrapper)
    @Function
    @Override
    public <T> Integer updateByWrapper(T data, IWrapper<T> updateWrapper) {
        if (null == data) {
            return 0;
        }
        String model = updateWrapper.getModel();
        DataMap updateEntity = MapWrapper.wrap(persistenceDataConverter.in(model, data)).getDataMap();
        DataMap queryEntity = MapWrapper.wrap(persistenceDataConverter.in(model, updateWrapper.getEntity())).getDataMap();
        int count = genericMapper.update(updateEntity, updateWrapper.generic(model, queryEntity));
        persistenceDataConverter.out(model, updateEntity);
        persistenceDataConverter.out(model, updateWrapper.getEntity());
        return count;
    }

    @Function.Advanced(displayName = "批量创建记录", managed = true)
    @Function.fun(createBatch)
    @Function
    @Override
    public <T> List<T> createBatch(List<T> dataList) {
        return createBatchWithSize(dataList, null);
    }

    @Function.Advanced(displayName = "批量创建记录(分批次创建)", managed = true)
    @Function.fun(createBatchWithSize)
    @Function
    @Override
    public <T> List<T> createBatchWithSize(List<T> dataList, Integer batchSize) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        String model = getModel(dataList);
        if (null == batchSize || batchSize == 0) {
            batchSize = fetchWriteBatchSize(model);
        }
        try {
            genericMapper.insertBatchWithSize(persistenceDataConverter.in(model, dataList), batchSize);
        } catch (Throwable e) {
            if (ExceptionHelper.isDuplicateKeyException(e)) {
                throw PamirsException.construct(OrmExpEnumerate.BASE_DATA_DUPLICATION_ERROR, e).errThrow();
            }
            throw e;
        }

        return persistenceDataConverter.out(model, dataList);
    }

    @Function.Advanced(displayName = "批量创建或更新记录", managed = true)
    @Function.fun(createOrUpdateBatch)
    @Function
    @Override
    public <T> Integer createOrUpdateBatch(List<T> dataList) {
        return createOrUpdateBatchWithSize(dataList, null);
    }

    @Function.Advanced(displayName = "批量创建或更新记录，返回对象列表", managed = true)
    @Function.fun(createOrUpdateBatchWithResult)
    @Function
    @Override
    public <T> Result<List<T>> createOrUpdateBatchWithResult(List<T> dataList) {
        return createOrUpdateBatchWithSizeWithResult(dataList, null);
    }

    @Function.Advanced(displayName = "批量创建或更新记录(分批次)，返回记录数", managed = true)
    @Function.fun(createOrUpdateBatchWithSize)
    @Function
    @Override
    public <T> Integer createOrUpdateBatchWithSize(List<T> dataList, Integer batchSize) {
        Result<List<T>> result = createOrUpdateBatchWithSizeWithResult(dataList, batchSize);
        return result.getEffectRows();
    }

    @Function.Advanced(displayName = "批量创建或更新记录(分批次)，返回对象列表", managed = true)
    @Function.fun(createOrUpdateBatchWithSizeWithResult)
    @Function
    @Override
    public <T> Result<List<T>> createOrUpdateBatchWithSizeWithResult(List<T> dataList, Integer batchSize) {
        if (CollectionUtils.isEmpty(dataList)) {
            return new Result<>();
        }
        String model = getModel(dataList);
        if (null == batchSize || batchSize == 0) {
            batchSize = fetchWriteBatchSize(model);
        }
        try {
            int count = genericMapper.insertOrUpdateBatchWithSize(persistenceDataConverter.in(model, dataList), batchSize);
            Result<List<T>> result = new Result<>();
            result.setEffectRows(count);
            result.setData(dataList);
            return result;
        } finally {
            persistenceDataConverter.out(model, dataList);
        }
    }

    @Function.Advanced(displayName = "批量更新记录", managed = true)
    @Function.fun(updateBatch)
    @Function
    @Override
    public <T> Integer updateBatch(List<T> dataList) {
        return updateBatchWithSize(dataList, null);
    }

    @Function.Advanced(displayName = "批量更新记录(分批次更新)", managed = true)
    @Function.fun(updateBatchWithSize)
    @Function
    @Override
    public <T> Integer updateBatchWithSize(List<T> dataList, Integer batchSize) {
        if (CollectionUtils.isEmpty(dataList)) {
            return 0;
        }
        String model = getModel(dataList);
        if (null == batchSize || batchSize == 0) {
            batchSize = fetchWriteBatchSize(model);
        }

        int count;
        try {
            // 根据值非空主键或者唯一索引批量更新列表
            count = genericMapper.updateBatchWithSize(persistenceDataConverter.in(model, dataList), batchSize);
        } catch (Throwable e) {
            if (ExceptionHelper.isDuplicateKeyException(e)) {
                throw PamirsException.construct(OrmExpEnumerate.BASE_DATA_DUPLICATION_ERROR, e).errThrow();
            }
            throw e;
        }

        persistenceDataConverter.out(model, dataList);
        return count;
    }

    @Function.Advanced(displayName = "根据主键删除记录", managed = true)
    @Function.fun(deleteByPk)
    @Function
    @Override
    public <T> Boolean deleteByPk(T data) {
        if (null == data) {
            return false;
        }
        String model = getModel(data);
        int count = genericMapper.deleteByPk(persistenceDataConverter.in(model, data));
        persistenceDataConverter.out(model, data);
        return 1 == count;
    }

    @Function.Advanced(displayName = "根据主键删除多条记录", managed = true)
    @Function.fun(deleteByPks)
    @Function
    @Override
    public <T> Boolean deleteByPks(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return false;
        }
        String model = getModel(dataList);
        int count = genericMapper.deleteByPks(persistenceDataConverter.in(model, dataList));
        persistenceDataConverter.out(model, dataList);
        return dataList.size() == count;
    }

    @Function.Advanced(displayName = "根据唯一索引删除记录", managed = true)
    @Function.fun(deleteByUniqueField)
    @Function
    @Override
    public <T> Boolean deleteByUniqueField(T data) {
        if (null == data) {
            return false;
        }
        String model = getModel(data);
        int count = genericMapper.deleteByUniqueKey(persistenceDataConverter.in(model, data));
        persistenceDataConverter.out(model, data);
        return 1 == count;
    }

    @Function.Advanced(displayName = "根据唯一索引删除多条记录", managed = true)
    @Function.fun(deleteByUniques)
    @Function
    @Override
    public <T> Boolean deleteByUniques(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return false;
        }
        String model = getModel(dataList);
        int count = genericMapper.deleteByUniqueKeys(persistenceDataConverter.in(model, dataList));
        persistenceDataConverter.out(model, dataList);
        return dataList.size() == count;
    }

    @Function.Advanced(displayName = "匹配条件删除记录", managed = true)
    @Function.fun(deleteByEntity)
    @Function
    @Override
    public <T> Integer deleteByEntity(T query) {
        if (null == query) {
            return 0;
        }
        String model = getModel(query);
        Map<String, Object> queryEntity = persistenceDataConverter.in(model, query);
        int count = genericMapper.delete(Pops.query(MapWrapper.wrap(queryEntity).getDataMap()));
        persistenceDataConverter.out(model, query);
        return count;
    }

    @Function.Advanced(displayName = "根据条件删除记录", managed = true)
    @Function.fun(deleteByWrapper)
    @Function
    @Override
    public <T> Integer deleteByWrapper(IWrapper<T> queryWrapper) {
        if (null == queryWrapper) {
            return 0;
        }
        String model = queryWrapper.getModel();
        Map<String, Object> queryEntity = persistenceDataConverter.in(model, queryWrapper.getEntity());
        int count = genericMapper.delete(queryWrapper.generic(model, MapWrapper.wrap(queryEntity).getDataMap()));
        persistenceDataConverter.out(model, queryWrapper.getEntity());
        return count;
    }

    @Function.Advanced(displayName = "更新自己及其关联数据", managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.API})
    public <T> T updateOneWithRelations(RelationDataModel data) {
        throw new RuntimeException("没有默认实现");
    }

    private Integer fetchWriteBatchSize(String model) {
        return pamirsMapperConfigurationProxy.batchOperationForModel(model).getWrite();
    }

}
