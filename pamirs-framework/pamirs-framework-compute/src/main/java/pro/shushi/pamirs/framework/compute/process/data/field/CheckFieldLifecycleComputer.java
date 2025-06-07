package pro.shushi.pamirs.framework.compute.process.data.field;

import pro.shushi.pamirs.framework.compute.process.common.ComputeHelper;
import pro.shushi.pamirs.framework.compute.system.check.spi.api.CheckModelFieldServiceApi;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.data.FieldComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.msg.MessageHub;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.util.JsonUtils;

/**
 * 字段校验
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class CheckFieldLifecycleComputer<T> implements FieldComputer<T> {

    @Override
    public Result<Void> compute(ComputeContext context, ModelFieldConfig field, T data) {
        return MessageHub.closure(() -> {
            boolean returnWhenError = context.returnWhenError();
            Boolean isSuccess = Spider.getDefaultExtension(CheckModelFieldServiceApi.class).check(returnWhenError, field, data);
            return ComputeHelper.generateCheckResult(isSuccess, () -> "校验错误，错误数据："
                    + ComputeHelper.limitString(JsonUtils.toJSONString(data)) + "，校验如下：");
        });
    }

}
