package pro.shushi.pamirs.auth.api.loader.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.loader.ActionPermissionNodeLoader;
import pro.shushi.pamirs.auth.api.loader.visitor.AuthCompileContext;
import pro.shushi.pamirs.auth.api.loader.visitor.AuthCompileVisitor;
import pro.shushi.pamirs.auth.api.loader.visitor.DslParser;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.utils.AuthAuthorizationHelper;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 默认动作权限节点加载器
 *
 * @author Adamancy Zhang at 11:54 on 2024-01-16
 */
@Slf4j
@Component
public class DefaultActionPermissionNodeLoader implements ActionPermissionNodeLoader {

    @Autowired
    protected MetaCacheManager metaCacheManager;

    @Override
    public List<ActionPermissionNode> buildActionNodes(PermissionNode node, Menu menu) {
        ViewAction viewAction = menu.getViewAction();
        if (viewAction == null || viewAction.getId() == null) {
            log.error("Invalid the view action of the menu. menuId: {}, module: {}, menuName: {}", menu.getId(), menu.getModule(), menu.getName());
            return new ArrayList<>();
        }
        View mainView = fetchMainView(viewAction);
        if (mainView == null) {
            return new ArrayList<>();
        }
        String template = mainView.getTemplate();
        if (StringUtils.isBlank(template)) {
            return new ArrayList<>();
        }
        Long groupId = node.getGroupId();
        Map<String, AuthResourceAuthorization> authorizationMap = null;
        if (groupId != null) {
            authorizationMap = AuthAuthorizationHelper.fetchActionAuthorizationMap(groupId);
        }
        return compileByMenu(mainView, menu, viewAction, authorizationMap);
    }

    @Override
    public List<ActionPermissionNode> buildActionNodes(PermissionNode node, ViewAction viewAction) {
        View mainView = fetchMainView(viewAction);
        if (mainView == null) {
            return new ArrayList<>();
        }
        String template = mainView.getTemplate();
        if (StringUtils.isBlank(template)) {
            return new ArrayList<>();
        }
        Long groupId = node.getGroupId();
        Map<String, AuthResourceAuthorization> authorizationMap = null;
        if (groupId != null) {
            authorizationMap = AuthAuthorizationHelper.fetchActionAuthorizationMap(groupId);
        }
        return compileByViewAction(mainView, viewAction, authorizationMap);
    }

    @Override
    public List<ActionPermissionNode> buildActionNodes(PermissionNode node, UeModule module, ViewAction homepageAction) {
        View mainView = fetchMainView(homepageAction);
        if (mainView == null) {
            return new ArrayList<>();
        }
        String template = mainView.getTemplate();
        if (StringUtils.isBlank(template)) {
            return new ArrayList<>();
        }
        Long groupId = node.getGroupId();
        Map<String, AuthResourceAuthorization> authorizationMap = null;
        if (groupId != null) {
            authorizationMap = AuthAuthorizationHelper.fetchActionAuthorizationMap(groupId);
        }
        return compileByHomepage(mainView, module, homepageAction, authorizationMap);
    }

    protected View fetchMainView(ViewAction viewAction) {
        String resModel = Optional.ofNullable(viewAction.getResModel()).orElse(viewAction.getModel());
        View resView = viewAction.getResView();
        View mainView;
        if (resView == null || resView.getTemplate() == null) {
            mainView = metaCacheManager.fetchView(resModel, viewAction.getResViewName(), viewAction.getViewType(), false);
        } else {
            mainView = resView;
        }
        return mainView;
    }

    protected List<ActionPermissionNode> compileByHomepage(View mainView, UeModule module, ViewAction homepageAction, Map<String, AuthResourceAuthorization> authorizationMap) {
        String actionName = homepageAction.getName();

        AccessResourceInfo info = new AccessResourceInfo();
        info.setModule(module.getModule());
        info.setModel(homepageAction.getModel());
        info.setHomepage(actionName);
        info.setActionName(actionName);
        info.setViewAction(homepageAction);
        return compile(info, mainView, authorizationMap);
    }

    protected List<ActionPermissionNode> compileByMenu(View mainView, Menu menu, ViewAction viewAction, Map<String, AuthResourceAuthorization> authorizationMap) {
        AccessResourceInfo info = new AccessResourceInfo();
        info.setModule(menu.getModule());
        info.setModel(viewAction.getModel());
        info.setActionName(viewAction.getName());
        info.setMenu(menu.getName());
        info.setViewAction(viewAction);
        return compile(info, mainView, authorizationMap);
    }

    protected List<ActionPermissionNode> compileByViewAction(View mainView, ViewAction viewAction, Map<String, AuthResourceAuthorization> authorizationMap) {
        AccessResourceInfo info = new AccessResourceInfo();
        info.setModule(viewAction.getModule());
        info.setModel(viewAction.getModel());
        info.setActionName(viewAction.getName());
        info.setViewAction(viewAction);
        return compile(info, mainView, authorizationMap);
    }

    protected List<ActionPermissionNode> compile(AccessResourceInfo info, View mainView, Map<String, AuthResourceAuthorization> authorizationMap) {
        if (log.isDebugEnabled()) {
            log.debug("auth compile view. model: {}, name: {}", mainView.getModel(), mainView.getName());
        }
        AuthCompileContext context = new AuthCompileContext(info, authorizationMap);
        AuthCompileVisitor visitor = new AuthCompileVisitor(context);
        try {
            DslParser.visit(mainView, visitor);
        } catch (Throwable e) {
            log.error("compile view error. model: {}, name: {}", mainView.getModel(), mainView.getName(), e);
        }
        // FIXME: zbh 20240308 此处使用策略展示「全部」动作
//        List<ActionPermissionNode> nodes = visitor.getNodes();
//        if (nodes.isEmpty()) {
//            return nodes;
//        }
//        ActionPermissionNode allActionNode = AuthNodeConvertHelper.buildAllActionNode(context);
//        if (allActionNode != null) {
//            nodes.add(0, allActionNode);
//        }
//        return nodes;
        return visitor.getNodes();
    }
}
