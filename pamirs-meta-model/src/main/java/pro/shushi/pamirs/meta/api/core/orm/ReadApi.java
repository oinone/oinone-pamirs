package pro.shushi.pamirs.meta.api.core.orm;

import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;

/**
 * 模型数据库读API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface ReadApi {

    <T> T queryByPk(T query);

    <T> T queryOne(T query);

    <T> T queryOneByWrapper(IWrapper<T> queryWrapper);

    <T> List<T> queryListByEntity(T query);

    <T> List<T> queryListByEntityWithBatchSize(T query, Integer batchSize);

    <T> List<T> queryListByWrapper(IWrapper<T> queryWrapper);

    <T> List<T> queryListByEntity(Pagination<T> page, T query);

    <T> List<T> queryListByWrapper(Pagination<T> page, IWrapper<T> queryWrapper);

    <T> Pagination<T> queryPage(Pagination<T> page, IWrapper<T> queryWrapper);

    <T> Long count(T query);

    <T> Long count(IWrapper<T> queryWrapper);

}
