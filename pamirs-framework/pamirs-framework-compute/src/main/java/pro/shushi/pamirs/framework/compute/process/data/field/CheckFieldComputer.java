package pro.shushi.pamirs.framework.compute.process.data.field;

import pro.shushi.pamirs.framework.compute.system.check.spi.api.CheckModelFieldServiceApi;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.data.FieldComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * 字段校验
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class CheckFieldComputer<T> implements FieldComputer<T> {

    @Override
    public Result<Void> compute(ComputeContext context, ModelFieldConfig field, T data) {
        if (!PamirsSession.directive().isDoCheck()) {
            return new Result<>();
        }
        boolean returnWhenError = context.returnWhenError();
        Boolean isSuccess = Spider.getDefaultExtension(CheckModelFieldServiceApi.class).check(returnWhenError, field, data);
        return new Result<Void>().setSuccess(isSuccess).addMessages(PamirsSession.getMessageHub().getAllMessages());
    }

}
