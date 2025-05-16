package pro.shushi.pamirs.framework.configure.annotation.core;

import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;

/**
 * 计算逻辑提交器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/15 6:03 下午
 */
public interface Submitter<T extends Prioritized> {

    @SuppressWarnings({"rawtypes"})
    Result compute(ExecuteContext executeContext, T priorityComputer);

}
