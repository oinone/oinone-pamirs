package pro.shushi.pamirs.eip.api.auth.basic.authorization;

import org.apache.camel.ExtendedExchange;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipAuthenticationProcessor;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConvertParam;
import pro.shushi.pamirs.eip.api.auth.basic.EipBasicConstant;
import pro.shushi.pamirs.eip.api.auth.basic.enumeration.EipBasicAuthParameter;
import pro.shushi.pamirs.eip.api.auth.oauth2.enumeration.EipOAuthAuthorizationPrefix;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * Basic集成接口认证
 *
 * @author Adamancy Zhang at 14:22 on 2021-06-15
 */
@Slf4j
@Component
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
public class EipBasicAuthorizationProcessor implements IEipAuthenticationProcessor<SuperMap> {

    @Function.fun(EipBasicConstant.AUTHORIZATION_PROCESSOR)
    @Function.Advanced(displayName = "Basic集成接口认证")
    @Function(name = EipBasicConstant.AUTHORIZATION_PROCESSOR)
    public Boolean authenticationFunction(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        return authentication(context, exchange);
    }

    @Override
    public boolean authentication(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        String authorizationPrefix = StringHelper.valueOf(context.getExecutorContextValue(EipBasicConstant.AUTHORIZATION_PREFIX_PARAMETER));
        if (StringUtils.isBlank(authorizationPrefix)) {
            authorizationPrefix = EipOAuthAuthorizationPrefix.BASIC.getValue();
            context.putExecutorContextValue(EipBasicConstant.AUTHORIZATION_PREFIX_PARAMETER, authorizationPrefix);
        }
        String authorization = StringHelper.valueOf(context.getExecutorContextValue(EipBasicConstant.AUTHORIZATION_PARAMETER));
        if (StringUtils.isBlank(authorization)) {
            String username = StringHelper.valueOf(context.getInterfaceContextValue(EipBasicAuthParameter.USERNAME.getTarget()));
            String password = StringHelper.valueOf(context.getInterfaceContextValue(EipBasicAuthParameter.PASSWORD.getTarget()));
            if (StringUtils.isAnyBlank(username, password)) {
                List<IEipConvertParam<SuperMap>> convertParamList = ((EipIntegrationInterface) context.getApi()).getRequestParamProcessor().getConvertParamList();
                for (IEipConvertParam<SuperMap> convertParam : convertParamList) {
                    String outParam = convertParam.getOutParam();
                    if (StringUtils.equals(EipBasicAuthParameter.USERNAME.getTarget(), outParam)) {
                        username = (String) convertParam.getDefaultValue();
                    }
                    if (StringUtils.equals(EipBasicAuthParameter.PASSWORD.getTarget(), outParam)) {
                        password = (String) convertParam.getDefaultValue();
                    }
                }
            }
            String authorizationString = username + CharacterConstants.SEPARATOR_COLON + password;
            authorization = new String(Base64.getEncoder().encode(authorizationString.getBytes(StandardCharsets.UTF_8)));
            context.putExecutorContextValue(EipBasicConstant.AUTHORIZATION_PARAMETER, authorization);
        }
        context.putInterfaceContextValue(EipBasicConstant.AUTHORIZATION_HEADER_KEY, authorizationPrefix + authorization);
        return true;
    }
}
