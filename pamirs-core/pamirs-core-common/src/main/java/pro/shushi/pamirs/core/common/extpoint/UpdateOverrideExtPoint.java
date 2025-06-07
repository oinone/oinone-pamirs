package pro.shushi.pamirs.core.common.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;

import static pro.shushi.pamirs.meta.constant.ExtPointConstants.OVERRIDE;
import static pro.shushi.pamirs.meta.constant.FunctionConstants.update;

/**
 * 更新覆盖扩展点
 *
 * @author Adamancy Zhang
 * @date 2020-11-30 22:51
 */
@Fun
@XService(publish = false)
public interface UpdateOverrideExtPoint<T> {

    /**
     * 覆盖处理
     *
     * @param data 数据
     * @return 数据
     */
    @ExtPoint.name(update + OVERRIDE)
    @ExtPoint(displayName = "更新覆盖扩展点")
    T updateOverride(T data);
}
