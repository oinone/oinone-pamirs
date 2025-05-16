package pro.shushi.pamirs.eip.api.auth.oauth2;

import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;

/**
 * @author Adamancy Zhang on 2021-02-01 17:02
 */
public class EipOAuthConstant {

    public static final String STATE_SEPARATOR = "|";

    public static final String STATE_SEPARATOR_REGEX = "\\|";

    //region 上下文常量

    public static final String PARAMETER_PREFIX = "config.oauth2.";

    public static final String AUTHORIZATION_PREFIX_PARAMETER = PARAMETER_PREFIX + "authorizationPrefix";

    public static final String AUTHORIZATION_URI_INTERFACE_NAME_PARAMETER = PARAMETER_PREFIX + "authorizationUriInterfaceName";

    public static final String REFRESH_TOKEN_URI_INTERFACE_NAME_PARAMETER = PARAMETER_PREFIX + "refreshTokenUriInterfaceName";

    public static final String AUTHORIZATION_PARAMETER = PARAMETER_PREFIX + "authorization";

    public static final String GRANT_TYPE_PARAMETER = PARAMETER_PREFIX + "grantType";

    public static final String CODE_PARAMETER = PARAMETER_PREFIX + "code";

    public static final String CLIENT_ID_PARAMETER = PARAMETER_PREFIX + "clientId";

    public static final String CLIENT_SECRET_PARAMETER = PARAMETER_PREFIX + "clientSecret";

    public static final String CREDENTIALS_PARAMETER = PARAMETER_PREFIX + "credentials";

    public static final String REDIRECT_URL_PARAMETER = PARAMETER_PREFIX + "redirectUrl";

    public static final String ACCESS_TOKEN_PARAMETER = PARAMETER_PREFIX + "accessToken";

    public static final String REFRESH_TOKEN_PARAMETER = PARAMETER_PREFIX + "refreshToken";

    public static final String SCOPE_PARAMETER = PARAMETER_PREFIX + "scope";

    //endregion

    //region 函数常量

    public static final String OAUTH_AUTHORIZATION_CONVERTER_FUN = EipFunctionConstant.AUTHENTICATION_PROCESSOR_PREFIX + "defaultOAuthAuthorizationConverterCallback";

    //endregion
}
