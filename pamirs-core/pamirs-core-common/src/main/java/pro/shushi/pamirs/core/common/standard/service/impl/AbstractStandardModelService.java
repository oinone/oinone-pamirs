package pro.shushi.pamirs.core.common.standard.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.enmu.CommonExpEnumerate;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 标准模型服务抽象实现
 *
 * @author Adamancy Zhang on 2021-05-18 11:09
 */
public abstract class AbstractStandardModelService<T extends BaseModel> implements StandardModelService<T> {

    @Override
    public T create(T data) {
        T createData = verificationAndSet(data, false);
        if (createData == null) {
            return null;
        }
        createData = realCreate(createData);
        afterProperties(createData, false);
        return createData;
    }

    /**
     * 原始创建方法（默认调用construct）
     *
     * @param data 数据
     * @return 数据
     */
    protected final T rawCreate(T data) {
        return Models.origin().createOne(data.construct());
    }

    public List<T> createBatch(List<T> list) {
        List<T> createList = new ArrayList<>(list.size());
        for (T item : list) {
            T createData = verificationAndSet(item, false);
            if (createData == null) {
                continue;
            }
            createList.add(createData);
        }
        if (createList.isEmpty()) {
            return createList;
        }
        createList = rawCreateBatch(createList);
        for (T item : createList) {
            afterProperties(item, false);
        }
        return createList;
    }

    protected final List<T> rawCreateBatch(List<T> list) {
        return Models.origin().createBatch(list);
    }


    @Override
    public T update(T data) {
        T updateData = verificationAndSet(data, true);
        if (updateData == null) {
            return null;
        }
        updateData = realUpdate(updateData);
        if (updateData == null) {
            return null;
        }
        afterProperties(data, true);
        return data;
    }

    @Override
    public Integer updateByWrapper(T data, LambdaUpdateWrapper<T> wrapper) {
        return Models.origin().updateByWrapper(data, wrapper);
    }

    /**
     * 使用主键或唯一键进行更新的原始方法
     *
     * @param data 数据
     * @return 数据
     */
    protected final T rawUpdate(T data) {
        Integer effectRow = FetchUtil.<T, Integer>consumerQueryWrapper(data, () -> null, wrapper -> Models.origin().updateByWrapper(data, wrapper));
        if (effectRow == null) {
            throw PamirsException.construct(CommonExpEnumerate.PK_UNIQUE_HAS_NULL).errThrow();
        }
        return data;
    }

    public Integer updateBatch(List<T> list) {
        List<T> updateList = new ArrayList<>(list.size());
        for (T item : list) {
            T updateData = verificationAndSet(item, true);
            if (updateData == null) {
                continue;
            }
            updateList.add(updateData);
        }
        if (updateList.isEmpty()) {
            return 0;
        }
        int effectRow = rawUpdateBatch(updateList);
        for (T item : updateList) {
            afterProperties(item, false);
        }
        return effectRow;
    }

    protected final int rawUpdateBatch(List<T> list) {
        return Models.origin().updateBatch(list);
    }

    /**
     * 创建或更新
     *
     * @param data 数据
     * @return 结果
     */
    @Override
    public T createOrUpdate(T data) {
        T origin = FetchUtil.fetchOneOfNullable(data);
        T finalData = createOrUpdateBefore(origin, data);
        if (finalData == null) {
            return null;
        }
        if (origin == null) {
            finalData = create(finalData);
        } else {
            FetchUtil.fillPkAndUniqueFields(finalData, origin);
            finalData = update(finalData);
        }
        return finalData;
    }

    @Override
    public List<T> delete(List<T> list) {
        list = deleteBeforeVerification(list);
        if (list.isEmpty()) {
            return list;
        }
        rawDelete(list);
        deleteAfterProperties(list);
        return list;
    }

    protected final List<T> rawDelete(List<T> list) {
        Models.origin().deleteByPks(list);
        return list;
    }


    @Override
    public T deleteOne(T data) {
        List<T> list = Collections.singletonList(data);
        list = deleteBeforeVerification(list);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        data = list.get(0);
        data = rawDeleteOne(data);
        if (data == null) {
            return null;
        }
        deleteAfterProperties(list);
        return data;
    }

    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<T> wrapper) {
        return Models.origin().deleteByWrapper(wrapper);
    }

    protected final T rawDeleteOne(T data) {
        Integer effectRow = FetchUtil.<T, Integer>consumerQueryWrapper(data, () -> null, wrapper -> Models.origin().deleteByWrapper(wrapper));
        if (effectRow == null) {
            throw PamirsException.construct(CommonExpEnumerate.DELETE_PK_UNIQUE_HAS_NULL).errThrow();
        }
        return data;
    }

    @Override
    public Pagination<T> queryPage(Pagination<T> page, LambdaQueryWrapper<T> queryWrapper) {
        return Models.origin().queryPage(page, queryWrapper);
    }

    @Override
    public T queryOne(T query) {
        return FetchUtil.fetchOne(query);
    }

    @Override
    public T queryOneByWrapper(LambdaQueryWrapper<T> queryWrapper) {
        return Models.origin().queryOneByWrapper(queryWrapper);
    }

    @Override
    public List<T> queryListByWrapper(LambdaQueryWrapper<T> queryWrapper) {
        return Models.origin().queryListByWrapper(queryWrapper);
    }

    @Override
    public Long count(LambdaQueryWrapper<T> queryWrapper) {
        return Models.origin().count(queryWrapper);
    }

    /**
     * 真实创建方法
     *
     * @param data 数据
     * @return 数据
     */
    protected T realCreate(T data) {
        return rawCreate(data);
    }

    /**
     * 真实更新方法
     *
     * @param data 数据
     * @return 数据
     */
    protected T realUpdate(T data) {
        return rawUpdate(data);
    }

    /**
     * 真实删除方法
     *
     * @param data 数据
     * @return 数据
     */
    protected T realDeleteOne(T data) {
        return rawDeleteOne(data);
    }

    /**
     * 创建或更新前校验
     *
     * @param data     数据
     * @param isUpdate 是否更新 {@link AbstractStandardModelService#create(T)} is false, {@link AbstractStandardModelService#update(T)} is true
     * @return 数据
     */
    protected T verificationAndSet(T data, boolean isUpdate) {
        return data;
    }

    /**
     * 创建或更新后配置
     *
     * @param data     数据
     * @param isUpdate 是否更新 {@link AbstractStandardModelService#create(T)} is false, {@link AbstractStandardModelService#update(T)} is true
     */
    protected void afterProperties(T data, boolean isUpdate) {
    }

    /**
     * 创建或更新前校验，仅在使用{@link AbstractStandardModelService#createOrUpdate(BaseModel)}方法时有效
     *
     * @param origin 原始数据
     * @param data   数据
     */
    protected T createOrUpdateBefore(T origin, T data) {
        return data;
    }

    /**
     * 删除前校验
     *
     * @param list 数据列表
     * @return 数据列表
     */
    protected List<T> deleteBeforeVerification(List<T> list) {
        return list;
    }

    /**
     * 删除后配置
     *
     * @param list 数据列表
     */
    protected void deleteAfterProperties(List<T> list) {
    }
}
