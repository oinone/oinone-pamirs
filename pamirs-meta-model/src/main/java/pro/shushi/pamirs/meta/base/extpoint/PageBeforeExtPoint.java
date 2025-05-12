package pro.shushi.pamirs.meta.base.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import static pro.shushi.pamirs.meta.constant.ExtPointConstants.BEFORE_SUFFIX;
import static pro.shushi.pamirs.meta.constant.FunctionConstants.queryPage;

/**
 * 分页查询前置扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
@XService(publish = false)
public interface PageBeforeExtPoint<T> {

    @ExtPoint.name(queryPage + BEFORE_SUFFIX)
    @ExtPoint(displayName = "分页查询前置扩展点")
    default Object[] queryPageBefore(Pagination<T> page, IWrapper<T> queryWrapper) {
        return new Object[]{page, queryWrapper};
    }

}
