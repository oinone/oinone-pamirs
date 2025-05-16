package pro.shushi.pamirs.auth.api.extend.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.extend.load.PermissionNodeLoadExtendApi;
import pro.shushi.pamirs.auth.api.helper.AuthHelper;
import pro.shushi.pamirs.auth.api.helper.AuthNodeHelper;
import pro.shushi.pamirs.auth.api.loader.entity.PermissionLoadContext;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.core.common.VerificationHelper;
import pro.shushi.pamirs.core.common.enmu.TopBarActionType;
import pro.shushi.pamirs.core.common.entry.TopBarAction;
import pro.shushi.pamirs.core.common.path.ResourcePathParser;
import pro.shushi.pamirs.core.common.query.QueryActionCollection;
import pro.shushi.pamirs.core.common.spi.TopBarActionExtendApi;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.*;

/**
 * 顶部栏节点加载扩展
 *
 * @author Adamancy Zhang at 09:29 on 2024-02-28
 */
@Component
@Order(88)
public class TopBarPermissionNodeLoadExtend implements PermissionNodeLoadExtendApi {

    @Autowired
    protected AuthAccessService authAccessService;

    @Autowired
    private ResourcePathParser resourcePathParser;

    @Override
    public List<PermissionNode> buildRootPermissions(PermissionLoadContext loadContext, List<PermissionNode> nodes) {
        List<TopBarAction> topBarActions = TopBarAction.getDefaultActions();
        TopBarActionExtendApi extendApi = Spider.getDefaultExtension(TopBarActionExtendApi.class);
        extendApi.edit(topBarActions);

        TopBarAction userAvatar = null;
        Map<String, TopBarAction> queryActionMap = new LinkedHashMap<>(topBarActions.size());
        for (TopBarAction topBarAction : topBarActions) {
            String sign = Action.sign(topBarAction.getModel(), topBarAction.getName());
            VerificationHelper.setDefaultValue(topBarAction, TopBarAction::getType, TopBarAction::setType, TopBarActionType.NORMAL);

            String sessionPath = AuthHelper.generatorTopBarActionPath(topBarAction);
            topBarAction.setSessionPath(sessionPath);
            topBarAction.setInfo(resourcePathParser.parseAccessInfo(sessionPath));

            if (TopBarActionType.USER_AVATAR.equals(topBarAction.getType())) {
                userAvatar = topBarAction;
            } else {
                queryActionMap.put(sign, topBarAction);
            }
        }

        QueryActionCollection queryCollection = new QueryActionCollection();

        if (userAvatar != null && auth(userAvatar)) {
            addAction(queryCollection, userAvatar);
        }

        queryActionMap.values().forEach(topBarAction -> {
            if (auth(topBarAction)) {
                addAction(queryCollection, topBarAction);
            }
        });

        queryCollection.fill();

        PermissionNode root = createTopBarNode();

        List<PermissionNode> newNodes = new ArrayList<>();

        if (userAvatar != null) {
            Action action = getAction(queryCollection, userAvatar);
            if (action != null) {
                AuthNodeHelper.addNode(newNodes, root, createActionNode(action, root, userAvatar));
            }
        }

        Map<Integer, ActionGroup> groups = new HashMap<>(4);
        for (TopBarAction topBarAction : queryActionMap.values()) {
            if (!TopBarActionType.NORMAL.equals(topBarAction.getType())) {
                continue;
            }
            Action action = getAction(queryCollection, topBarAction);
            if (action == null) {
                continue;
            }
            groups.computeIfAbsent(topBarAction.getGroupOrder(), ActionGroup::new).addAction(action, topBarAction);
        }
        groups.values().stream().sorted(Comparator.comparing(ActionGroup::getPriority))
                .forEach(group -> createActionNodes(newNodes, group, root));

        if (!root.getNodes().isEmpty()) {
            nodes.add(0, root);
            return newNodes;
        }
        return null;
    }

    private boolean auth(TopBarAction action) {
        if (Boolean.TRUE.equals(action.getAuthIgnored())) {
            return false;
        }
        AccessResourceInfo info = action.getInfo();
        if (info == null) {
            return authAccessService.canManagementAction(action.getModel(), action.getName()).getSuccess();
        }
        return authAccessService.canManagementAction(action.getSessionPath()).getSuccess();
    }

    private void addAction(QueryActionCollection queryCollection, TopBarAction topBarAction) {
        queryCollection.add(topBarAction.getModel(), topBarAction.getName(), topBarAction.getActionType());
    }

    private <T extends Action> T getAction(QueryActionCollection queryCollection, TopBarAction topBarAction) {
        return queryCollection.get(topBarAction.getModel(), topBarAction.getName(), topBarAction.getActionType());
    }

    private PermissionNode createTopBarNode() {
        return AuthNodeHelper.createNodeWithTranslate("TopBarNode", "顶部栏");
    }

    private ActionPermissionNode createActionNode(Action action, PermissionNode parentNode, TopBarAction topBarAction) {
        ActionPermissionNode node = AuthNodeHelper.createActionNode(topBarAction.getModule(), action, parentNode);
        if (node == null) {
            return null;
        }
        node.setPath(topBarAction.getSessionPath());
        return node;
    }

    private void createActionNodes(List<PermissionNode> nodes, ActionGroup group, PermissionNode parentNode) {
        List<Action> actions = group.getActions();
        List<TopBarAction> topBarActions = group.getTopBarActions();
        for (int i = 0; i < actions.size(); i++) {
            Action action = actions.get(i);
            TopBarAction topBarAction = topBarActions.get(i);
            AuthNodeHelper.addNode(nodes, parentNode, createActionNode(action, parentNode, topBarAction));
        }
    }

    private static class ActionGroup {

        private final List<Action> actions;

        private final List<TopBarAction> topBarActions;

        private final int priority;

        public ActionGroup(int priority) {
            this.actions = new ArrayList<>(4);
            this.topBarActions = new ArrayList<>(4);
            this.priority = priority;
        }

        public List<Action> getActions() {
            return actions;
        }

        public List<TopBarAction> getTopBarActions() {
            return topBarActions;
        }

        public int getPriority() {
            return priority;
        }

        public void addAction(Action action, TopBarAction topBarAction) {
            this.actions.add(action);
            this.topBarActions.add(topBarAction);
        }
    }
}
