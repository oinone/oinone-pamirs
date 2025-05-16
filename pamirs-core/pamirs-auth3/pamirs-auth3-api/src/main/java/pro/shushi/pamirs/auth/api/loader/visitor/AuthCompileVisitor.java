package pro.shushi.pamirs.auth.api.loader.visitor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.helper.AuthCompileHelper;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.loader.PermissionNodeLoader;
import pro.shushi.pamirs.auth.api.loader.ResourcePermissionNodeConverter;
import pro.shushi.pamirs.auth.api.runtime.spi.AccessPermissionApi;
import pro.shushi.pamirs.boot.base.constants.ClientActionConstants;
import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.boot.base.ux.model.view.UIField;
import pro.shushi.pamirs.boot.base.ux.model.view.UITemplate;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.core.common.ObjectHelper;
import pro.shushi.pamirs.core.common.query.QueryActions;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * DslNodeVisitor - 权限编译
 *
 * @author Adamancy Zhang at 13:42 on 2024-01-16
 */
@Slf4j
public class AuthCompileVisitor implements DslNodeVisitor {

    private final MetaCacheManager metaCacheManager;

    private final PermissionNodeLoader permissionNodeLoader;

    private final ResourcePermissionNodeConverter resourcePermissionNodeConverter;

    private final AccessPermissionApi accessPermissionApi;

    private final Stack<AuthCompileContext> contextStack;

    private final List<ActionPermissionNode> nodes;

    private final Set<String> repeatSet;

    private static final int FIRST_VIEW = 1;

    private static final int FIRST_VIEW_POP = 2;

    public AuthCompileVisitor(AuthCompileContext context) {
        this.metaCacheManager = BeanDefinitionUtils.getBean(MetaCacheManager.class);
        this.permissionNodeLoader = BeanDefinitionUtils.getBean(PermissionNodeLoader.class);
        this.resourcePermissionNodeConverter = this.permissionNodeLoader.getNodeConverter();
        this.accessPermissionApi = AuthApiHolder.getAccessPermissionApi();
        this.contextStack = new Stack<>();
        this.contextStack.push(context);
        this.nodes = new ArrayList<>();
        this.repeatSet = new HashSet<>();
    }

    @NotNull
    public AuthCompileContext getCurrentContext() {
        return contextStack.peek();
    }

    public List<ActionPermissionNode> getNodes() {
        return this.nodes;
    }

    @Override
    public boolean visit(UIView node) {
        AuthCompileContext currentContext = this.getCurrentContext();
        String model = node.getModel();
        if (StringUtils.isBlank(model)) {
            node.setModel(currentContext.getCurrentModel());
        }
        currentContext.setCurrentView(node);
        if (contextStack.size() == FIRST_VIEW) {
            AccessResourceInfo info = currentContext.getInfo().clone();
            AuthCompileContext context = new AuthCompileContext(info, currentContext.getAuthorizationMap());
            context.setCurrentViewAction(info.getViewAction());
            context.setCurrentView(node);
            context.setCurrentModel(node.getModel());
            context.setIsMainView(true);
            if (ViewTypeEnum.TABLE.equals(Optional.ofNullable(context.getCurrentView()).map(UIView::getType).orElse(null))) {
                context.setTableEditable(true);
            }
            contextStack.push(context);
        } else {
            if (isRecursive(node)) {
                return false;
            }
            AccessResourceInfo info = currentContext.getInfo().clone();
            AuthCompileContext context = new AuthCompileContext(info, currentContext.getAuthorizationMap());
            context.setNode(currentContext.getNode());
            context.setCurrentViewAction(currentContext.getCurrentViewAction());
            context.setCurrentView(currentContext.getCurrentView());
            context.setCurrentField(currentContext.getCurrentField());
            context.setCurrentModel(currentContext.getCurrentModel());
            contextStack.push(context);
        }
        return true;
    }

    @Override
    public void endVisit(UIView node) {
        if (contextStack.size() == FIRST_VIEW_POP) {
            AuthCompileContext currentContext = this.getCurrentContext();
            if (currentContext.getTableEditable()) {
                UIAction uiAction = new UIAction();
                uiAction.setName(FunctionConstants.update);
                uiAction.setLabel(AuthConstants.TABLE_EDITABLE_UPDATE);
                visit(uiAction);
            }
        }
        contextStack.pop();
    }

