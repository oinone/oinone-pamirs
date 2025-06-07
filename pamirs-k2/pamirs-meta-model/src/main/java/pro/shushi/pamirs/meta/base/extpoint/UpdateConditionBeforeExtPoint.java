package pro.shushi.pamirs.meta.base.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import static pro.shushi.pamirs.meta.constant.ExtPointConstants.AFTER_SUFFIX;
import static pro.shushi.pamirs.meta.constant.FunctionConstants.updateByWrapper;

/**
 * 条件更新前置扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
@XService(publish = false)
public interface UpdateConditionBeforeExtPoint<T> {

    @ExtPoint.name(updateByWrapper + AFTER_SUFFIX)
    @ExtPoint(displayName = "条件更新前置扩展点")
    default T updateConditionBefore(T data, IWrapper<T> queryWrapper) {
        return data;
    }

}
