package pro.shushi.pamirs.auth.api.constants;

import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.locale.utils.I18nUtils;

/**
 * 系统角色
 *
 * @author Adamancy Zhang at 19:33 on 2024-01-04
 */
public class SystemRole {

    private SystemRole() {
        // reject create object
    }

    public static AuthRole admin() {
        AuthRole role = new AuthRole();
        role.setCode(AuthConstants.SUPER_ROLE_CODE)
                .setName(I18nUtils.getMessage(AuthConstants.SUPER_ROLE_NAME))
                .setDescription(I18nUtils.getMessage(AuthConstants.SUPER_ROLE_COMMENT))
                .setSource(AuthorizationSourceEnum.BUILD_IN)
                .setRoleTypeCode(AuthConstants.SYSTEM_ROLE_TYPE_CODE)
                .setActive(Boolean.TRUE)
                .setId(AuthConstants.SUPER_ROLE_ID);
        return role;
    }

    @Deprecated
    public static AuthRole base() {
        AuthRole role = new AuthRole();
        role.setCode(AuthConstants.BASE_ROLE_CODE)
                .setName(I18nUtils.getMessage(AuthConstants.BASE_ROLE_NAME))
                .setDescription(I18nUtils.getMessage(AuthConstants.BASE_ROLE_COMMENT))
                .setSource(AuthorizationSourceEnum.SYSTEM)
                .setRoleTypeCode(AuthConstants.SYSTEM_ROLE_TYPE_CODE)
                .setActive(Boolean.TRUE)
                .setId(AuthConstants.BASE_ROLE_ID);
        return role;
    }

    @Deprecated
    public static AuthRole business() {
        AuthRole role = new AuthRole();
        role.setCode(AuthConstants.BUSINESS_ROLE_CODE)
                .setName(I18nUtils.getMessage(AuthConstants.BUSINESS_ROLE_NAME))
                .setDescription(I18nUtils.getMessage(AuthConstants.BUSINESS_ROLE_COMMENT))
                .setSource(AuthorizationSourceEnum.SYSTEM)
                .setRoleTypeCode(AuthConstants.SYSTEM_ROLE_TYPE_CODE)
                .setActive(Boolean.TRUE)
                .setId(AuthConstants.BUSINESS_ROLE_ID);
        return role;
    }
}
