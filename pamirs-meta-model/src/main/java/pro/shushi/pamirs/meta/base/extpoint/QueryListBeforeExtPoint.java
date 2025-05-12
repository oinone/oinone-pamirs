package pro.shushi.pamirs.meta.base.extpoint;


import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import static pro.shushi.pamirs.meta.constant.ExtPointConstants.BEFORE_SUFFIX;
import static pro.shushi.pamirs.meta.constant.FunctionConstants.queryListByWrapper;

/**
 * 查询列表前置扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
@XService(publish = false)
public interface QueryListBeforeExtPoint<T> {

    @ExtPoint.name(queryListByWrapper + BEFORE_SUFFIX)
    @ExtPoint(displayName = "查询列表前置扩展点")
    default IWrapper<T> queryListBefore(IWrapper<T> condition) {
        return condition;
    }

}
