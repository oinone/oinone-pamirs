package pro.shushi.pamirs.eip.api.auth;

import org.apache.camel.ExtendedExchange;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipAuthenticationProcessor;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipPamirsTenantFinder;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

/**
 * @author Adamancy Zhang
 * @date 2021-01-05 14:18
 */
@Component
public class PamirsTenantAuthenticationProcessor extends AbstractOpenApiAuthenticationProcessor implements IEipAuthenticationProcessor<SuperMap> {

    @Override
    public boolean authentication(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        String tenant = exchange.getMessage().getHeader(OpenApiConstant.TENANT_KEY, String.class);
        if (StringUtils.isBlank(tenant)) {
            for (IEipPamirsTenantFinder finder : BeanDefinitionUtils.getBeansOfTypeByOrdered(IEipPamirsTenantFinder.class)) {
                if (finder.match(context, exchange)) {
                    tenant = finder.find(context, exchange);
                    if (StringUtils.isNotBlank(tenant)) {
                        break;
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(tenant)) {
            PamirsTenantSession.setTenant(tenant);
        }
        return true;
    }
}
