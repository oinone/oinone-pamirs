package pro.shushi.pamirs.auth.api.helper;

import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.HomepagePermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.MenuPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.ModulePermissionNode;
import pro.shushi.pamirs.auth.api.loader.PermissionNodeLoader;
import pro.shushi.pamirs.auth.api.loader.ResourcePermissionNodePathGenerator;
import pro.shushi.pamirs.auth.api.loader.visitor.AuthCompileContext;
import pro.shushi.pamirs.core.common.entry.Holder;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

/**
 * 权限路径生成帮助类
 *
 * @author Adamancy Zhang at 09:44 on 2024-03-08
 */
public class AuthPathGenerateHelper {

    private static final Holder<ResourcePermissionNodePathGenerator> holder = new Holder<>();

    private AuthPathGenerateHelper() {
        // reject create object
    }

    /**
     * 生成模块路径
     *
     * @param moduleNode 模块权限节点
     * @return 路径
     */
    public static String generatorModulePath(ModulePermissionNode moduleNode) {
        return getPathGenerator().generatorModulePath(moduleNode);
    }

    /**
     * 生成首页路径
     *
     * @param homepageNode 首页权限节点
     * @return 路径
     */
    public static String generatorHomepagePath(HomepagePermissionNode homepageNode) {
        return getPathGenerator().generatorHomepagePath(homepageNode);
    }

    /**
     * 生成菜单路径
     *
     * @param menuNode 菜单节点
     * @return 路径
     */
    public static String generatorMenuPath(MenuPermissionNode menuNode) {
        return getPathGenerator().generatorMenuPath(menuNode);
    }

    /**
     * 生成菜单路径
     *
     * @param moduleNode 模块权限节点
     * @param menuNode   菜单权限节点
     * @return 路径
     */
    public static String generatorMenuPath(ModulePermissionNode moduleNode, MenuPermissionNode menuNode) {
        return getPathGenerator().generatorMenuPath(moduleNode, menuNode);
    }

    /**
     * 生成菜单路径
     *
     * @param moduleNode     模块权限节点
     * @param menuNode       菜单权限节点
     * @param parentMenuNode 上级菜单权限节点
     * @return 路径
     */
    public static String generatorMenuPath(ModulePermissionNode moduleNode, MenuPermissionNode menuNode, MenuPermissionNode parentMenuNode) {
        return getPathGenerator().generatorMenuPath(moduleNode, menuNode, parentMenuNode);
    }

    /**
     * 生成动作路径
     *
     * @param context    当前编译上下文
     * @param actionNode 动作权限节点
     * @return 路径
     */
    public static String generatorActionPath(AuthCompileContext context, ActionPermissionNode actionNode) {
        return getPathGenerator().generatorActionPath(context, actionNode);
    }

    /**
     * 生成「全部」动作路径
     *
     * @param context    当前编译上下文
     * @param actionNode 动作权限节点
     * @return 路径
     */
    public static String generatorAllActionPath(AuthCompileContext context, ActionPermissionNode actionNode) {
        return getPathGenerator().generatorAllActionPath(context, actionNode);
    }

    public static ResourcePermissionNodePathGenerator getPathGenerator() {
        ResourcePermissionNodePathGenerator generator = holder.get();
        if (generator == null) {
            synchronized (AuthNodeConvertHelper.class) {
                generator = holder.get();
                if (generator == null) {
                    generator = BeanDefinitionUtils.getBean(PermissionNodeLoader.class).getPathGenerator();
                    holder.set(generator);
                }
            }
        }
        return generator;
    }
}
