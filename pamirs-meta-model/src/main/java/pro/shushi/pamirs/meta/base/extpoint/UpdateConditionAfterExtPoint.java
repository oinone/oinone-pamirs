package pro.shushi.pamirs.meta.base.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;

import static pro.shushi.pamirs.meta.constant.ExtPointConstants.AFTER_SUFFIX;
import static pro.shushi.pamirs.meta.constant.FunctionConstants.updateByWrapper;

/**
 * 条件更新后置扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
@XService(publish = false)
public interface UpdateConditionAfterExtPoint<T> {

    @ExtPoint.name(updateByWrapper + AFTER_SUFFIX)
    @ExtPoint(displayName = "条件更新后置扩展点")
    default Integer updateConditionAfter(Integer data) {
        return data;
    }

}
