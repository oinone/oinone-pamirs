package pro.shushi.pamirs.auth.api.loader;

import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.runtime.session.AuthRoleSession;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;

import java.util.List;
import java.util.Set;

/**
 * 权限节点加载器
 *
 * @author Adamancy Zhang at 09:47 on 2024-01-15
 */
public interface ResourcePermissionNodeLoader {

    /**
     * 加载当前用户的全部资源权限项
     *
     * @return 可管理权限节点集合
     */
    default List<PermissionNode> buildAllPermissions() {
        return buildAllPermissions(AuthRoleSession.getCurrentRoles());
    }

    /**
     * 加载指定角色的全部资源权限项
     *
     * @param roleIds 可访问判定角色ID集合
     * @return 可管理权限节点集合
     */
    List<PermissionNode> buildAllPermissions(Set<Long> roleIds);

    /**
     * 加载根资源权限项
     *
     * @return 可管理权限节点集合
     */
    List<PermissionNode> buildRootPermissions();

    /**
     * 加载下级资源权限项
     *
     * @param selected 当前选中节点
     * @return 可管理权限节点集合
     */
    List<PermissionNode> buildNextPermissions(PermissionNode selected);

    /**
     * 加载「首页」下级资源权限项
     *
     * @param selected       当前选中节点
     * @param module         模块
     * @param homepageAction 首页动作
     * @return 可管理权限节点集合
     */
    List<PermissionNode> buildNextPermissionsByHomepage(PermissionNode selected, UeModule module, ViewAction homepageAction);

    /**
     * 加载「菜单」下级资源权限项
     *
     * @param selected       当前选中节点
     * @param menu           菜单
     * @param menuViewAction 菜单动作
     * @return 可管理权限节点集合
     */
    List<PermissionNode> buildNextPermissionsByMenu(PermissionNode selected, Menu menu, ViewAction menuViewAction);

    /**
     * 加载「跳转动作」下级资源权限项
     *
     * @param selected   当前选中节点
     * @param viewAction 跳转动作
     * @return 可管理权限节点集合
     */
    List<PermissionNode> buildNextPermissionsByViewAction(PermissionNode selected, ViewAction viewAction);
}
