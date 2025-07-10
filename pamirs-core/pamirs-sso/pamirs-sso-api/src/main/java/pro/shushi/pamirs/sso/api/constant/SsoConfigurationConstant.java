package pro.shushi.pamirs.sso.api.constant;


public class SsoConfigurationConstant {
    public static final String PAMIRS_SSO_PREFIX = "pamirs.sso";

    public static final String PAMIRS_SSO_PRIVATE_KEY_PREFIX = "pamirs:sso:temporary:";

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String OAUTH2_PREFIX = "Basic ";
    public static final String PAMIRS_SSO_LOGIN_KEY = "loginKey";

    public static final String PAMIRS_SSO_REDIS_KEY_PREFIX = "pamirs:sso:";
    public static final String PAMIRS_SSO_SERVER_REDIS_KEY_PREFIX = "pamirs:sso:server:";

    public static final String PAMIRS_SSO_REDIS_KEY_AK_SUFFIX = ":access_token";

    public static final String PAMIRS_SSO_REDIS_KEY_RK_SUFFIX = ":refresh_token";

    public static final String USER_REDIS_CACHE = "SSO_USER_CACHE";

    public static final String PAMIRS_SSO_CLIENT_ID_PREFIX = "pamirs_";

    public static final String PAMIRS_SSO_CLIENT_PUBLIC = "pamirs_public";
    public static final String PAMIRS_SSO_INTERNAL_CLIENT_PRIVATE = "pamirs_sso_internal_private";
    public static final String PAMIRS_SSO_INTERNAL_CLIENT_PUBLIC = "pamirs_sso_internal_public";

    public static final String PAMIRS_SSO_TOKEN_HEADER = "Authorization";
    public static final String PAMIRS_SSO_TOKEN_DELIMITER = ":";
    public static final String PAMIRS_SSO_ACCESS_TOKEN_LIKE = ":*";


    public static final String PAMIRS_SSO_CLIENT_ID = "clientId";
    public static final String PAMIRS_SSO_OPEN_ID = "openId";
    public static final String PAMIRS_SSO_RANDOM_AK_ID = "randomAkId";
    public static final String PAMIRS_SSO_DATE_TIME = "dateTime";
}