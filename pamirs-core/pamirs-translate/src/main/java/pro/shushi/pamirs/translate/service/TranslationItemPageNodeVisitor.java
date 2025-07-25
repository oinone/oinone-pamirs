package pro.shushi.pamirs.translate.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.boot.base.constants.ClientActionConstants;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.ClientAction;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.base.ux.model.part.UIOption;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.boot.base.ux.model.view.UIField;
import pro.shushi.pamirs.boot.base.ux.model.view.UIPack;
import pro.shushi.pamirs.boot.base.ux.model.view.UITemplate;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.translate.visitor.AuthCompileHelper;
import pro.shushi.pamirs.translate.visitor.DslNodeVisitor;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * 翻译解析XML
 *
 * @author Adamancy Zhang at 13:42 on 2024-01-16
 */
@Slf4j
public class TranslationItemPageNodeVisitor implements DslNodeVisitor {

    private static final List<ActionTargetEnum> INNER_ACTION_TARGETS = Arrays.asList(ActionTargetEnum.DIALOG, ActionTargetEnum.DRAWER, ActionTargetEnum.INNER);

    private final MetaCacheManager metaCacheManager;

    private final Stack<TranslateCompileContext> contextStack;

    private static final int FIRST_VIEW = 1;

    private static final int FIRST_VIEW_POP = 2;
    // module,origin
    public final Set<String> context;


    public TranslationItemPageNodeVisitor(Set<String> map) {
        this.context = map;
        this.metaCacheManager = BeanDefinitionUtils.getBean(MetaCacheManager.class);
        this.contextStack = new Stack<>();
    }

    public void setCurrentContext(TranslateCompileContext context) {
        this.contextStack.push(context);
    }

    @NotNull
    public TranslateCompileContext getCurrentContext() {
        return contextStack.peek();
    }


    @Override
    public boolean visit(UIView node) {
        TranslateCompileContext currentContext = this.getCurrentContext();
        String title = node.getTitle();
        if (StringUtils.isNotBlank(title)) {
            context.add(title);
        }
        String model = node.getModel();
        if (StringUtils.isBlank(model)) {
            node.setModel(currentContext.getCurrentModel());
        }
        currentContext.setCurrentView(node);
        if (contextStack.size() == FIRST_VIEW) {
            TranslateCompileContext context = new TranslateCompileContext();
            context.setCurrentView(node);
            context.setCurrentModel(node.getModel());
            context.setCurrentModule(currentContext.getCurrentModule());
            context.setIsMainView(true);
            if (ViewTypeEnum.TABLE.equals(Optional.ofNullable(context.getCurrentView()).map(UIView::getType).orElse(null))) {
                context.setTableEditable(true);
            }
            contextStack.push(context);
        } else {
            if (isRecursive(node)) {
                return false;
            }
            currentContext.setCurrentModel(node.getModel());
        }
        return true;
    }

    @Override
    public void endVisit(UIView node) {
        if (contextStack.size() == FIRST_VIEW_POP) {
            TranslateCompileContext currentContext = this.getCurrentContext();
            if (currentContext.getTableEditable()) {
                UIAction uiAction = new UIAction();
                uiAction.setName(FunctionConstants.update);
                uiAction.setLabel(AuthConstants.TABLE_EDITABLE_UPDATE);
                visit(uiAction);
            }
            contextStack.pop();
        }
    }

    private boolean isRecursive(UIView node) {
        String currentModel = node.getModel();
        String currentName = node.getName();
        if (StringUtils.isAnyBlank(currentModel, currentName)) {
            return false;
        }
        Enumeration<TranslateCompileContext> iterator = contextStack.elements();
        while (iterator.hasMoreElements()) {
            TranslateCompileContext context = iterator.nextElement();
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
        TranslateCompileContext currentContext = this.getCurrentContext();
        String model = currentContext.getCurrentModel();

        String placeholder = node.getPlaceholder();
        if (StringUtils.isNotBlank(placeholder)) {
            context.add(placeholder);
        }
        String hint = node.getHint();
        if (StringUtils.isNotBlank(hint)) {
            context.add(hint);
        }
        String label = node.getLabel();
        if (StringUtils.isNotBlank(label)) {
            context.add(label);
        }

        //获取所有的options
        List<UIOption> options = node.getOptions();
        if (CollectionUtils.isNotEmpty(options)) {
            for (UIOption option : options) {
                String displayName = option.getDisplayName();
                if (StringUtils.isNotBlank(displayName)) {
                    context.add(displayName);
                }
            }
        }

        String field = Optional.ofNullable(node.getData()).orElse(node.getName());
        if (StringUtils.isBlank(field)) {
            return false;
        }
        ModelField modelField = Optional.ofNullable(PamirsSession.getContext().getModelField(model, field)).map(ModelFieldConfig::getModelField).orElse(null);
        if (modelField == null) {
            return false;
        }
        String displayName = modelField.getDisplayName();
        if (StringUtils.isBlank(label) && StringUtils.isNotBlank(displayName)) {
            context.add(displayName);
        }

        compileField(node, modelField);

        String resModel = node.getReferences();
        if (StringUtils.isBlank(resModel)) {
            return false;
        }

        UIView subview = AuthCompileHelper.fetchAutoFillSubviewForRelation(node);
        if (subview != null) {
            AuthCompileHelper.addWidget(node, subview);
        }
        if (!AuthCompileHelper.isEmptyChildrenWidgets(node)) {
            TranslateCompileContext context = new TranslateCompileContext();
            context.setCurrentViewAction(currentContext.getCurrentViewAction());
            context.setCurrentField(node);
            context.setCurrentModel(resModel);
            context.setCurrentModule(currentContext.getCurrentModule());
            contextStack.push(context);
            return true;
        }
        return false;
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
        TranslateCompileContext currentContext = this.getCurrentContext();
        String label = node.getLabel();
        if (StringUtils.isNotBlank(label)) {
            context.add(label);
        }

        String model = Optional.ofNullable(node.getModel()).filter(StringUtils::isNotBlank).orElse(currentContext.getCurrentModel());
        Action action = metaCacheManager.fetchAction(model, actionName);
        if (action == null) {
            action = fetchInternalClientAction(model, actionName);
            if (action == null) {
                return false;
            }
        }

        String labelOrDisplayName = Optional.ofNullable(node.getDisplayName()).orElse(Optional.ofNullable(action.getLabel()).orElse(action.getDisplayName()));
        if (StringUtils.isBlank(label) && StringUtils.isNotBlank(labelOrDisplayName)) {
            context.add(labelOrDisplayName);
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

    @Override
    public boolean visit(UIPack node) {
        TranslateCompileContext currentContext = this.getCurrentContext();
        String title = node.getTitle();
        if (StringUtils.isNotBlank(title)) {
            context.add(title);
        }
        return true;
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

}
