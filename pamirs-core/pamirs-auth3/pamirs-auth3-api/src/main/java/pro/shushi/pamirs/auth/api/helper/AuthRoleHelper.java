package pro.shushi.pamirs.auth.api.helper;

import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.AuthRole;

/**
 * 角色帮助类
 *
 * @author Adamancy Zhang at 18:50 on 2024-01-20
 */
public class AuthRoleHelper {

    private AuthRoleHelper() {
        // reject create object
    }

    public static boolean isAllowOperationRole(AuthRole role) {
        AuthorizationSourceEnum source = role.getSource();
        if (source == null) {
            return false;
        }
        return AuthorizationSourceEnum.MANUAL.equals(source);
    }

    public static boolean isActiveRole(AuthRole role) {
        Boolean active = role.getActive();
        if (active == null) {
            active = true;
        }
        return active;
    }
}
