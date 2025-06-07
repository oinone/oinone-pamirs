package pro.shushi.pamirs.framework.configure.annotation.core;

import pro.shushi.pamirs.meta.api.dto.common.Result;

/**
 * 计算执行器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/15 6:03 下午
 */
public interface Executor<D> {

    Result<Void> compute(D data);

}
