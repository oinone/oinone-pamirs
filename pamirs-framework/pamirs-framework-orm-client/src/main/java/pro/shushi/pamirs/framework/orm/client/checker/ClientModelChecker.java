package pro.shushi.pamirs.framework.orm.client.checker;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.emnu.FwExpEnumerate;
import pro.shushi.pamirs.framework.compute.system.check.spi.api.CheckModelServiceApi;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

/**
 * 模型记录校验
 * <p>
 * 2021/3/5 1:07 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
public class ClientModelChecker {

    public Object check(ModelComputeContext context, String model, Object obj) {
        if (!PamirsSession.directive().isDoCheck()) {
            return obj;
        }
        boolean returnWhenError = PamirsSession.getRequestVariables().returnWhenError();
        Boolean isSuccess = Spider.getDefaultExtension(CheckModelServiceApi.class).check(returnWhenError, model, obj);
        if (!isSuccess && PamirsSession.getRequestVariables().returnWhenError()) {
            throw PamirsException.construct(FwExpEnumerate.BASE_CHECK_DATA_ERROR).errThrow();
        }
        return obj;
    }

    public Object check(ModelComputeContext context, ModelDefinition modelDefinition, Object obj) {
        if (!PamirsSession.directive().isDoCheck()) {
            return obj;
        }
        boolean returnWhenError = PamirsSession.getRequestVariables().returnWhenError();
        Boolean isSuccess = Spider.getDefaultExtension(CheckModelServiceApi.class).check(returnWhenError, modelDefinition, obj);
        if (!isSuccess && PamirsSession.getRequestVariables().returnWhenError()) {
            throw PamirsException.construct(FwExpEnumerate.BASE_CHECK_DATA_ERROR).errThrow();
        }
        return obj;
    }

}
