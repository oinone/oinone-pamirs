package pro.shushi.pamirs.auth.api.user;

/**
 * 用户信息服务
 * <p>
 * PS: 兼容旧版模块依赖，启动工程必须依赖pamirs-user-api才可使用，不提供默认服务
 * </p>
 *
 * @author Adamancy Zhang at 15:28 on 2024-01-06
 */
@Deprecated
public interface AuthUserService {

    /**
     * Please using SystemUser.admin()
     *
     * @deprecated 6.0.0
     */
    AuthUser getAdminUser();

    /**
     * Please using SystemUser.anonymous()
     *
     * @deprecated 6.0.0
     */
    AuthUser getAnonymousUser();

    /**
     * @deprecated 6.0.0
     */
    String getPasswordModel();

}
