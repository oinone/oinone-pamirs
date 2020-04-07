package pro.shushi.pamirs.meta.api.core.orm;

import pro.shushi.pamirs.meta.api.dto.crud.Condition;
import pro.shushi.pamirs.meta.api.dto.crud.Page;
import pro.shushi.pamirs.meta.api.dto.crud.PageCondition;
import pro.shushi.pamirs.meta.api.dto.crud.QueryCondition;

import java.util.List;

/**
 * 模型数据库读API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 */
public interface ReadApi {

    <T> T queryOne(T query);

    <T> List<T> queryList(PageCondition<T> condition);

    <T> Page<T> queryPage(PageCondition<T> condition);

    <T> Long count(Condition<T> condition);

}
