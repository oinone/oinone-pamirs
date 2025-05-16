package pro.shushi.pamirs.eip.api.auth.oauth2.converter;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConvertParam;
import pro.shushi.pamirs.eip.api.IEipParamConverterCallback;
import pro.shushi.pamirs.eip.api.auth.oauth2.EipOAuthConstant;
import pro.shushi.pamirs.eip.api.auth.oauth2.enumeration.EipOAuthAuthorizationPrefix;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * OAuth Authorization 参数转换函数
 *
 * @author Adamancy Zhang on 2021-02-07 16:44
 */
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
public class EipOAuthAuthorizationConverterCallback implements IEipParamConverterCallback<SuperMap> {

    @Function.fun(EipOAuthConstant.OAUTH_AUTHORIZATION_CONVERTER_FUN)
    @Function.Advanced(displayName = "OAuth Authorization 参数转换函数")
    @Function(name = EipOAuthConstant.OAUTH_AUTHORIZATION_CONVERTER_FUN)
    @Override
    public Object callback(IEipContext<SuperMap> context, IEipConvertParam<SuperMap> convertParam, List<AtomicInteger> inParamCounterList, Object object) {
        String authorization = StringHelper.valueOf(context.getExecutorContextValue(EipOAuthConstant.AUTHORIZATION_PARAMETER));
        if (StringUtils.isBlank(authorization)) {
            String authorizationPrefix = StringHelper.valueOf(context.getExecutorContextValue(EipOAuthConstant.AUTHORIZATION_PREFIX_PARAMETER));
            if (StringUtils.isBlank(authorizationPrefix)) {
                authorizationPrefix = EipOAuthAuthorizationPrefix.BEARER.getValue();
            }
            authorization = authorizationPrefix + StringHelper.valueOf(object);
        }
        return authorization;
    }
}
