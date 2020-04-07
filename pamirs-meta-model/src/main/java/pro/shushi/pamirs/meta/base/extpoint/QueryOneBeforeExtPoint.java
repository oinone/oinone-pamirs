package pro.shushi.pamirs.meta.base.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;

/**
 * 查询单条数据前置扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
public interface QueryOneBeforeExtPoint<T> {

    @ExtPoint(name = "before")
    T queryOneBefore(T data);

}
