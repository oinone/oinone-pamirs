package pro.shushi.pamirs.auth.api.helper;

import pro.shushi.pamirs.auth.api.user.AuthUser;

/**
 * 用户帮助类
 *
 * @author Adamancy Zhang at 19:02 on 2024-01-20
 */
public class AuthUserHelper {

    private AuthUserHelper() {
        // reject create object
    }

    public static Boolean isActiveUser(AuthUser user) {
        Boolean active = user.getActive();
        if (active == null) {
            active = Boolean.TRUE;
        }
        return active;
    }
}
