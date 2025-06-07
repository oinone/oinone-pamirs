package pro.shushi.pamirs.user.api.constants;

import pro.shushi.pamirs.meta.constant.RSqlConstants;

public class UserConstant {

    public static final String USER_SESSION_ID = "pamirs_uc_session_id";

    public static final String USER_CACHE_KEY = "pamirs:user:";

    public static final String USER_TOKEN_HEADER = "Authorization";

    public static final String USER_OAUTH_HEADER = "Oauth";

    public static final String USER_LOGIN_VIEW_ACTION = "userLoginViewAction";

    public static final String USER_TOKEN_PREFIX = "Bearer ";

    public static final int USER_EXPIRE_TIME = 3600 * 2;//unit:s

    public static final String DINGTALK_ENABLED_KEY = "dingtalk_enabled";

    public static final String WXWORK_ENABLED_KEY = "wxwork_enabled";

    public static final String DINGLOGIN_APPKEY = "dingLogin_appkey";

    public static final String DINGLOGIN_APPSECRET = "dingLogin_appsecret";

    public static final String DINGLOGIN_AGENTID = "dingLogin_agentId";

    public static final String USER_TOKEN_KEY = ":user_token:";

    public static final String LOGIN_PIC_CODE = "login_pic_code";//登录的时候验证码类型

    public static final String COMMON_PIC_CODE = "common_pic_code";

    public static final String MODIFY_PWD_PIC_CODE = "modify_pwd_pic_code"; //修改密码的时候验证码类型

    public static final String LOGIN_ERROR_COUNT = "_login_error_count";

    /**
     * @deprecated please using RsqlConstants#WHERE
     */
    @Deprecated
    public static final String WHERE_LOW_CASE = RSqlConstants.WHERE;

    public static final String USER_SYSTEM_NAME = "系统";

    public static final Long USER_SYSTEM_ID = 10002L;

    /**
     * @deprecated please using UserConstants#ADMIN_USER_ID
     */
    @Deprecated
    public static final Long ADMIN_USER_ID = UserConstants.ADMIN_USER_ID;

    public static final String FIELD_PIC_CODE = "picCode";

    public static final String FIELD_PASSWORD = "password";

    public static final String FIELD_VERIFICATION_CODE = "verificationCode";

    public static final String FIELD_LOGIN = "login";

    public static final String FIELD_EMAIL = "email";

    public static final String FIELD_PHONE = "phone";

    public static final String SOURCE = "Source";//应用来源，区分不同的请求应用

    // 用户信息的缓存key
    public static final String USER_INFO_CACHE_KEY = "pamirs:user-info:";
    public static final int USER_INFO_EXPIRE_TIME = 30;//unit:s

}
