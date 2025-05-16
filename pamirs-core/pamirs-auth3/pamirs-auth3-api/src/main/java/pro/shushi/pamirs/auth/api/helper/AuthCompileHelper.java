package pro.shushi.pamirs.auth.api.helper;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.enmu.WidgetEnum;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.ux.constants.DslNodeConstants;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.boot.base.ux.enmu.ViewSlotNameEnum;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.base.ux.model.UIWidget;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.boot.base.ux.model.view.UIField;
import pro.shushi.pamirs.boot.base.ux.model.view.UITemplate;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.boot.web.manager.UiIoManager;
import pro.shushi.pamirs.boot.web.utils.ClientActionUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限编译帮助类
 *
 * @author Adamancy Zhang at 14:07 on 2024-01-16
 */
@Slf4j
public class AuthCompileHelper {

    private static final List<WidgetEnum> FIELD_FILL_VIEW_WIDGETS = Arrays.asList(WidgetEnum.TABLE, WidgetEnum.FORM, WidgetEnum.DETAIL);

    public static final List<ActionTargetEnum> INNER_ACTION_TARGETS = Arrays.asList(ActionTargetEnum.DIALOG, ActionTargetEnum.DRAWER, ActionTargetEnum.INNER);

    // FIXME: zbh 20240628 统一定义常量
    private static final String DEFAULT_SLOT = "default";

    private AuthCompileHelper() {
        // reject create object
    }

    public static List<UIAction> fetchAutoFillActionsForMainView(String model, UIView view, UITemplate template) {
        List<Action> actionList = getMetaCacheManager().fetchActions(model);
        if (CollectionUtils.isEmpty(actionList)) {
            return new ArrayList<>();
        }

        ViewTypeEnum viewType = view.getType();
        // 处理动作
        List<UIAction> uiActionList = new ArrayList<>();
        UIAction uiAction;

        // 处理返回动作
        if (ViewTypeEnum.DETAIL.equals(viewType) || ViewTypeEnum.FORM.equals(viewType)) {
            uiActionList.add(ClientActionUtils.makeGoBackAction());
        }

        if (!ViewTypeEnum.DETAIL.equals(viewType)) {
            for (Action action : actionList) {
                if (null != action.getSys() && !action.getSys()) {
                    continue;
                }
                if (StringUtils.isNotBlank(action.getBindingViewName()) && !action.getBindingViewName().equals(view.getName())) {
                    continue;
                }
                if (null == action.getBindingType() || !action.getBindingType().contains(viewType)) {
                    continue;
                }
                uiAction = new UIAction();
                uiAction.setModel(action.getModel());
                uiAction.setName(action.getName());
                uiAction.setLabel(action.getLabel());
                uiAction.setActionType(action.getActionType());
                uiAction.setContextType(action.getContextType());
                uiAction.setContext(action.getContext());
                uiAction.setMapping(action.getMapping());
                uiAction.setInvisible(action.getInvisible());
                uiAction.setDisabled(action.getDisable());
                uiAction.setPriority(action.getPriority());

                if (ViewTypeEnum.TABLE.equals(viewType)) {
                    if (ActionContextTypeEnum.SINGLE.equals(action.getContextType())) {
                        if (ViewSlotNameEnum.ROW_ACTIONS.value().equals(template.getSlot())) {
                            uiActionList.add(uiAction);
                        }
                    } else {
                        if (ViewSlotNameEnum.ACTIONS.value().equals(template.getSlot())) {
                            uiActionList.add(uiAction);
                        }
                    }
                } else {
                    if (ActionContextTypeEnum.SINGLE.equals(action.getContextType()) || ActionContextTypeEnum.SINGLE_AND_BATCH.equals(action.getContextType())) {
                        if (ViewSlotNameEnum.ACTIONS.value().equals(template.getSlot())) {
                            uiActionList.add(uiAction);
                        }
                    }
                }
            }
        }
        return sortActions(uiActionList);
    }

    private static List<UIAction> sortActions(List<UIAction> actionList) {
        return actionList.stream().sorted(Comparator.comparing(UIAction::getPriority)).collect(Collectors.toList());
    }

    public static UIView fetchAutoFillSubviewForRelation(UIField uiField) {
        if (CollectionUtils.isNotEmpty(uiField.getWidgets())) {
            return null;
        }
        String model = uiField.getReferences();
        if (StringUtils.isBlank(model)) {
            return null;
        }
        String widget = uiField.getWidget();
        if (StringUtils.isBlank(widget)) {
            return null;
        }

        WidgetEnum widgetType = null;
        for (WidgetEnum target : FIELD_FILL_VIEW_WIDGETS) {
            if (widget.equals(target.value())) {
                widgetType = target;
                break;
            }
        }
        if (widgetType == null) {
            return null;
        }

        ViewTypeEnum viewType = null;
        switch (widgetType) {
            case TABLE:
                viewType = ViewTypeEnum.TABLE;
                break;
            case FORM:
                viewType = ViewTypeEnum.FORM;
                break;
            case DETAIL:
                viewType = ViewTypeEnum.DETAIL;
                break;
        }
        String resViewName = uiField.getResViewName();

        if (viewType == null && StringUtils.isBlank(resViewName)) {
            return null;
        }

        String template = getMetaCacheManager().fetchViewTemplate(model, resViewName, viewType);
        if (StringUtils.isBlank(template)) {
            return null;
        }
        try {
            UIView subView = getUiIOManager().parseTemplate(template);
            subView.setModel(model);
            subView.setName(resViewName);
            return subView;
        } catch (Exception e) {
            log.error("解析视图异常 视图名称:{} ，模型编码 {} ", resViewName, model, e);
        }
        return null;
    }

