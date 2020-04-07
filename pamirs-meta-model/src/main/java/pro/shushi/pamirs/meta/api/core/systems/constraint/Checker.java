package pro.shushi.pamirs.meta.api.core.systems.constraint;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;

/**
 * 校验接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:50 上午
 *
 * @param <T> 入参类型
 *
 */
public interface Checker<T> extends CommonApi {

    /**
     * 校验
     *
     * @param value 值
     * @return
     */
    Result check(T value);

}
