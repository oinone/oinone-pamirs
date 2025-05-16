package pro.shushi.pamirs.auth.api.helper;

import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.loader.visitor.AuthCompileContext;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;

/**
 * 权限扩展帮助类
 *
 * @author Adamancy Zhang at 09:57 on 2024-03-08
 */
public class AuthExtendHelper {

    private AuthExtendHelper() {
        // reject create object
    }

    public static ActionPermissionNode createExtendActionNode(AuthCompileContext context, String model, String actionName) {
        Action action = CommonApiFactory.getApi(MetaCacheManager.class).fetchAction(model, actionName);
        if (action == null) {
            return null;
        }
        boolean isFill = true;
        if (action.getId() == null) {
            ServerAction dbAction = Models.origin().queryOneByWrapper(Pops.<ServerAction>lambdaQuery()
                    .from(ServerAction.MODEL_MODEL)
                    .select(ServerAction::getId)
                    .eq(ServerAction::getModel, action.getModel())
                    .eq(ServerAction::getName, action.getName()));
            if (dbAction == null) {
                isFill = false;
            } else {
                action.setId(dbAction.getId());
            }
        }
        if (isFill) {
            return AuthNodeConvertHelper.convertActionNode(context, null, action);
        }
        return null;
    }

    public static void addExtendActionNode(ActionPermissionNode node, ActionPermissionNode target) {
        if (target != null) {
            node.setHasNext(Boolean.TRUE);
            node.getNodes().add(target);
        }
    }
}
