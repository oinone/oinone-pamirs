package pro.shushi.pamirs.auth.api.extpoint;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.AuthUserRoleRel;
import pro.shushi.pamirs.auth.api.service.manager.AuthRoleManager;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;

import java.util.List;

/**
 * 角色管理扩展点
 *
 * @author Adamancy Zhang at 23:55 on 2024-05-22
 */
@Ext(AuthRoleManager.class)
public interface AuthRoleManagerExtPoint {

    @ExtPoint(displayName = "删除角色前")
    default Boolean deleteBefore(List<AuthRole> roles, List<AuthUserRoleRel> userRoleRelList) {
        return null;
    }

    @ExtPoint(displayName = "删除角色后")
    default Boolean deleteAfter(List<AuthRole> roles, List<AuthUserRoleRel> userRoleRelList) {
        return null;
    }

    @ExtPoint(displayName = "启用角色")
    default Boolean activeAfter(AuthRole role, List<AuthUserRoleRel> userRoleRelList) {
        return null;
    }

    @ExtPoint(displayName = "禁用角色")
    default Boolean disableAfter(AuthRole role, List<AuthUserRoleRel> userRoleRelList) {
        return null;
    }

}
