package pro.shushi.pamirs.auth.api.helper;

import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.core.common.TranslateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <h>权限节点帮助类</h>
 * <p>
 * 此帮助类所有方法均不使用节点加载策略相关内容，仅提供特定节点的简单创建逻辑
 * <br>
 * 如需使用节点转换，请查看{@link AuthNodeConvertHelper}相关内容
 * </p>
 *
 * @author Adamancy Zhang at 17:20 on 2024-03-02
 */
public class AuthNodeHelper {

    private AuthNodeHelper() {
        // reject create object
    }

    public static PermissionNode createNode(String id, String displayValue) {
        PermissionNode node = new PermissionNode();
        node.setId(id);
        node.setHasNext(Boolean.TRUE);
        node.setCanAccess(Boolean.FALSE);
        node.setCanAllot(Boolean.FALSE);
        node.setDisplayValue(displayValue);
        node.setNodes(new ArrayList<>(8));
        return node;
    }

    public static PermissionNode createNode(String id, String displayValue, PermissionNode parentNode) {
        PermissionNode node = createNode(id, displayValue);
        if (parentNode != null) {
            node.setParentId(parentNode.getId());
            node.setParent(parentNode);
        }
        return node;
    }

    public static PermissionNode createNodeWithTranslate(String id, String displayValue) {
        return AuthNodeHelper.createNode(id, TranslateUtils.translateValues(displayValue));
    }

    public static PermissionNode createNodeWithTranslate(String id, String displayValue, PermissionNode parentNode) {
        return AuthNodeHelper.createNode(id, TranslateUtils.translateValues(displayValue), parentNode);
    }

    public static void addNode(List<PermissionNode> nodes, PermissionNode node, PermissionNode target) {
        if (target == null) {
            return;
        }
        boolean isAppendNode = true;
        String id = node.getId();
        for (PermissionNode item : nodes) {
            if (id.equals(item.getId())) {
                isAppendNode = false;
                break;
            }
        }
        if (isAppendNode) {
            nodes.add(node);
        }
        node.setHasNext(Boolean.TRUE);
        node.getNodes().add(target);
    }

    public static ActionPermissionNode createServerActionNode(String module, ServerAction action) {
        return createActionNode(module, action, null);
    }

    public static ActionPermissionNode createServerActionNode(String module, ServerAction action, PermissionNode parentNode) {
        return createActionNode(module, action, parentNode);
    }

    public static ActionPermissionNode createViewActionNode(String module, ViewAction action) {
        return createActionNode(module, action, null);
    }

    public static ActionPermissionNode createViewActionNode(String module, ViewAction action, PermissionNode parentNode) {
        return createActionNode(module, action, parentNode);
    }

    public static ActionPermissionNode createUrlActionNode(String module, UrlAction action) {
        return createActionNode(module, action, null);
    }

    public static ActionPermissionNode createUrlActionNode(String module, UrlAction action, PermissionNode parentNode) {
        return createActionNode(module, action, parentNode);
    }

    public static ActionPermissionNode createClientActionNode(String module, ClientAction action) {
        return createActionNode(module, action, null);
    }

    public static ActionPermissionNode createClientActionNode(String module, ClientAction action, PermissionNode parentNode) {
        return createActionNode(module, action, parentNode);
    }

    public static ActionPermissionNode createActionNode(String module, Action action) {
        return createActionNode(module, action, null);
    }

    public static ActionPermissionNode createActionNode(String module, Action action, PermissionNode parentNode) {
        if (action instanceof ServerAction) {
            return createActionNode(module, (ServerAction) action, parentNode);
        }
        if (action instanceof ViewAction) {
            return createActionNode(module, (ViewAction) action, parentNode);
        }
        if (action instanceof UrlAction) {
            return createActionNode(module, (UrlAction) action, parentNode);
        }
        if (action instanceof ClientAction) {
            return createActionNode(module, (ClientAction) action, parentNode);
        }
        return null;
    }

    private static ActionPermissionNode createActionNode(String module, ServerAction action, PermissionNode parentNode) {
        ActionPermissionNode node = createActionNode0(module, action, parentNode);
        node.setNodeType(ResourcePermissionSubtypeEnum.SERVER_ACTION);
        return node;
    }

    private static ActionPermissionNode createActionNode(String module, ViewAction action, PermissionNode parentNode) {
        ActionPermissionNode node = createActionNode0(module, action, parentNode);
        node.setNodeType(ResourcePermissionSubtypeEnum.VIEW_ACTION);
        return node;
    }

    private static ActionPermissionNode createActionNode(String module, UrlAction action, PermissionNode parentNode) {
        ActionPermissionNode node = createActionNode0(module, action, parentNode);
        node.setNodeType(ResourcePermissionSubtypeEnum.URL_ACTION);
        return node;
    }

    private static ActionPermissionNode createActionNode(String module, ClientAction action, PermissionNode parentNode) {
        ActionPermissionNode node = createActionNode0(module, action, parentNode);
        node.setNodeType(ResourcePermissionSubtypeEnum.CLIENT_ACTION);
        return node;
    }

    private static ActionPermissionNode createActionNode0(String module, Action action, PermissionNode parentNode) {
        Long actionId = action.getId();

        ActionPermissionNode node = new ActionPermissionNode();
        node.setId(actionId.toString());
        node.setHasNext(Boolean.FALSE);
        node.setCanAccess(Boolean.FALSE);
        node.setCanAllot(Boolean.TRUE);
        node.setResourceId(actionId);
        node.setPath(ResourcePath.generatorPath(action.getModel(), action.getName()));
        node.setDisplayValue(AuthHelper.getActionDisplayValue(action));
        node.setModule(module);
        node.setModel(action.getModel());
        node.setAction(action.getName());
        node.setActionType(action.getActionType());

        if (parentNode != null) {
            node.setParentId(parentNode.getId());
            node.setParent(parentNode);
        }
        return node;
    }
}
