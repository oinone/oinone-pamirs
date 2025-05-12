package pro.shushi.pamirs.meta.base.extpoint.id;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;

import static pro.shushi.pamirs.meta.constant.ExtPointConstants.AFTER_SUFFIX;
import static pro.shushi.pamirs.meta.constant.FunctionConstants.updateById;

/**
 * 根据ID更新单条数据后置扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
@XService(publish = false)
public interface UpdateByIdAfterExtPoint<T> {

    @ExtPoint.name(updateById + AFTER_SUFFIX)
    @ExtPoint(displayName = "根据id更新单条记录后置扩展点")
    default Integer updateByIdAfter(Integer result) {
        return result;
    }

}
