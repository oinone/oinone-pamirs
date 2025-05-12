package pro.shushi.pamirs.meta.base.extpoint.code;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;

import static pro.shushi.pamirs.meta.constant.ExtPointConstants.BEFORE_SUFFIX;
import static pro.shushi.pamirs.meta.constant.FunctionConstants.deleteByCode;

/**
 * 根据编码删除单条数据前置扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
@XService(publish = false)
public interface DeleteByCodeBeforeExtPoint<T> {

    @ExtPoint.name(deleteByCode + BEFORE_SUFFIX)
    @ExtPoint(displayName = "根据编码删除单条记录前置扩展点")
    default T deleteByCodeBefore(T data) {
        return data;
    }

}
