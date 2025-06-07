package pro.shushi.pamirs.auth.view;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.AuthRoleType;
import pro.shushi.pamirs.auth.view.pmodel.AuthGroupSystemPermissionProxy;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenu;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenus;
import pro.shushi.pamirs.core.common.constant.CommonConstants;

/**
 * 权限菜单定义
 *
 * @author Adamancy Zhang at 12:16 on 2024-01-09
 */
@UxMenus(module = CommonConstants.MANAGEMENT_CENTER_MODULE, basePriority = 300)
class AuthMenus {

    @UxMenu(name = "AuthMenus_RoleAndPermission", value = "角色与权限")
    class RoleAndPermission {

        @UxMenu("角色类型")
        @UxRoute(model = AuthRoleType.MODEL_MODEL, viewName = "AuthRoleTypeTable")
        class RoleTypeManagement {
        }

        @UxMenu("角色管理")
        @UxRoute(model = AuthRole.MODEL_MODEL, viewName = "AuthRoleTable")
        class RoleManagement {
        }

        @UxMenu("系统权限")
        @UxRoute(model = AuthGroupSystemPermissionProxy.MODEL_MODEL, viewName = "AuthGroupSystemPermission")
        class SystemPermission {
        }

//        @UxMenu("资源权限")
//        @UxRoute(model = AuthGroupResourcePermissionProxy.MODEL_MODEL, viewName = "AuthGroupResourcePermissionTable")
//        class ResourcePermission {
//        }


//        @UxMenu("自定义权限")
//        class CustomPermission {
//
//            @UxMenu("自定义权限组")
//            @UxRoute(model = AuthCustomGroup.MODEL_MODEL, viewName = "AuthCustomGroupTable")
//            class CustomGroup {
//            }
//
//            @UxMenu("自定义权限项")
//            @UxRoute(model = AuthCustomResourcePermissionItem.MODEL_MODEL, viewName = "AuthCustomResourcePermissionItemTable")
//            class CustomResourcePermissionItem {
//            }
//        }
    }
}