    public static UIView fetchAutoFillSubViewForAction(UIAction uiAction, String resModel) {
        if (!isEmptyChildrenWidgets(uiAction)) {
            return null;
        }
        View view = getMetaCacheManager().fetchView(resModel, uiAction.getResViewName(), uiAction.getViewType(), Boolean.FALSE);
        if (view == null) {
            return null;
        }
        String template = view.getTemplate();
        if (StringUtils.isEmpty(template)) {
            log.warn("subViewForAction-template is null. 【{},{},{}】", uiAction.getModel(), uiAction.getName(), uiAction.getDisplayName());
            return null;
        }

        try {
            UIView subView = getUiIOManager().parseTemplate(template);
            subView.setModel(resModel);
            subView.setName(view.getName());
            return subView;
        } catch (Exception e) {
            log.error("解析视图异常 视图名称:{} ，模型编码 {} ", view.getName(), resModel, e);
        }
        return null;
    }

    public static boolean isEmptyChildrenWidgets(UIWidget node) {
        List<UIWidget> childrenWidgets = node.getWidgets();
        if (CollectionUtils.isEmpty(childrenWidgets)) {
            return true;
        }
        if (node instanceof UIAction) {
            UIAction actionNode = (UIAction) node;
            ActionTargetEnum target = actionNode.getTarget();
            if (target != null && INNER_ACTION_TARGETS.contains(target)) {
                return findPopupDefaultTemplate((UIAction) node) == null;
            }
        }
        if (childrenWidgets.size() == 1) {
            UIWidget firstNode = childrenWidgets.get(0);
            return TemplateNodeConstants.NODE_TEMPLATE.equals(firstNode.getDslNodeType()) && CollectionUtils.isEmpty(firstNode.getWidgets());
        }
        return false;
    }

    private static UIWidget findPopupDefaultTemplate(UIAction node) {
        UIWidget firstNode = Optional.ofNullable(node.getWidgets())
                .filter(v -> v.size() == 1)
                .map(v -> v.get(0))
                .orElse(null);
        if (firstNode == null) {
            return null;
        }
        if (DslNodeConstants.NODE_TEMPLATE.equals(firstNode.getDslNodeType()) && DEFAULT_SLOT.equals(firstNode.getSlot())) {
            List<UIWidget> widgets = firstNode.getWidgets();
            if (CollectionUtils.isEmpty(widgets)) {
                return null;
            }
            int viewWidgetIndex = -1;
            for (int i = 0; i < widgets.size(); i++) {
                UIWidget widget = widgets.get(i);
                if (DslNodeConstants.NODE_TEMPLATE.equals(widget.getDslNodeType()) && DEFAULT_SLOT.equals(widget.getSlot())) {
                    return widget;
                }
                if (DslNodeConstants.NODE_VIEW.equals(widget.getDslNodeType())) {
                    viewWidgetIndex = i;
                }
            }
            if (viewWidgetIndex != -1) {
                UIWidget viewWidget = widgets.remove(viewWidgetIndex);
                UITemplate defaultTemplate = new UITemplate();
                defaultTemplate.setDslNodeType(TemplateNodeConstants.NODE_TEMPLATE);
                defaultTemplate.setSlot(DEFAULT_SLOT);
                widgets.add(defaultTemplate);
                defaultTemplate.addWidget(viewWidget);
                return defaultTemplate;
            }
            return null;
        } else if (DslNodeConstants.NODE_VIEW.equals(firstNode.getDslNodeType())) {
            UITemplate defaultTemplate = new UITemplate();
            defaultTemplate.setDslNodeType(TemplateNodeConstants.NODE_TEMPLATE);
            defaultTemplate.setSlot(DEFAULT_SLOT);
            defaultTemplate.addWidget(firstNode);
            node.setWidgets(Lists.newArrayList(defaultTemplate));
            return defaultTemplate;
        }
        return null;
    }

    public static void addWidget(UIWidget node, UIWidget target) {
        List<UIWidget> childrenWidgets = node.getWidgets();
        if (CollectionUtils.isEmpty(childrenWidgets)) {
            node.addWidget(target);
        } else if (childrenWidgets.size() == 1) {
            UIWidget firstNode = childrenWidgets.get(0);
            if (node instanceof UIAction) {
                UIAction actionNode = (UIAction) node;
                ActionTargetEnum actionTarget = actionNode.getTarget();
                if (actionTarget != null && INNER_ACTION_TARGETS.contains(actionTarget)) {
                    List<UIWidget> widgets = firstNode.getWidgets();
                    UIWidget defaultTemplate = null;
                    if (CollectionUtils.isNotEmpty(widgets)) {
                        for (UIWidget widget : widgets) {
                            if (TemplateNodeConstants.NODE_TEMPLATE.equals(widget.getDslNodeType()) && DEFAULT_SLOT.equals(widget.getSlot())) {
                                defaultTemplate = widget;
                                break;
                            }
                        }
                    }
                    if (defaultTemplate == null) {
                        defaultTemplate = new UITemplate();
                        defaultTemplate.setDslNodeType(TemplateNodeConstants.NODE_TEMPLATE);
                        defaultTemplate.setSlot(DEFAULT_SLOT);
                        firstNode.addWidget(defaultTemplate);
                    }
                    defaultTemplate.addWidget(target);
                    return;
                }
            }
            if (TemplateNodeConstants.NODE_TEMPLATE.equals(firstNode.getDslNodeType()) && CollectionUtils.isEmpty(firstNode.getWidgets())) {
                firstNode.addWidget(target);
            }
        }
    }

    private static MetaCacheManager getMetaCacheManager() {
        return BeanDefinitionUtils.getBean(MetaCacheManager.class);
    }

    private static UiIoManager getUiIOManager() {
        return BeanDefinitionUtils.getBean(UiIoManager.class);
    }
}
