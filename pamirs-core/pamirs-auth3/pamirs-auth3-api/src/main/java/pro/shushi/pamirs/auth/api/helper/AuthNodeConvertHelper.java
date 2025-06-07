package pro.shushi.pamirs.auth.api.helper;

import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.HomepagePermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.MenuPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.ModulePermissionNode;
import pro.shushi.pamirs.auth.api.loader.PermissionNodeLoader;
import pro.shushi.pamirs.auth.api.loader.ResourcePermissionNodeConverter;
import pro.shushi.pamirs.auth.api.loader.visitor.AuthCompileContext;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.core.common.entry.Holder;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

/**
 * 权限节点转换帮助类
 *
 * @author Adamancy Zhang at 09:30 on 2024-03-08
 */
public class AuthNodeConvertHelper {

    private static final Holder<ResourcePermissionNodeConverter> holder = new Holder<>();

    private AuthNodeConvertHelper() {
        // reject create object
    }

    /**
     * 模块节点转换
     *
     * @param module 模块
     * @return 模块权限节点
     */
    public static ModulePermissionNode convertModuleNode(UeModule module) {
        return getNodeConverter().convertModuleNode(module);
    }

    /**
     * 模块首页节点转换（无首页动作）
     *
     * @param module 模块
     * @return 首页权限节点
     */
    public static HomepagePermissionNode convertHomepageNode(UeModule module) {
        return getNodeConverter().convertHomepageNode(module);
    }

    /**
     * 模块首页节点转换（有首页动作）
     *
     * @param module 模块
     * @param action 首页动作
     * @return 首页权限节点
     */
    public static HomepagePermissionNode convertHomepageNode(UeModule module, ViewAction action) {
        return getNodeConverter().convertHomepageNode(module, action);
    }

    /**
     * 菜单节点转换
     *
     * @param menu 菜单
     * @return 菜单权限节点
     */
    public static MenuPermissionNode convertMenuNode(Menu menu) {
        return getNodeConverter().convertMenuNode(menu);
    }

    /**
     * 动作节点转换
     *
     * @param context 编译上下文
     * @param action  动作
     * @return 动作权限节点
     */
    public static ActionPermissionNode convertActionNode(AuthCompileContext context, UIAction actionNode, Action action) {
        return getNodeConverter().convertActionNode(context, actionNode, action);
    }

    /**
     * 「全部」动作节点转换
     *
     * @param context 编译上下文
     * @return 「全部」动作权限节点
     */
    public static ActionPermissionNode buildAllActionNode(AuthCompileContext context) {
        return getNodeConverter().buildAllActionNode(context);
    }

    public static ResourcePermissionNodeConverter getNodeConverter() {
        ResourcePermissionNodeConverter converter = holder.get();
        if (converter == null) {
            synchronized (AuthNodeConvertHelper.class) {
                converter = holder.get();
                if (converter == null) {
                    converter = BeanDefinitionUtils.getBean(PermissionNodeLoader.class).getNodeConverter();
                    holder.set(converter);
                }
            }
        }
        return converter;
    }
}
