package pro.shushi.pamirs.meta.base.extpoint.id;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;

import static pro.shushi.pamirs.meta.constant.ExtPointConstants.BEFORE_SUFFIX;
import static pro.shushi.pamirs.meta.constant.FunctionConstants.updateById;

/**
 * 根据ID更新单条数据前置扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
@XService(publish = false)
public interface UpdateByIdBeforeExtPoint<T> {

    @ExtPoint.name(updateById + BEFORE_SUFFIX)
    @ExtPoint(displayName = "根据id更新单条记录前置扩展点")
    default T updateByIdBefore(T data) {
        return data;
    }

}
