package pro.shushi.pamirs.eip.api.tenant;

import org.apache.camel.ExtendedExchange;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipPamirsTenantFinder;
import pro.shushi.pamirs.eip.api.auth.oauth2.EipOAuthConstant;
import pro.shushi.pamirs.eip.api.auth.oauth2.enumeration.EipOAuthParameter;

/**
 * @author Adamancy Zhang on 2021-02-01 17:01
 */
@Component
public class DefaultOAuth2TenantFinder implements IEipPamirsTenantFinder {

    @Override
    public boolean match(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        return true;
    }

    @Override
    public String find(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        String state = exchange.getMessage().getHeader(EipOAuthParameter.STATE.getOrigin(), String.class);
        if (state == null) {
            return null;
        }
        String[] states = state.split(EipOAuthConstant.STATE_SEPARATOR_REGEX);
        if (states.length >= 1) {
            return states[0].trim();
        }
        return null;
    }
}
