package pro.shushi.pamirs.user.api.constants;

/**
 * 常量
 *
 * @author Adamancy Zhang at 09:51 on 2024-01-05
 */
public interface UserConstants {

    Long ANONYMOUS_USER_ID = 10000L;
    String ANONYMOUS_USER_CODE = "10000";
    String ANONYMOUS_USER_LOGIN = "anonymous";
    String ANONYMOUS_USER_NICKNAME = "UserConstants.anonymous_user_nickname";

    Long ADMIN_USER_ID = 10001L;
    String ADMIN_USER_CODE = "10001";
    String ADMIN_USER_LOGIN = "admin";
    String ADMIN_USER_PASSWORD = "admin";
    String ADMIN_USER_NICKNAME = "UserConstants.admin_user_nickname";

    String SINGLE_USER_CACHE_MODE = "single";
    String MULTIPLE_USER_CACHE_MODE = "multiple";

}
