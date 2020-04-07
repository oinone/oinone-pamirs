package pro.shushi.pamirs.meta.api.core.orm;

import pro.shushi.pamirs.meta.api.dto.crud.Condition;
import pro.shushi.pamirs.meta.api.dto.crud.UpdateCondition;

import java.util.List;

/**
 * 模型数据库写API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 */
public interface WriteApi {

    <T> T create(T data);

    <T> T update(T data);

    <T> T update(UpdateCondition<T> condition);

    <T> List<T> create(List<T> dataList);

    <T> List<T> update(List<T> dataList);

    <T> Boolean delete(Condition<T> condition);

}
