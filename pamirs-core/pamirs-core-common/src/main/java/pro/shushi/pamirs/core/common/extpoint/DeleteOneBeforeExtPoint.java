package pro.shushi.pamirs.core.common.extpoint;

import pro.shushi.pamirs.core.common.function.FunctionConstant;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;
import pro.shushi.pamirs.meta.constant.ExtPointConstants;

/**
 * 单个删除前置扩展点
 *
 * @author Adamancy Zhang on 2021-05-17 17:01
 */
@Fun
@XService(publish = false)
public interface DeleteOneBeforeExtPoint<T> {

    /**
     * 前置处理
     *
     * @param data 数据
     * @return 数据
     */
    @ExtPoint.name(FunctionConstant.deleteOne + ExtPointConstants.BEFORE_SUFFIX)
    @ExtPoint(displayName = "单个删除前置扩展点")
    T deleteOneBefore(T data);
}
