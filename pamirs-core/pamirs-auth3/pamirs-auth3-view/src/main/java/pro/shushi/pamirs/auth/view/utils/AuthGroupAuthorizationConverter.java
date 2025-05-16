package pro.shushi.pamirs.auth.view.utils;

import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;
import pro.shushi.pamirs.auth.view.model.AuthActionPermissionItem;
import pro.shushi.pamirs.auth.view.model.AuthFieldPermissionItem;
import pro.shushi.pamirs.auth.view.model.AuthRowPermissionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限组授权转换
 *
 * @author Adamancy Zhang at 14:45 on 2024-06-27
 */
public class AuthGroupAuthorizationConverter {

    private AuthGroupAuthorizationConverter() {
        // reject create object
    }

    public static List<AuthResourceAuthorization> convertResourceAuthorizations(List<AuthActionPermissionItem> actionPermissions) {
        if (actionPermissions == null) {
            return null;
        }
        List<AuthResourceAuthorization> resourceAuthorizations = new ArrayList<>();
        for (AuthActionPermissionItem actionPermission : actionPermissions) {
            AuthResourceAuthorization resourceAuthorization = AuthResourceAuthorization.transfer(actionPermission, new AuthResourceAuthorization());
            resourceAuthorization.setAuthorizedValue(AuthGroupAuthorizationComputeHelper.getActionAuthorizedValue(actionPermission));
            resourceAuthorizations.add(resourceAuthorization);
        }
        return resourceAuthorizations;
    }

    public static List<AuthFieldAuthorization> convertFieldAuthorizations(List<AuthFieldPermissionItem> fieldPermissionItems) {
        if (fieldPermissionItems == null) {
            return null;
        }
        List<AuthFieldAuthorization> fieldAuthorizations = new ArrayList<>();
        for (AuthFieldPermissionItem fieldPermissionItem : fieldPermissionItems) {
            AuthFieldAuthorization fieldAuthorization = AuthFieldAuthorization.transfer(fieldPermissionItem, new AuthFieldAuthorization());
            fieldAuthorization.setAuthorizedValue(AuthGroupAuthorizationComputeHelper.getFieldAuthorizedValue(fieldPermissionItem));
            fieldAuthorizations.add(fieldAuthorization);
        }
        return fieldAuthorizations;
    }

    public static List<AuthRowAuthorization> convertRowAuthorizations(List<AuthRowPermissionItem> rowPermissionItems) {
        if (rowPermissionItems == null) {
            return null;
        }
        List<AuthRowAuthorization> rowAuthorizations = new ArrayList<>();
        for (AuthRowPermissionItem rowPermissionItem : rowPermissionItems) {
            AuthRowAuthorization rowAuthorization = AuthRowAuthorization.transfer(rowPermissionItem, new AuthRowAuthorization());
            rowAuthorization.setAuthorizedValue(AuthGroupAuthorizationComputeHelper.getRowPermissionAuthorizedValue(rowPermissionItem));
            rowAuthorizations.add(rowAuthorization);
        }
        return rowAuthorizations;
    }

    public static AuthRowAuthorization convertRowAuthorization(AuthRowPermissionItem rowPermissionItem) {
        if (rowPermissionItem == null) {
            return null;
        }
        rowPermissionItem.setFilter(rowPermissionItem.getDomainExp());
        AuthRowAuthorization rowAuthorization = AuthRowAuthorization.transfer(rowPermissionItem, new AuthRowAuthorization());
        rowAuthorization.setAuthorizedValue(AuthGroupAuthorizationComputeHelper.getRowPermissionAuthorizedValue(rowPermissionItem));
        return rowAuthorization;
    }
}
