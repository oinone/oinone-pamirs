package pro.shushi.pamirs.user.core.base.pojo;

import pro.shushi.pamirs.auth.api.model.AuthRole;

import java.util.Map;

/**
 * @author Wuxin
 * @Date 2024/6/29
 * @since 1.0
 */
public class UserPojo {
    private Map<String, AuthRole> authRoleMap;

    public Map<String, AuthRole> getAuthRoleMap() {
        return authRoleMap;
    }

    public void setAuthRoleMap(Map<String, AuthRole> authRoleMap) {
        this.authRoleMap = authRoleMap;
    }

    public static UserPojo of(Map<String, AuthRole> authRoleMap) {
        UserPojo userPojo = new UserPojo();
        userPojo.setAuthRoleMap(authRoleMap);

        return userPojo;
    }
}
