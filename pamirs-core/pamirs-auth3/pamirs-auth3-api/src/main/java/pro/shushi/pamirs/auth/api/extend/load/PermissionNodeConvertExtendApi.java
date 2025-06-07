package pro.shushi.pamirs.auth.api.extend.load;

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
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 权限节点转换扩展API
 *
 * @author Adamancy Zhang at 09:19 on 2024-03-08
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface PermissionNodeConvertExtendApi {

    default void convertModuleNode(ModulePermissionNode node, UeModule module) {
    }

    default void convertHomepageNode(HomepagePermissionNode node, UeModule module) {
    }

    default void convertHomepageNode(HomepagePermissionNode node, UeModule module, ViewAction action) {
    }

    default void convertMenuNode(MenuPermissionNode node, Menu menu) {
    }

    default void convertActionNode(ActionPermissionNode node, AuthCompileContext context, UIAction actionNode, Action action) {
    }
}
