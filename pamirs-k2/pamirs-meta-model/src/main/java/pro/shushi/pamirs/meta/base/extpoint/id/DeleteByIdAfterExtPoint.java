package pro.shushi.pamirs.meta.base.extpoint.id;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;

import static pro.shushi.pamirs.meta.constant.ExtPointConstants.AFTER_SUFFIX;
import static pro.shushi.pamirs.meta.constant.FunctionConstants.deleteById;

/**
 * 根据ID删除单条数据后置扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
@XService(publish = false)
public interface DeleteByIdAfterExtPoint<T> {

    @ExtPoint.name(deleteById + AFTER_SUFFIX)
    @ExtPoint(displayName = "根据id删除单条记录后置扩展点")
    default Boolean deleteByIdAfter(Boolean result) {
        return result;
    }

}
