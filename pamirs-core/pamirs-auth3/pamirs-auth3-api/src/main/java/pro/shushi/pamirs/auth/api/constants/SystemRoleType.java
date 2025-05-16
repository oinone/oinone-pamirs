package pro.shushi.pamirs.auth.api.constants;

import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.AuthRoleType;

/**
 * 系统角色类型
 *
 * @author Adamancy Zhang at 13:24 on 2024-01-05
 */
public class SystemRoleType {

    private SystemRoleType() {
        // reject create object
    }

    public static AuthRoleType system() {
        AuthRoleType roleType = new AuthRoleType();
        roleType.setCode(AuthConstants.SYSTEM_ROLE_TYPE_CODE)
                .setName(AuthConstants.SYSTEM_ROLE_TYPE_NAME)
                .setSource(AuthorizationSourceEnum.BUILD_IN)
                .setId(AuthConstants.SYSTEM_ROLE_TYPE_ID);
        return roleType;
    }
}
