package pro.shushi.pamirs.eip.api.auth;

import org.apache.camel.ExtendedExchange;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipAuthenticationProcessor;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.model.EipApplication;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.util.PStringUtils;

/**
 * @author Adamancy Zhang
 * @date 2021-01-05 14:18
 */
@Component
public class OpenApiPermissionProcessor extends AbstractOpenApiAuthenticationProcessor implements IEipAuthenticationProcessor<SuperMap> {

    @Override
    public boolean authentication(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        String interfaceName = context.getApi().getInterfaceName();
        EipApplication eipApplication = (EipApplication) context.getExecutorContextValue(OpenApiConstant.OPEN_API_EIP_APPLICATION_KEY);

        Long count = Models.origin().count(
                Pops.query().from(EipApplication.APPLICATION_REL_OPENINTERFACE_MODEL_MODEL)
                        .eq(PStringUtils.fieldName2Column(EipApplication.APPLICATION_REL_OPENINTERFACE_FILED_APP), eipApplication.getAppKey())
                        .eq(PStringUtils.fieldName2Column(EipApplication.APPLICATION_REL_OPENINTERFACE_FILED_API), interfaceName)
        );
        if (count == 0) {
            error(exchange, "400006", "开放接口无访问权限");
            return false;
        }
        return true;
    }
}
