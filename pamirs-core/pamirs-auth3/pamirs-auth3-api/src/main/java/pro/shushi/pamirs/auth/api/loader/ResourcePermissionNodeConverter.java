package pro.shushi.pamirs.auth.api.loader;

import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.HomepagePermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.MenuPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.ModulePermissionNode;
import pro.shushi.pamirs.auth.api.loader.visitor.AuthCompileContext;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;

/**
 * 资源权限节点转换
 *
 * @author Adamancy Zhang at 13:10 on 2024-02-04
 */
public interface ResourcePermissionNodeConverter {

    /**
     * 模块节点转换
     *
     * @param module 模块
     * @return 模块权限节点
     */
    ModulePermissionNode convertModuleNode(UeModule module);

    /**
     * 模块首页节点转换（无首页动作）
     *
     * @param module 模块
     * @return 首页权限节点
     */
    HomepagePermissionNode convertHomepageNode(UeModule module);

    /**
     * 模块首页节点转换（有首页动作）
     *
     * @param module 模块
     * @param action 首页动作
     * @return 首页权限节点
     */
    HomepagePermissionNode convertHomepageNode(UeModule module, ViewAction action);

    /**
     * 菜单节点转换
     *
     * @param menu 菜单
     * @return 菜单权限节点
     */
    MenuPermissionNode convertMenuNode(Menu menu);

    /**
     * 动作节点转换
     *
     * @param context 编译上下文
     * @param action  动作
     * @return 动作权限节点
     */
    ActionPermissionNode convertActionNode(AuthCompileContext context, UIAction actionNode, Action action);

    /**
     * 「全部」动作节点转换
     *
     * @param context 编译上下文
     * @return 「全部」动作权限节点
     */
    ActionPermissionNode buildAllActionNode(AuthCompileContext context);
}
