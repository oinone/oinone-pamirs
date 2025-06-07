package pro.shushi.pamirs.auth.api.loader;

import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;

import java.util.List;

/**
 * 动作权限加载器
 *
 * @author Adamancy Zhang at 11:52 on 2024-01-16
 */
public interface ActionPermissionNodeLoader {

    /**
     * 构建基于首页的动作权限节点
     *
     * @param node           当前权限节点
     * @param module         指定模块
     * @param homepageAction 指定首页动作
     * @return 动作权限节点列表
     */
    List<ActionPermissionNode> buildActionNodes(PermissionNode node, UeModule module, ViewAction homepageAction);

    /**
     * 构建基于菜单的动作权限节点
     *
     * @param node 当前权限节点
     * @param menu 指定菜单
     * @return 动作权限节点列表
     */
    List<ActionPermissionNode> buildActionNodes(PermissionNode node, Menu menu);

    /**
     * 构建基于跳转动作的动作权限节点
     *
     * @param node       当前权限节点
     * @param viewAction 指定跳转动作
     * @return 动作权限节点列表
     */
    List<ActionPermissionNode> buildActionNodes(PermissionNode node, ViewAction viewAction);

}
