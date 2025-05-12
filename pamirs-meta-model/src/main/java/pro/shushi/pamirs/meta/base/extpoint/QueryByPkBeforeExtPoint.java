package pro.shushi.pamirs.meta.base.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;

import static pro.shushi.pamirs.meta.constant.ExtPointConstants.BEFORE_SUFFIX;
import static pro.shushi.pamirs.meta.constant.FunctionConstants.queryByPk;

/**
 * 根据主键查询单条数据前置扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
@XService(publish = false)
public interface QueryByPkBeforeExtPoint<T> {

    @ExtPoint.name(queryByPk + BEFORE_SUFFIX)
    @ExtPoint(displayName = "根据主键查询单条记录后置扩展点")
    default T queryByPkBefore(T data) {
        return data;
    }

}
