package pro.shushi.pamirs.core.common.dsl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.constants.ClientActionConstants;
import pro.shushi.pamirs.boot.base.constants.ViewConstants;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.ux.enmu.ViewSlotNameEnum;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.base.ux.model.UIWidget;
import pro.shushi.pamirs.boot.base.ux.model.part.UIOption;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.boot.base.ux.model.view.UIField;
import pro.shushi.pamirs.boot.base.ux.model.view.UIPack;
import pro.shushi.pamirs.boot.base.ux.model.view.UITemplate;
import pro.shushi.pamirs.boot.web.utils.ActionUtils;
import pro.shushi.pamirs.boot.web.utils.ClientActionUtils;
import pro.shushi.pamirs.boot.web.utils.ViewXmlUtils;
import pro.shushi.pamirs.core.common.dsl.model.UdView;
import pro.shushi.pamirs.core.common.dsl.model.UdWidget;
import pro.shushi.pamirs.core.common.dsl.model.part.UdOption;
import pro.shushi.pamirs.core.common.dsl.model.view.*;
import pro.shushi.pamirs.framework.orm.xml.PamirsXmlUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.model.Prop;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.meta.util.ClassUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DSL v2 转换器
 * 2022/5/2 6:18 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
public class DslConverter {

    public final static String X_STREAM_TYPE = "view-xml-v2";

    static {
        Collection<Class<?>> annotationClassSet = ClassUtils.getClasses(UdView.class.getPackage().getName());
        if (!CollectionUtils.isEmpty(annotationClassSet)) {
            Class<?>[] annotationClasses = annotationClassSet.toArray(new Class[0]);
            PamirsXmlUtils.register(X_STREAM_TYPE, annotationClasses);
        }
    }

    public static Object fromXML(String xml) {
        return PamirsXmlUtils.getInstance(X_STREAM_TYPE).fromXML(xml);
    }

    public static String convert(Meta meta, String model, String content) {
        if (content.startsWith("<host") || content.startsWith("<mask")) {
            return content;
        }
        UdView view = (UdView) PamirsXmlUtils.getInstance(X_STREAM_TYPE).fromXML(content);
        UIView uiView = new UIView();
        uiView.setWidget(view.getWidget());
        uiView.setType(view.getType());
        List<UdWidget> udWidgets = view.getWidgets();
        Deque<String> viewStack = new ArrayDeque<>();
        viewStack.push(Optional.ofNullable(model).orElse(view.getModel()));
        boolean autoFillActions = Optional.ofNullable(view.getAutoFillActions()).orElse(true);

        // 构造模型动作映射表
        Map<String, List<Action>> actionMap = ActionUtils.makeActionMap(meta.getCurrentModuleData());

        parseWidget(meta, actionMap, viewStack, uiView, udWidgets, autoFillActions);
        return ViewXmlUtils.toXML(uiView);
    }

