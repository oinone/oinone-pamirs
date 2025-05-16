package pro.shushi.pamirs.auth.api.utils;

import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.enumeration.authorized.ResourceAuthorizedValueEnum;

import java.util.Map;
import java.util.Set;

/**
 * 管理权限缓存结果集帮助类
 *
 * @author Adamancy Zhang at 10:16 on 2024-09-14
 */
public class AuthManagementPermissionCacheHelper {

    private AuthManagementPermissionCacheHelper() {
        // reject create object
    }

    public static AuthResult<Map<String, Set<String>>> collectionMenuPermissionResult(Map<Long, Map<String, Map<String, Long>>> accessMenusMap) {
        return AuthAccessPermissionCacheHelper.collectionMenuPermissionResult(accessMenusMap, ResourceAuthorizedValueEnum::isManagement);
    }

    public static AuthResult<Set<String>> collectionModelActionPermissionResult(Map<Long, Map<String, Map<String, Long>>> modelActionPermissions) {
        return AuthAccessPermissionCacheHelper.collectionModelActionPermissionResult(modelActionPermissions, ResourceAuthorizedValueEnum::isManagement);
    }

    public static AuthResult<Set<String>> collectionManagementResults(Map<Long, Map<String, Long>> accessPermissions) {
        return AuthAccessPermissionCacheHelper.collectionAccessResult(accessPermissions, ResourceAuthorizedValueEnum::isManagement);
    }
}
