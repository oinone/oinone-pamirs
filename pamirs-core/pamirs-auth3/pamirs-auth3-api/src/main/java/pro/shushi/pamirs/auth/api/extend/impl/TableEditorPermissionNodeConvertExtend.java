package pro.shushi.pamirs.auth.api.extend.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.extend.load.PermissionNodeConvertExtendApi;
import pro.shushi.pamirs.auth.api.helper.AuthExtendHelper;
import pro.shushi.pamirs.auth.api.loader.visitor.AuthCompileContext;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.meta.constant.FunctionConstants;

/**
 * 表格编辑权限节点转换扩展
 *
 * @author Adamancy Zhang at 11:11 on 2025-03-24
 */
@Component
@Order(88)
public class TableEditorPermissionNodeConvertExtend implements PermissionNodeConvertExtendApi {

    private static final String ADD_ONE = "$$internal_AddOne";

    private static final String COPY_ONE = "$$internal_CopyOne";

    @Override
    public void convertActionNode(ActionPermissionNode node, AuthCompileContext context, UIAction actionNode, Action action) {
        if (action.getActionType() == ActionTypeEnum.CLIENT) {
            String model = action.getModel();
            String actionFun = actionNode.getFun();
            if (ADD_ONE.equals(actionFun) || COPY_ONE.equals(actionFun)) {
                AuthExtendHelper.addExtendActionNode(node, AuthExtendHelper.createExtendActionNode(context, model, FunctionConstants.create));
            }
        }
    }
}