    private static void parseWidget(Meta meta, Map<String, List<Action>> actionMap, Deque<String> viewStack,
                                    UIWidget uiWidget, List<UdWidget> widgets, boolean autoFillActions) {
        if (null == uiWidget) {
            return;
        }
        boolean isView = uiWidget instanceof UIView;
        boolean isPack = uiWidget instanceof UIPack;
        String viewType = null;
        ViewTypeEnum viewTypeEnum = null;
        if (isView) {
            viewType = uiWidget.getWidget();
            if ("dialog-form".equals(viewType)) {
                viewType = "form";
                viewTypeEnum = ViewTypeEnum.valueOf(viewType.toUpperCase());
            } else if ("form".equals(viewType)) {
                viewType = "form";
                viewTypeEnum = ViewTypeEnum.valueOf(viewType.toUpperCase());
            } else if ("table".equals(viewType)) {
                viewType = "table";
                viewTypeEnum = ViewTypeEnum.valueOf(viewType.toUpperCase());
            } else if ("detail".equals(viewType)) {
                viewType = "detail";
                viewTypeEnum = ViewTypeEnum.valueOf(viewType.toUpperCase());
            }
        }
        if (CollectionUtils.isNotEmpty(widgets)) {
            // 处理动作
            List<UIWidget> fieldList = new ArrayList<>();
            List<UIWidget> searchFieldList = new ArrayList<>();
            List<UIWidget> subActionList = new ArrayList<>();
            Set<String> uniqueActionSet = new HashSet<>();
            List<UIAction> globalActionList = new ArrayList<>();
            List<UIAction> rowActionList = new ArrayList<>();
            if (isView && 1 == viewStack.size()) {
                // 处理返回动作
                String upperCaseViewType = Optional.ofNullable(viewType).map(String::toUpperCase).orElse(null);
                if (autoFillActions
                        && (ViewTypeEnum.DETAIL.value().toUpperCase().equals(upperCaseViewType)
                        || ViewTypeEnum.FORM.value().toUpperCase().equals(upperCaseViewType))) {
                    globalActionList.add((UIAction) ClientActionUtils.makeGoBackAction().setPriority(0));
                }
            }
            int i = 0;
            for (UdWidget udWidget : widgets) {
                if (udWidget instanceof UdPlaceholder) {
                    UdPlaceholder udPlaceholder = (UdPlaceholder) udWidget;
                    List<UdWidget> widgetsList = udPlaceholder.getWidgets();
                    udWidget = Optional.ofNullable(widgetsList)
                            .filter(v -> !v.isEmpty()).map(v -> v.get(0)).orElse(null);
                }

                UIWidget subUiWidget = null;
                boolean isSubView = udWidget instanceof UdView;
                boolean isStoreRelationField = false;

                if (udWidget instanceof UdAction) {
                    UdAction udAction = (UdAction) udWidget;
                    UIAction uiAction = new UIAction();
                    subUiWidget = uiAction;
                    uiAction.get_d().putAll(udAction.get_d());
                    uiAction.setRefs(null);
                    uiAction.setWidgets(null);
                    uiAction.setModel(Optional.ofNullable(uiAction.getModel()).orElse(viewStack.peek()));
                    uiAction.setName(udAction.getRefs());
                    uiAction.setLabel(Optional.ofNullable(udAction.getText()).orElse(udAction.getDisplayName()));
                    uiAction.setActionType(udAction.getActionType());

                    if ("$$internal_GotoM2MListDialog".equals(udAction.getRefs()) || "$$internal_GotoM2MListDialog".equals(udAction.getFun())) {
                        uiAction.setLabel(Optional.ofNullable(uiAction.getLabel()).orElse("添加"));
                        uiAction.setActionType(Optional.ofNullable(uiAction.getActionType()).orElse(ActionTypeEnum.VIEW));
                        uiAction.setViewType(Optional.ofNullable(uiAction.getViewType()).orElse(ViewTypeEnum.TABLE));
                        uiAction.setResViewName(Optional.ofNullable(udAction.getResViewName()).orElse(ViewConstants.Name.dialogTableView));
                    } else if ("$$internal_DeleteOne".equals(udAction.getRefs())) {
                        uiAction.setLabel(Optional.ofNullable(uiAction.getLabel()).orElse("删除"));
                        uiAction.setActionType(Optional.ofNullable(uiAction.getActionType()).orElse(ActionTypeEnum.CLIENT));
                        uiAction.setFun(udAction.getRefs());
                        uiAction.setContextType(ActionContextTypeEnum.SINGLE_AND_BATCH);
                    }

                    uiAction.setPriority(i + 1);

                    if (isView) {
                        // 获取tag
                        String tag = udAction.getTag();
                        if (StringUtils.isBlank(tag)) {
                            autoFillActions(meta, viewType, globalActionList, rowActionList, uniqueActionSet, uiAction);
                        } else if ("contextFreeAction".equals(tag)) {
                            globalActionList.add(uiAction);
                            uniqueActionSet.add(uiAction.getName());
                        } else {
                            rowActionList.add(uiAction);
                            uniqueActionSet.add(uiAction.getName());
                        }
                    } else {
                        subActionList.add(uiAction);
                        uniqueActionSet.add(uiAction.getName());
                    }
                } else if (udWidget instanceof UdField) {
                    UdField udField = (UdField) udWidget;
                    UIField uiField = new UIField();
                    uiField.get_d().putAll(udField.get_d());
                    uiField.setWidgets(null);
                    uiField.setOptions(convertOptions(udField.getOptions()));
                    subUiWidget = uiField;
                    uiField.setModel(Optional.ofNullable(udField.getModel()).orElse(viewStack.peek()));
                    uiField.setData(udField.getName());
                    uiField.setLabel(udField.getLabel());
                    uiField.setPriority(i);
                    fieldList.add(uiField);

                    ModelField modelField = meta.getModelField(uiField.getModel(), uiField.getData());
                    if (null != modelField) {
                        isStoreRelationField = TtypeEnum.isRelationType(modelField.getTtype())
                                && StringUtils.isNotBlank(modelField.getReferences());
                        if (isStoreRelationField) {
                            viewStack.push(modelField.getReferences());
                        }
                    } else {
                        log.warn("Field does not exist, model:{}, field:{}", uiField.getModel(), uiField.getData());
                    }
                } else if (udWidget instanceof UdFilter) {
                    UdFilter udField = (UdFilter) udWidget;
                    UIField uiField = new UIField();
                    uiField.get_d().putAll(udField.get_d());
                    uiField.setWidgets(null);
                    uiField.setOptions(convertOptions(udField.getOptions()));
                    subUiWidget = uiField;
                    uiField.setModel(Optional.ofNullable(udField.getModel()).orElse(viewStack.peek()));
                    uiField.setData(udField.getName());
                    uiField.setLabel(udField.getLabel());
                    uiField.setPriority(i);
                    searchFieldList.add(uiField);
                } else if (udWidget instanceof UdGroup) {
                    UdGroup udGroup = (UdGroup) udWidget;
                    UIPack uiPack = new UIPack();
                    uiPack.get_d().putAll(udGroup.get_d());
                    uiPack.setWidgets(null);
                    subUiWidget = uiPack;
                    uiPack.setTitle(udGroup.getTitle());
                    String widget = udGroup.getWidget();
                    if ("fieldset".equals(widget)) {
                        widget = "group";
                    }
                    uiPack.setWidget(widget);
                    fieldList.add(uiPack);
                } else if (udWidget instanceof UdConfig) {
                    for (String key : udWidget.get_d().keySet()) {
                        uiWidget.addProp(new Prop().setName(key).setValue(udWidget.get_d().get(key)));
                    }
                    if (isView && udWidget.get_d().containsKey("excluded-actions")) {
                        String excludeActionsString = (String) uiWidget.getPropValue("excluded-actions");
                        if (StringUtils.isNotBlank(excludeActionsString)) {
                            for (String token : excludeActionsString.split(CharacterConstants.SEPARATOR_COMMA)) {
                                uniqueActionSet.add(token.trim());
                            }
                        }
                    }
                } else if (isSubView) {
                    UdView udView = (UdView) udWidget;
                    UIView uiView = new UIView();
                    uiView.get_d().putAll(udView.get_d());
                    uiView.setWidgets(null);
                    subUiWidget = uiView;

                    String model = udView.getModel();
                    if (null == model) {
                        model = viewStack.peek();
                    }
                    uiView.setModel(model);
                    uiView.setWidget(udView.getWidget());
                    uiView.setType((udView).getType());
                    uiWidget.addWidget(uiView);
                    viewStack.push(model);

                    autoFillActions = Optional.ofNullable(udView.getAutoFillActions()).orElse(true);
                }

                if (null == subUiWidget) {
                    continue;
                }
                List<UdWidget> childWidgets = udWidget.getWidgets();
                parseWidget(meta, actionMap, viewStack, subUiWidget, childWidgets, autoFillActions);

                if (isSubView || isStoreRelationField) {
                    viewStack.pop();
                }
                i++;
            }
            if (isView) {
                // 补充动作
                if (autoFillActions && 1 == viewStack.size() && CollectionUtils.isEmpty(uniqueActionSet)) {
                    // 补充主视图动作
                    makeUiActions((UIView) uiWidget, viewTypeEnum);
                } else {
                    // 将动作分发到槽中
                    uiWidget.addWidget(makeActionsSlot(ViewSlotNameEnum.ACTIONS.value(), globalActionList));
                    uiWidget.addWidget(makeActionsSlot(ViewSlotNameEnum.ROW_ACTIONS.value(), rowActionList));
                }

                // 处理字段
                uiWidget.addWidget(makeFieldsSlot(ViewSlotNameEnum.FIELDS, fieldList));

                // 处理搜索字段
                uiWidget.addWidget(makeFieldsSlot(ViewSlotNameEnum.SEARCH, searchFieldList));
            } else if (isPack) {
                // 处理字段
                uiWidget.setWidgets(fieldList);
            } else if (uiWidget instanceof UIAction) {
                uiWidget.setWidgets(subActionList);
            }
        }
    }

