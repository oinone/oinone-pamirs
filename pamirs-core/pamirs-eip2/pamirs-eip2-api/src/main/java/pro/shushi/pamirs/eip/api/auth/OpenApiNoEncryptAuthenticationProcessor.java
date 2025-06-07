package pro.shushi.pamirs.eip.api.auth;

import org.apache.camel.ExtendedExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipAuthenticationProcessor;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.config.EipOpenApiSwitchCondition;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

@Component
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
@Conditional(EipOpenApiSwitchCondition.class)
public class OpenApiNoEncryptAuthenticationProcessor implements IEipAuthenticationProcessor<SuperMap> {

    @Autowired
    private AccessTokenAuthenticationProcessor accessTokenAuthenticationProcessor;

    @Autowired
    private EipApplicationAuthenticationProcessor eipApplicationAuthenticationProcessor;

    @Autowired
    private OpenApiPermissionProcessor openApiPermissionProcessor;

    @Function.fun(EipFunctionConstant.DEFAULT_NO_ENCRYPT_AUTHENTICATION_PROCESSOR_FUN)
    @Function.Advanced(displayName = "开放接口非加密请求认证")
    @Function(name = EipFunctionConstant.DEFAULT_NO_ENCRYPT_AUTHENTICATION_PROCESSOR_FUN)
    public Boolean authenticationFunction(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        return authentication(context, exchange);
    }

    @Override
    public boolean authentication(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        if (!accessTokenAuthenticationProcessor.authentication(context, exchange)) {
            return false;
        }

        if (!eipApplicationAuthenticationProcessor.authentication(context, exchange)) {
            return false;
        }

        if (!openApiPermissionProcessor.authentication(context, exchange)) {
            return false;
        }

        return true;
    }
}
