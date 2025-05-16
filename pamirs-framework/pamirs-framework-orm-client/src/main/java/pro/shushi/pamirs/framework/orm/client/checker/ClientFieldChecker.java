package pro.shushi.pamirs.framework.orm.client.checker;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.emnu.FwExpEnumerate;
import pro.shushi.pamirs.framework.compute.system.check.spi.api.CheckModelFieldServiceApi;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.function.FieldCheckApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.Map;

/**
 * 前端字段校验处理
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class ClientFieldChecker implements FieldCheckApi {

    @Override
    public void check(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        if (!PamirsSession.directive().isDoCheck()) {
            return;
        }
        String fun = context.getTotalContext().getFun();
        boolean returnWhenError = PamirsSession.getRequestVariables().returnWhenError();
        Boolean isSuccess = Spider.getDefaultExtension(CheckModelFieldServiceApi.class)
                .check(returnWhenError, fieldConfig, origin, fun);
        if (!isSuccess && PamirsSession.getRequestVariables().returnWhenError()) {
            throw PamirsException.construct(FwExpEnumerate.BASE_CHECK_DATA_ERROR).errThrow();
        }
    }

}