    private static void autoFillActions(Meta meta, String viewType,
                                        List<UIAction> globalActionList,
                                        List<UIAction> rowActionList,
                                        Set<String> uniqueActionSet,
                                        UIAction uiAction) {
        // 过滤动作
        if (null != uniqueActionSet) {
            if (uniqueActionSet.contains(uiAction.getName())) {
                return;
            }
            uniqueActionSet.add(uiAction.getName());
        }

        if (ClientActionConstants.GoBack.name.equals(uiAction.getName())) {
            if (ClientActionConstants.GoBack.name.equals(Optional.of(globalActionList)
                    .filter(v -> CollectionUtils.isNotEmpty(globalActionList))
                    .map(v -> v.get(0)).map(UIWidget::getName).orElse(null))) {
                globalActionList.set(0, uiAction);
            } else {
                globalActionList.add(uiAction);
            }
        }

        // 分发动作
        Action action = meta.getDataItem(Action.MODEL_MODEL, Action.sign(uiAction.getModel(), uiAction.getName()));
        ActionContextTypeEnum contextType = null;
        if (null != action) {
            contextType = action.getContextType();
        }
        if ("table".equals(viewType)) {
            if (ActionContextTypeEnum.SINGLE.equals(contextType)) {
                rowActionList.add(uiAction);
            } else {
                globalActionList.add(uiAction);
            }
        } else {
            if (null == contextType || ActionContextTypeEnum.SINGLE.equals(contextType) || ActionContextTypeEnum.SINGLE_AND_BATCH.equals(contextType)) {
                globalActionList.add(uiAction);
            }
        }
    }

