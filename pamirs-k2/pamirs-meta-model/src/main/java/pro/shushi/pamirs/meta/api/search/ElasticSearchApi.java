package pro.shushi.pamirs.meta.api.search;

import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

/**
 * ElasticSearchApi
 *
 * @author yakir on 2020/04/14 21:20.
 */
public interface ElasticSearchApi {

    <T> Pagination<T> search(Pagination<T> page, IWrapper<T> queryWrapper);

}
