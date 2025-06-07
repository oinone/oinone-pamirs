package pro.shushi.pamirs.framework.compute.process.data.model;

import pro.shushi.pamirs.framework.compute.process.common.ComputeHelper;
import pro.shushi.pamirs.framework.compute.system.check.spi.api.CheckModelServiceApi;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.data.ModelComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.msg.MessageHub;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.util.JsonUtils;

/**
 * 模型约束函数
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@SPI.Service
public class CheckModelLifecycleComputer<T> implements ModelComputer<T> {

    @Override
    public Result<Void> compute(ComputeContext context, String model, T data) {
        return MessageHub.closure(() -> {
            boolean returnWhenError = context.returnWhenError();
            Boolean isSuccess = Spider.getDefaultExtension(CheckModelServiceApi.class).check(returnWhenError, model, data);
            return ComputeHelper.generateCheckResult(isSuccess, () -> "校验错误，错误数据："
                    + ComputeHelper.limitString(JsonUtils.toJSONString(data)) + "，校验如下：");
        });
    }

}
