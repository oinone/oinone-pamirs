package pro.shushi.pamirs.meta.api.core.orm;

import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;

/**
 * 模型数据同步API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface EnhanceApi {

    <T> Pagination<T> search(Pagination<T> page, IWrapper<T> queryWrapper);

    <T> List<T> synchronize(List<T> data);

}
