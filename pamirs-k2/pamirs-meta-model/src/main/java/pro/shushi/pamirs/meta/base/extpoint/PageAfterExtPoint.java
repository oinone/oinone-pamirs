package pro.shushi.pamirs.meta.base.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import static pro.shushi.pamirs.meta.constant.ExtPointConstants.AFTER_SUFFIX;
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
public interface PageAfterExtPoint<T> {

    @ExtPoint.name(queryPage + AFTER_SUFFIX)
    @ExtPoint(displayName = "分页查询后置扩展点")
    default Pagination<T> queryPageAfter(Pagination<T> page) {
        return page;
    }

}
