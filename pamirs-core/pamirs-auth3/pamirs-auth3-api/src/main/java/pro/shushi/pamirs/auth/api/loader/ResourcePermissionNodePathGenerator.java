package pro.shushi.pamirs.auth.api.loader;

import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.HomepagePermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.MenuPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.ModulePermissionNode;
import pro.shushi.pamirs.auth.api.loader.visitor.AuthCompileContext;

/**
 * 资源权限节点路径生成器
 *
 * @author Adamancy Zhang at 12:51 on 2024-02-04
 */
public interface ResourcePermissionNodePathGenerator {

    /**
     * 生成模块路径
     *
     * @param moduleNode 模块权限节点
     * @return 路径
     */
    String generatorModulePath(ModulePermissionNode moduleNode);

    /**
     * 生成首页路径
     *
     * @param homepageNode 首页权限节点
     * @return 路径
     */
    String generatorHomepagePath(HomepagePermissionNode homepageNode);

    /**
     * 生成菜单路径
     *
     * @param menuNode 菜单节点
     * @return 路径
     */
    String generatorMenuPath(MenuPermissionNode menuNode);

    /**
     * 生成菜单路径
     *
     * @param moduleNode 模块权限节点
     * @param menuNode   菜单权限节点
     * @return 路径
     */
    String generatorMenuPath(ModulePermissionNode moduleNode, MenuPermissionNode menuNode);

    /**
     * 生成菜单路径
     *
     * @param moduleNode     模块权限节点
     * @param menuNode       菜单权限节点
     * @param parentMenuNode 上级菜单权限节点
     * @return 路径
     */
    String generatorMenuPath(ModulePermissionNode moduleNode, MenuPermissionNode menuNode, MenuPermissionNode parentMenuNode);

    /**
     * 生成动作路径
     *
     * @param context    当前编译上下文
     * @param actionNode 动作权限节点
     * @return 路径
     */
    String generatorActionPath(AuthCompileContext context, ActionPermissionNode actionNode);

    /**
     * 生成「全部」动作路径
     *
     * @param context    当前编译上下文
     * @param actionNode 动作权限节点
     * @return 路径
     */
    String generatorAllActionPath(AuthCompileContext context, ActionPermissionNode actionNode);
}