    private boolean isRecursive(UIView node) {
        String currentModel = node.getModel();
        String currentName = node.getName();
        if (StringUtils.isAnyBlank(currentModel, currentName)) {
            return false;
        }
        Enumeration<AuthCompileContext> iterator = contextStack.elements();
        while (iterator.hasMoreElements()) {
            AuthCompileContext context = iterator.nextElement();
            UIView view = context.getCurrentView();
            if (view != null && iterator.hasMoreElements() && currentModel.equals(view.getModel()) && currentName.equals(view.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean visit(UITemplate node) {
        compileTemplate(node);
        return true;
    }

    @Override
    public boolean visit(UIField node) {
        AuthCompileContext currentContext = this.getCurrentContext();
        String model = currentContext.getCurrentModel();
        String field = Optional.ofNullable(node.getData()).orElse(node.getName());
        if (StringUtils.isBlank(field)) {
            return false;
        }
        ModelField modelField = Optional.ofNullable(PamirsSession.getContext().getModelField(model, field)).map(ModelFieldConfig::getModelField).orElse(null);
        if (modelField == null) {
            return false;
        }

        compileField(node, modelField);

        String resModel = node.getReferences();
        if (StringUtils.isBlank(resModel)) {
            if (!AuthCompileHelper.isEmptyChildrenWidgets(node)) {
                AccessResourceInfo info = currentContext.getInfo().clone();
                info.addFieldPath(model, field);
                AuthCompileContext context = buildAuthContext(info, currentContext, node, model);
                contextStack.push(context);
                return true;
            }
            return false;
        }

        UIView subview = AuthCompileHelper.fetchAutoFillSubviewForRelation(node);
        if (subview != null) {
            AuthCompileHelper.addWidget(node, subview);
        }
        if (!AuthCompileHelper.isEmptyChildrenWidgets(node)) {
            AccessResourceInfo info = currentContext.getInfo().clone();
            info.addFieldPath(model, field);
            AuthCompileContext context = buildAuthContext(info, currentContext, node, resModel);
            contextStack.push(context);
            return true;
        }
        return false;
    }

    private AuthCompileContext buildAuthContext(AccessResourceInfo info, AuthCompileContext currentContext, UIField node, String model) {
        AuthCompileContext context = new AuthCompileContext(info, currentContext.getAuthorizationMap());
        context.setNode(currentContext.getNode());
        context.setCurrentViewAction(currentContext.getCurrentViewAction());
        context.setCurrentField(node);
        context.setCurrentModel(model);
        return context;
    }

    @Override
    public void endVisit(UIField node) {
        contextStack.pop();
    }

    @Override
    public boolean visit(UIAction node) {
        String actionName = Optional.ofNullable(node.getName()).filter(StringUtils::isNotBlank).orElse(node.getFun());
        if (StringUtils.isBlank(actionName)) {
            return false;
        }
        AuthCompileContext currentContext = this.getCurrentContext();
        String model = Optional.ofNullable(node.getModel()).filter(StringUtils::isNotBlank).orElse(currentContext.getCurrentModel());
        Action action = metaCacheManager.fetchAction(model, actionName);
        if (action == null) {
            action = fetchInternalClientAction(model, actionName);
            if (action == null) {
                log.debug("fetch action metadata cache error. model: {}, action: {}", model, actionName);
                return false;
            }
        }
        actionName = action.getName();

        if (actionIgnored(currentContext, action, model, actionName)) {
            return false;
        }

        compileAction(node, action);

        Long actionId = action.getId();
        if (actionId == null) {
            log.warn("action id is missing. model: {}, name: {}", model, actionName);
            QueryActions<Action> queryActions = new QueryActions<>(action.getActionType());
            queryActions.add(model, actionName);
            List<Action> actions = queryActions.query();
            if (CollectionUtils.isEmpty(actions)) {
                return false;
            } else {
                Action idAction = actions.get(0);
                actionId = idAction.getId();
                action.setId(actionId);
            }
        }

        ViewAction currentViewAction = currentContext.getCurrentViewAction();
        ActionPermissionNode actionPermission;
        if (!model.equals(currentViewAction.getModel()) || !actionName.equals(currentViewAction.getName())) {
            actionPermission = resourcePermissionNodeConverter.convertActionNode(currentContext, node, action);
            if (actionPermission.getNodes() == null) {
                actionPermission.setNodes(new ArrayList<>());
            }
            if (ObjectHelper.isRepeat(repeatSet, actionPermission.getPath())) {
                return false;
            }
            ActionPermissionNode priorNode = currentContext.getNode();
            if (priorNode != null) {
                priorNode.getNodes().add(actionPermission);
            } else {
                nodes.add(actionPermission);
            }
            if (Boolean.TRUE.equals(actionPermission.getIgnoreChildren())) {
                return false;
            }
        } else {
            return false;
        }

        String resModel = node.getResModel();
        String resModule = node.getResModule();
        boolean isRouteViewAction = ActionTypeEnum.VIEW.equals(node.getActionType()) && StringUtils.isNotBlank(resModel);
        if (!isRouteViewAction) {
            return false;
        }
        if (!AuthCompileHelper.isEmptyChildrenWidgets(node)) {
            AccessResourceInfo info = currentContext.getInfo().clone();
            info.addActionPath(model, actionName);
            AuthCompileContext context = new AuthCompileContext(info, currentContext.getAuthorizationMap());
            context.setNode(actionPermission);
            context.setCurrentViewAction((ViewAction) action);
            context.setCurrentModel(resModel);
            contextStack.push(context);
            return true;
        }
        if (!AuthCompileHelper.INNER_ACTION_TARGETS.contains(node.getTarget())) {
            boolean isMenuAction = Models.origin().count(Pops.<Menu>lambdaQuery()
                    .from(Menu.MODEL_MODEL)
                    .eq(Menu::getModule, resModule)
                    .eq(Menu::getModel, resModel)
                    .eq(Menu::getActionName, actionName)).equals(1L);
            if (isMenuAction) {
                return false;
            }
        }
        UIView subview = AuthCompileHelper.fetchAutoFillSubViewForAction(node, resModel);
        if (subview != null) {
            AuthCompileHelper.addWidget(node, subview);
        }
        if (!AuthCompileHelper.isEmptyChildrenWidgets(node)) {
            AccessResourceInfo info = currentContext.getInfo().clone();
            info.addActionPath(model, actionName);
            AuthCompileContext context = new AuthCompileContext(info, currentContext.getAuthorizationMap());
            context.setNode(actionPermission);
            context.setCurrentViewAction((ViewAction) action);
            context.setCurrentModel(resModel);
            contextStack.push(context);
            return true;
        }
        return false;
    }

    @Override
    public void endVisit(UIAction node) {
        contextStack.pop();
    }

    private void compileTemplate(UITemplate node) {
        UIView currentView = this.getCurrentContext().getCurrentView();
        if (currentView != null) {
            fillActions(currentView, node);
        }
    }

    private void fillActions(UIView currentView, UITemplate template) {
        if (null == template.getAutoFill() || !template.getAutoFill() || !CollectionUtils.isEmpty(template.getWidgets())) {
            return;
        }
        AuthCompileHelper.fetchAutoFillActionsForMainView(this.getCurrentContext().getCurrentModel(), currentView, template).forEach(template::addWidget);
    }

    private void compileField(UIField widget, ModelField modelField) {
        widget.setTtype(modelField.getTtype());
        widget.setModel(modelField.getModel());
        widget.setReferences(modelField.getReferences());
    }

    private void compileAction(UIAction node, Action action) {
        node.setLabel(Optional.ofNullable(node.getLabel()).orElse(action.getLabel()));
        node.setDisplayName(Optional.ofNullable(node.getDisplayName()).orElse(action.getDisplayName()));
        node.setActionType(action.getActionType());
        node.setModel(action.getModel());
        node.setName(action.getName());
        if (action instanceof ViewAction) {
            ViewAction viewAction = (ViewAction) action;
            node.setResModel(viewAction.getResModel());
            node.setResViewName(viewAction.getResViewName());
            node.setViewType(viewAction.getViewType());
            node.setTarget(viewAction.getTarget());
            node.setResModule(Optional.ofNullable(viewAction.getResModule()).orElse(viewAction.getModule()));
        } else if (action instanceof ClientAction) {
            ClientAction clientAction = (ClientAction) action;
            node.setFun(clientAction.getFun());
        }
    }

    private Action fetchInternalClientAction(String model, String actionName) {
        String finalActionName = null;
        switch (actionName) {
            case ClientActionConstants.GoBack.fun:
                finalActionName = ClientActionConstants.GoBack.name;
                break;
            case ClientActionConstants.X2MDelete.fun:
                finalActionName = ClientActionConstants.X2MDelete.name;
                break;
        }
        if (finalActionName == null) {
            return null;
        }
        return metaCacheManager.fetchAction(model, finalActionName);
    }

    private boolean actionIgnored(AuthCompileContext currentContext, Action action, String model, String actionName) {
        return this.accessPermissionApi.isFilterFunction(model, actionName) ||
                this.accessPermissionApi.isFilterFunctionOnlyLogin(model, actionName) ||
                (this.permissionNodeLoader.isOptimizeManagementNode() && isOptimizeFilter(currentContext, action));
    }

    private boolean isOptimizeFilter(AuthCompileContext currentContext, Action action) {
        ViewAction viewAction = currentContext.getCurrentViewAction();
        if (viewAction == null || !(action instanceof ServerAction)) {
            return false;
        }
        ServerAction serverAction = (ServerAction) action;
        String namespace = serverAction.getModel();
        String fun = serverAction.getFun();
        Function function = null;
        if (StringUtils.isNoneBlank(namespace, fun)) {
            function = PamirsSession.getContext().getFunctionAllowNull(namespace, fun);
        }
        if (function == null) {
            return false;
        }
//        if (ActionContextTypeEnum.CONTEXT_FREE.equals(viewAction.getContextType())) {
//            return Optional.of(function)
//                    .map(Function::getType)
//                    .map(FunctionTypeEnum.UPDATE::in)
//                    .orElse(false);
//        } else if (ActionContextTypeEnum.SINGLE.equals(viewAction.getContextType())) {
//            return Optional.of(function)
//                    .map(Function::getType)
//                    .map(FunctionTypeEnum.CREATE::in)
//                    .orElse(false);
//        }
        String viewActionName = viewAction.getName();
        if (ViewActionConstants.redirectCreatePage.name.equals(viewActionName)) {
            return FunctionConstants.update.equals(fun);
        } else if (ViewActionConstants.redirectUpdatePage.name.equals(viewActionName)) {
            return FunctionConstants.create.equals(fun);
        }
        return false;
    }
}
