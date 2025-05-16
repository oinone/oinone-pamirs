package pro.shushi.pamirs.auth.rbac.core.utils;

import pro.shushi.pamirs.auth.api.enumeration.authorized.FieldAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.ResourceAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.RowAuthorizedValueEnum;
import pro.shushi.pamirs.auth.rbac.api.model.AuthRbacFieldPermissionItem;
import pro.shushi.pamirs.auth.rbac.api.model.AuthRbacResourcePermissionItem;
import pro.shushi.pamirs.auth.rbac.api.model.AuthRbacRowPermissionItem;

/**
 * RBAC权限计算帮助类
 *
 * @author Adamancy Zhang at 12:27 on 2024-09-11
 */
public class AuthRbacAuthorizationComputeHelper {

    private AuthRbacAuthorizationComputeHelper() {
        // reject create object
    }

    public static Long computeResourceAuthorizedValue(AuthRbacResourcePermissionItem resourcePermission) {
        Long authorizedValue = 0L;
        if (Boolean.TRUE.equals(resourcePermission.getCanAccess())) {
            authorizedValue |= ResourceAuthorizedValueEnum.ACCESS.value();
        }
        if (Boolean.TRUE.equals(resourcePermission.getCanManagement())) {
            authorizedValue |= ResourceAuthorizedValueEnum.MANAGEMENT.value();
        }
        if (Boolean.TRUE.equals(resourcePermission.getCanDesign())) {
            authorizedValue |= ResourceAuthorizedValueEnum.DESIGN.value();
        }
        return authorizedValue;
    }

    public static Long computeFieldAuthorizedValue(AuthRbacFieldPermissionItem fieldPermission) {
        Long authorizedValue = 0L;
        if (Boolean.TRUE.equals(fieldPermission.getPermRead())) {
            authorizedValue |= FieldAuthorizedValueEnum.READ.value();
        }
        if (Boolean.TRUE.equals(fieldPermission.getPermWrite())) {
            authorizedValue |= FieldAuthorizedValueEnum.WRITE.value();
        }
        return authorizedValue;
    }

    public static Long computeRowAuthorizedValue(AuthRbacRowPermissionItem rowPermission) {
        Long authorizedValue = 0L;
        if (Boolean.TRUE.equals(rowPermission.getPermRead())) {
            authorizedValue |= RowAuthorizedValueEnum.READ.value();
        }
        if (Boolean.TRUE.equals(rowPermission.getPermWrite())) {
            authorizedValue |= RowAuthorizedValueEnum.WRITE.value();
        }
        if (Boolean.TRUE.equals(rowPermission.getPermDelete())) {
            authorizedValue |= RowAuthorizedValueEnum.DELETE.value();
        }
        return authorizedValue;
    }
}
