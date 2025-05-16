package pro.shushi.pamirs.translate.auth;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.extend.load.PermissionNodeLoadExtendApi;
import pro.shushi.pamirs.auth.api.helper.AuthNodeHelper;
import pro.shushi.pamirs.auth.api.loader.entity.PermissionLoadContext;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.core.common.query.QueryActions;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.translate.TranslateModule;
import pro.shushi.pamirs.translate.action.ResourceTranslationAction;

import java.util.ArrayList;
import java.util.List;

/**
 * 翻译工具栏权限节点加载扩展
 *
 * @author Adamancy Zhang at 15:57 on 2025-02-17
 */
@Order(84)
@Component
public class TranslatePermissionNodeLoadExtend implements PermissionNodeLoadExtendApi {

    private static final String CREATE_FUN = LambdaUtil.fetchMethodName(ResourceTranslationAction::create);

    private static final String CREATE_AND_REFRESH_FUN = LambdaUtil.fetchMethodName(ResourceTranslationAction::createOrUpdateAndRefreshBatch);

    @Override
    public List<PermissionNode> buildRootPermissions(PermissionLoadContext loadContext, List<PermissionNode> nodes) {
        PermissionNode root = createTranslateToolNode();

        List<PermissionNode> newNodes = new ArrayList<>();

        QueryActions<ServerAction> queryActions = new QueryActions<>(ActionTypeEnum.SERVER);
        queryActions.add(ResourceTranslation.MODEL_MODEL, CREATE_FUN);
        queryActions.add(ResourceTranslation.MODEL_MODEL, CREATE_AND_REFRESH_FUN);
        List<ServerAction> serverActions = queryActions.query();
        for (ServerAction serverAction : serverActions) {
            AuthNodeHelper.addNode(newNodes, root, createActionNode(serverAction, root));
        }

        if (!root.getNodes().isEmpty()) {
            nodes.add(0, root);
            return newNodes;
        }
        return null;
    }

    private PermissionNode createTranslateToolNode() {
        return AuthNodeHelper.createNodeWithTranslate("TranslateToolNode", "翻译工具箱");
    }

    private PermissionNode createActionNode(ServerAction serverAction, PermissionNode parentNode) {
        ActionPermissionNode node = AuthNodeHelper.createActionNode(TranslateModule.MODULE_MODULE, serverAction, parentNode);
        if (node == null) {
            return null;
        }
        AccessResourceInfo info = new AccessResourceInfo();
        info.setModel(serverAction.getModel());
        info.setActionName(serverAction.getName());
        node.setPath(info.toString());
        return node;
    }
}
