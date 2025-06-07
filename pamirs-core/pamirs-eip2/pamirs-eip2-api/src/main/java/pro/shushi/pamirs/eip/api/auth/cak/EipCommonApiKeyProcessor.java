package pro.shushi.pamirs.eip.api.auth.cak;

import org.apache.camel.ExtendedExchange;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipAuthenticationProcessor;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConvertParam;
import pro.shushi.pamirs.eip.api.auth.basic.EipBasicConstant;
import pro.shushi.pamirs.eip.api.auth.oauth2.enumeration.EipOAuthAuthorizationPrefix;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.List;


/**
 * EipCommonApiKeyProcessor
 *
 * @author yakir on 2023/05/23 14:25.
 */
@Slf4j
@Component
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
public class EipCommonApiKeyProcessor implements IEipAuthenticationProcessor<SuperMap> {

    @Function.fun(EipBasicConstant.AUTHORIZATION_CAK_PROCESSOR)
    @Function.Advanced(displayName = "CommonApiKey集成接口认证")
    @Function(name = EipBasicConstant.AUTHORIZATION_CAK_PROCESSOR)
    public Boolean authenticationFunction(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        return authentication(context, exchange);
    }

    @Override
    public boolean authentication(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        String authorizationPrefix = StringHelper.valueOf(context.getExecutorContextValue(EipBasicConstant.AUTHORIZATION_PREFIX_PARAMETER));
        if (StringUtils.isBlank(authorizationPrefix)) {
            authorizationPrefix = EipOAuthAuthorizationPrefix.CAK.getValue();
            context.putExecutorContextValue(EipBasicConstant.AUTHORIZATION_PREFIX_PARAMETER, authorizationPrefix);
        }
        String authorization = StringHelper.valueOf(context.getExecutorContextValue(EipBasicConstant.AUTHORIZATION_PARAMETER));
        if (StringUtils.isBlank(authorization)) {
            String accessKey = StringHelper.valueOf(context.getInterfaceContextValue(EipCakAuthParameter.ACCESSKEY.getTarget()));
            String accessSecret = StringHelper.valueOf(context.getInterfaceContextValue(EipCakAuthParameter.ACCESSSECRET.getTarget()));
            String accessKeyId = StringHelper.valueOf(context.getInterfaceContextValue(EipCakAuthParameter.ACCESSKEY_ID.getTarget()));
            String accessSecretId = StringHelper.valueOf(context.getInterfaceContextValue(EipCakAuthParameter.ACCESSSECRET_ID.getTarget()));
            if (StringUtils.isAnyBlank(accessKey, accessSecret)) {
                List<IEipConvertParam<SuperMap>> convertParamList = ((EipIntegrationInterface) context.getApi()).getRequestParamProcessor().getConvertParamList();
                for (IEipConvertParam<SuperMap> convertParam : convertParamList) {
                    String outParam = convertParam.getOutParam();
                    if (StringUtils.equals(EipCakAuthParameter.ACCESSKEY.getTarget(), outParam)) {
                        accessKey = (String) convertParam.getDefaultValue();
                    }
                    if (StringUtils.equals(EipCakAuthParameter.ACCESSSECRET.getTarget(), outParam)) {
                        accessSecret = (String) convertParam.getDefaultValue();
                    }
                }
            }
            context.putExecutorContextValue(EipBasicConstant.AUTHORIZATION_PARAMETER + "." + accessKeyId, accessKeyId);
            context.putExecutorContextValue(EipBasicConstant.AUTHORIZATION_PARAMETER + "." + accessSecretId, accessKeyId);

            context.putInterfaceContextValue(IEipContext.HEADER_PARAMS_KEY + "." + accessKeyId, accessKey);
            context.putInterfaceContextValue(IEipContext.HEADER_PARAMS_KEY + "." + accessSecretId, accessSecret);
        }
        return true;
    }
}
