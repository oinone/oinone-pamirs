package pro.shushi.pamirs.framework.compute.process.data.model;

import pro.shushi.pamirs.framework.compute.system.check.spi.api.CheckModelServiceApi;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.data.ModelComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * 模型约束函数
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@SPI.Service
public class CheckModelComputer<T> implements ModelComputer<T> {

    @Override
    public Result<Void> compute(ComputeContext context, String model, T data) {
        if (!PamirsSession.directive().isDoCheck()) {
            return new Result<>();
        }
        boolean returnWhenError = context.returnWhenError();
        Boolean isSuccess = Spider.getDefaultExtension(CheckModelServiceApi.class).check(returnWhenError, model, data);
        return new Result<Void>().setSuccess(isSuccess).addMessages(PamirsSession.getMessageHub().getAllMessages());
    }

}