    private static UITemplate makeFieldsSlot(ViewSlotNameEnum slotName, List<UIWidget> widgets, Prop... props) {
        if (CollectionUtils.isNotEmpty(widgets)) {
            UITemplate fields = new UITemplate();
            fields.setSlot(slotName.value());
            fields.setWidgets(widgets);
            if (ArrayUtils.isNotEmpty(props)) {
                for (Prop prop : props) {
                    fields.addProp(prop);
                }
            }
            return fields;
        }
        return null;
    }

    private static void makeUiActions(UIView uiView, ViewTypeEnum viewType) {
        uiView.addWidget(makeDefaultActionsSlot(ViewSlotNameEnum.ACTIONS.value()));
        if (ViewTypeEnum.TABLE.equals(viewType)) {
            uiView.addWidget(makeDefaultActionsSlot(ViewSlotNameEnum.ROW_ACTIONS.value()));
        }
    }

    private static UITemplate makeDefaultActionsSlot(String actionSlotName) {
        UITemplate globalActions = new UITemplate();
        globalActions.setSlot(actionSlotName);
        globalActions.setAutoFill(true);
        return globalActions;
    }

    private static UITemplate makeActionsSlot(String actionSlotName, List<UIAction> actionList) {
        if (CollectionUtils.isNotEmpty(actionList)) {
            UITemplate globalActions = new UITemplate();
            globalActions.setSlot(actionSlotName);
            globalActions.setWidgets(actionList.stream()
                    .sorted(Comparator.comparing(UIAction::getPriority)).collect(Collectors.toList()));
            return globalActions;
        }
        return null;
    }

    private static List<UIOption> convertOptions(List<UdOption> options) {
        if (CollectionUtils.isEmpty(options)) {
            return null;
        }
        List<UIOption> uiOptions = new ArrayList<>(options.size());
        UIOption uiOption;
        for (UdOption udOption : options) {
            uiOption = new UIOption();
            uiOption.set_d(udOption.get_d());
            uiOptions.add(uiOption);
        }
        return uiOptions;
    }

}
