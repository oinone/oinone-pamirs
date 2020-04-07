package pro.shushi.pamirs.meta.base.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.api.dto.crud.PageCondition;

/**
 * 查询列表前置扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
public interface QueryListBeforeExtPoint<T> {

    @ExtPoint(name = "queryListBefore")
    PageCondition<T> queryListBefore(PageCondition<T> condition);

}
