package pro.shushi.pamirs.framework.compute.process.data.model;

import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.data.ModelComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.FunctionConstants;

/**
 * 默认构造函数计算函数
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class ConstructComputer<T> implements ModelComputer<T> {

    @Override
    public Result<Void> compute(ComputeContext context, String model, T data) {
        Result<Void> result = new Result<>();
        if (IWrapper.class.isAssignableFrom(data.getClass())) {
            return result;
        }
        Function construct = Fun.fetch(model, FunctionConstants.construct);
        // 默认值计算函数
        if (null != construct) {
            Fun.run(construct, data);
        }
        return result;
    }

}
