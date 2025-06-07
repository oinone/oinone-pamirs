package pro.shushi.pamirs.apps.api.auth;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.apps.api.pmodel.AppsManagementModule;
import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.extend.load.PermissionNodeConvertExtendApi;
import pro.shushi.pamirs.auth.api.loader.visitor.AuthCompileContext;
import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;

/**
 * 应用中心权限节点转换扩展
 *
 * @author Adamancy Zhang at 09:20 on 2024-05-09
 */
@Order
@Component
public class AppsPermissionNodeConverterExtend implements PermissionNodeConvertExtendApi {

    @Override
    public void convertActionNode(ActionPermissionNode node, AuthCompileContext context, UIAction actionNode, Action action) {
        ViewAction currentViewAction = context.getCurrentViewAction();
        if (!AppsManagementModule.MODEL_MODEL.equals(currentViewAction.getModel()) ||
                !ViewActionConstants.homepage.name.equals(currentViewAction.getName())) {
            return;
        }
        String model = action.getModel();
        String name = action.getName();
        if ("designer.DesignerModelDefinition".equals(model) && "homepage".equals(name)) {
            node.setIgnoreChildren(true);
        } else if ("ui.designer.UiDesignerView".equals(model) && "homepage".equals(name)) {
            node.setIgnoreChildren(true);
        } else if ("paas.codeFuse.CodeFuseModuleExtProjectDisplay".equals(model) && "PaasMenus_Menu_LowCodeMenu".equals(name)) {
            node.setIgnoreChildren(true);
        }
    }
}
