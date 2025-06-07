package pro.shushi.pamirs.auth.api.utils;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.boot.base.enmu.BaseExpEnumerate;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.Set;

/**
 * 权限验证帮助类
 *
 * @author Adamancy Zhang at 21:53 on 2024-04-23
 */
public class AuthVerificationHelper {

    private AuthVerificationHelper() {
        // reject create object
    }

    public static void checkLogin() {
        if (PamirsSession.getUserId() == null) {
            throw PamirsException.construct(BaseExpEnumerate.BASE_USER_NOT_LOGIN_ERROR).errThrow();
        }
    }

    public static Boolean isAccessModule(Set<String> accessModules, String module) {
        return AuthApiHolder.getVerificationPermissionApi().isAccessModule(accessModules, module);
    }

    public static Boolean isAccessHomepage(Set<String> accessHomepages, String module) {
        return AuthApiHolder.getVerificationPermissionApi().isAccessHomepage(accessHomepages, module);
    }

    public static Boolean isAccessMenu(Set<String> accessMenus, String module, String name) {
        return AuthApiHolder.getVerificationPermissionApi().isAccessMenu(accessMenus, module, name);
    }

    public static Boolean isAccessFunction(String namespace, String fun) {
        return AuthApiHolder.getVerificationPermissionApi().isAccessFunction(namespace, fun);
    }

    public static Boolean isAccessAction(Set<String> accessActions, String model, String name) {
        String path = SessionPathHelper.getActionPath(model, name);
        if (StringUtils.isBlank(path)) {
            return AuthApiHolder.getVerificationPermissionApi().isAccessAction(accessActions, model, name);
        }
        return AuthApiHolder.getVerificationPermissionApi().isAccessAction(accessActions, path);
    }

    public static Boolean isAccessAction(Set<String> accessActions, String path) {
        return AuthApiHolder.getVerificationPermissionApi().isAccessAction(accessActions, path);
    }

    public static boolean isAccessResource(AuthResult<Boolean> result) {
        boolean isAccess = false;
        if (result != null && result.isFetch()) {
            Boolean isValid = result.getData();
            if (isValid == null) {
                isValid = true;
            }
            isAccess = isValid;
        }
        return isAccess;
    }

    public static Boolean isManagementModule(Set<String> managementModules, String module) {
        return AuthApiHolder.getVerificationPermissionApi().isManagementModule(managementModules, module);
    }

    public static Boolean isManagementHomepage(Set<String> managementHomepageModules, String module) {
        return AuthApiHolder.getVerificationPermissionApi().isManagementHomepage(managementHomepageModules, module);
    }

    public static Boolean isManagementMenu(Set<String> managementMenus, String module, String name) {
        return AuthApiHolder.getVerificationPermissionApi().isManagementMenu(managementMenus, module, name);
    }

    public static Boolean isManagementAction(Set<String> accessActions, String model, String name) {
        return AuthApiHolder.getVerificationPermissionApi().isManagementAction(accessActions, model, name);
    }

    public static Boolean isManagementAction(Set<String> accessActions, String path) {
        return AuthApiHolder.getVerificationPermissionApi().isManagementAction(accessActions, path);
    }
}