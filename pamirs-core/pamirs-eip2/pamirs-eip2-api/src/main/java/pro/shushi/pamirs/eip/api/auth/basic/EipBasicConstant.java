package pro.shushi.pamirs.eip.api.auth.basic;

import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;

/**
 * @author Adamancy Zhang at 14:31 on 2021-06-15
 */
public class EipBasicConstant {

    public static final String AUTHORIZATION_PROCESSOR = EipFunctionConstant.AUTHENTICATION_PROCESSOR_PREFIX + "eipBasicAuthorizationProcessor";
    public static final String AUTHORIZATION_CAK_PROCESSOR = EipFunctionConstant.AUTHENTICATION_PROCESSOR_PREFIX + "eipCakAuthorizationProcessor";

    public static final String AUTH_PROCESSOR = EipFunctionConstant.AUTHENTICATION_PROCESSOR_PREFIX + "eipBasicAuthProcessor";

    //region 上下文常量

    public static final String PARAMETER_PREFIX = "config.basic.";

    public static final String AUTHORIZATION_PREFIX_PARAMETER = PARAMETER_PREFIX + "authorizationPrefix";

    public static final String AUTHORIZATION_PARAMETER = PARAMETER_PREFIX + "authorization";

    public static final String AUTHORIZATION_KEY = "Authorization";

    public static final String AUTHORIZATION_HEADER_KEY = IEipContext.HEADER_PARAMS_KEY + "." + AUTHORIZATION_KEY;

    //endregion

    //region 函数常量

    public static final String AUTHORIZATION_CONVERTER_FUN = EipFunctionConstant.AUTHENTICATION_PROCESSOR_PREFIX + "defaultBasicAuthorizationConverterCallback";

    //endregion
}
