package pro.shushi.pamirs.eip.api.auth;

import pro.shushi.pamirs.eip.api.constant.EipConfigurationConstant;

/**
 * @author Adamancy Zhang on 2021-06-09 18:34
 */
public class OpenApiConstant {

    public static final String ACCESS_TOKEN_KEY = "accessToken";

    public static final String APP_KEY_KEY = "appKey";

    public static final String APP_SECRET_KEY = "appSecret";

    public static final String SIGNATURE_KEY = "signature";

    public static final String SIGNATURE_METHOD_KEY = "signature_method";

    public static final String EIP_APPLICATION_KEY = "eipApplication";

    public static final String EIP_OPEN_INTERFACE = "openInterface";

    //region 开放接口固定键值

    public static final String OPEN_API_FIXED_PREFIX = "/openapi";

    public static final String TENANT_KEY = "tenant";

    public static final String OPEN_API_DATA_KEY = "data";

    public static final String OPEN_API_SUCCESS_KEY = "success";

    public static final String OPEN_API_ERROR_CODE_KEY = "errorCode";

    public static final int OPEN_API_ERROR_CODE_SUCCESS_VALUE = 0;

    public static final String OPEN_API_ERROR_MSG_KEY = "errorMsg";

    public static final String OPEN_API_ERROR_MSG_SUCCESS_VALUE = "ok";

    public static final String OPEN_API_GET_ACCESS_TOKEN = "openApi_getAccessToken";

    //endregion

    //region 开放接口缓存常量

    public static final String CACHE_PREFIX_KEY = "pamirs:eip:";

    public static final String OPEN_API_CACHE_PREFIX_KEY = CACHE_PREFIX_KEY + "openapi:";

    public static final String OPEN_API_AUTH_CACHE_PREFIX_KEY = OPEN_API_CACHE_PREFIX_KEY + "auth:";

    public static final String OPEN_API_AUTH_CACHE_SUFFIX_KEY = ":token";

    public static final String OPEN_API_APP_KEY_KEY = EipConfigurationConstant.PAMIRS_EIP_OPEN_API_PREFIX + ".appKey";

    public static final String OPEN_API_EIP_APPLICATION_KEY = EipConfigurationConstant.PAMIRS_EIP_OPEN_API_PREFIX + ".eipApplication";

    public static final String OPEN_API_EIP_AUTHENTICATION_KEY = EipConfigurationConstant.PAMIRS_EIP_OPEN_API_PREFIX + ".eipAuthentication";

    //endregion
}
