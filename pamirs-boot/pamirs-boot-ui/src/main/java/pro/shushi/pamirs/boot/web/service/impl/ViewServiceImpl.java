package pro.shushi.pamirs.boot.web.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.constants.ClientActionConstants;
import pro.shushi.pamirs.boot.base.constants.ViewConstants;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.enmu.WidgetEnum;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.boot.base.ux.constants.ActionPropConstants;
import pro.shushi.pamirs.boot.base.ux.constants.DslNodeConstants;
import pro.shushi.pamirs.boot.base.ux.constants.FieldPropConstants;
import pro.shushi.pamirs.boot.base.ux.enmu.ViewSlotNameEnum;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.base.ux.model.UIWidget;
import pro.shushi.pamirs.boot.base.ux.model.auth.UIAuth;
import pro.shushi.pamirs.boot.base.ux.model.metadata.UIMetadata;
import pro.shushi.pamirs.boot.base.ux.model.metadata.UIModel;
import pro.shushi.pamirs.boot.base.ux.model.view.*;
import pro.shushi.pamirs.boot.web.cache.LayoutDefinitionCache;
import pro.shushi.pamirs.boot.web.compile.ViewCompileContext;
import pro.shushi.pamirs.boot.web.constants.UIConstants;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.boot.web.manager.UiIoManager;
import pro.shushi.pamirs.boot.web.service.ViewService;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.boot.web.spi.api.UserPreferenceService;
import pro.shushi.pamirs.boot.web.utils.ClientActionUtils;
import pro.shushi.pamirs.boot.web.utils.UiViewUtils;
import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.orm.json.PamirsJsonUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.model.Prop;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 视图处理服务
 * <p>
 * 2022/2/23 9:55 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Service
public class ViewServiceImpl implements ViewService {

    @Resource
    private MetaCacheManager metaCacheManager;

    @Resource
    private UiIoManager uiioManager;

    @Override
    public List<View> load(List<View> viewList) {
        if (CollectionUtils.isEmpty(viewList)) {
            return viewList;
        }
        Map<String, MutablePair<Integer, View>> needQueryViewMap = new HashMap<>(viewList.size());
        for (int i = 0; i < viewList.size(); i++) {
            View view = viewList.get(i);
            String model = view.getModel();
            if (StringUtils.isBlank(model)) {
                continue;
            }
            if (StringUtils.isNotBlank(view.getName())) {
                needQueryViewMap.put(view.getSign(), new MutablePair<>(i, view));
            } else {
                String template = view.getTemplate();
                if (StringUtils.isNotBlank(template)) {
                    view.setTemplate(compile(model, null, template));
                }
            }
        }
        if (!needQueryViewMap.isEmpty()) {
            LambdaQueryWrapper<View> wrapper = Pops.<View>lambdaQuery().from(View.MODEL_MODEL);
            List<String> models = new ArrayList<>(needQueryViewMap.size());
            List<String> fields = new ArrayList<>(needQueryViewMap.size());
            needQueryViewMap.values().stream().map(MutablePair::getRight).forEach(v -> {
                models.add(v.getModel());
                fields.add(v.getName());
            });
            wrapper.in(Arrays.asList(View::getModel, View::getName), models, fields);
            List<View> dbViewList = Models.origin().queryListByWrapper(wrapper);
            if (CollectionUtils.isNotEmpty(dbViewList)) {
                dbViewList = Models.origin().listFieldQuery(dbViewList, View::getBaseLayoutDefinition);
                for (View view : dbViewList) {
                    String model = view.getModel();
                    compileView(model, view);
                    LayoutDefinition layoutDefinition = view.getBaseLayoutDefinition();
                    if (layoutDefinition != null) {
                        compileLayout(model, layoutDefinition);
                    }
                    MutablePair<Integer, View> entry = needQueryViewMap.get(view.getSign());
                    if (entry != null) {
                        viewList.set(entry.getLeft(), view);
                    }
                }
            }
        }
        return viewList;
    }

    @Override
    public View load(View view) {
        if (null == view) {
            return null;
        }
        String model = view.getModel();
        if (StringUtils.isBlank(model)) {
            return view;
        }
        String name = view.getName();
        if (StringUtils.isBlank(name)) {
            String template = view.getTemplate();
            if (StringUtils.isNotBlank(template)) {
                view.setTemplate(compile(model, null, template));
            }
        } else {
            LambdaQueryWrapper<View> wrapper = Pops.<View>lambdaQuery().from(View.MODEL_MODEL)
                    .eq(View::getModel, model).eq(View::getName, name);
            View dbView = view.queryOneByWrapper(wrapper);
            if (null != dbView) {
                dbView = dbView.fieldQuery(View::getBaseLayoutDefinition);
                view = compileView(model, dbView);
                LayoutDefinition layoutDefinition = view.getBaseLayoutDefinition();
                if (layoutDefinition != null) {
                    compileLayout(model, layoutDefinition);
                }
            }
        }
        return view;
    }

    @Override
    public List<View> compile(List<View> viewList) {
        for (View view : viewList) {
            compile(view);
        }
        return viewList;
    }

    @Override
    public View compile(View view) {
        if (null != view.getCompiled() && view.getCompiled()) {
            return null;
        }
        String model = view.getModel();
        UIView uiView = parser(model, view.getName(), view.getTemplate());
        Deque<ViewCompileContext> viewStack = new ArrayDeque<>();
        viewStack.push(new ViewCompileContext(model));
        compileWidget(viewStack, uiView);
        view.setUiView(uiView);
        view.setTemplate(PamirsJsonUtils.toJSONString(uiView));
        return view;
    }

    @Override
    public UIField compile(ModelField modelField, UIField uiField) {
        uiField.setLabel(Optional.ofNullable(uiField.getLabel()).orElse(modelField.getDisplayName()))
                .setTtype(modelField.getTtype())
                .setRelatedTtype(modelField.getRelatedTtype())
                .setStoreSerialize(modelField.getStoreSerialize())
                .setMulti(Optional.ofNullable(uiField.getMulti()).orElse(modelField.getMulti()))
                .setDefaultValue(uiField.getDefaultValue())
                .setStore(Optional.ofNullable(uiField.getStore()).orElse(modelField.getStore()))
                .setIndex(Optional.ofNullable(uiField.getIndex()).orElse(modelField.getIndex()))
                .setUnique(Optional.ofNullable(uiField.getUnique()).orElse(modelField.getUnique()))
                .setRequired(Optional.ofNullable(uiField.getRequired())
                        .orElse(Optional.ofNullable(modelField.getRequired()).map(String::valueOf).orElse(modelField.getRequiredCondition())))
                .setSize(Optional.ofNullable(uiField.getSize()).orElse(TypeUtils.stringValueOf(modelField.getSize())))
                .setDecimal(Optional.ofNullable(uiField.getDecimal()).orElse(TypeUtils.stringValueOf(modelField.getDecimal())))
                .setMin(Optional.ofNullable(uiField.getMin()).orElse(modelField.getMin()))
                .setMax(Optional.ofNullable(uiField.getMax()).orElse(modelField.getMax()))
                .setFormat(Optional.ofNullable(uiField.getFormat()).filter(StringUtils::isNotBlank).orElse(null))
                .setLimit(Optional.ofNullable(uiField.getLimit()).orElse(modelField.getLimit()))
                .setInvisible(Optional.ofNullable(uiField.getInvisible())
                        .orElse(Optional.ofNullable(modelField.getInvisible()).map(String::valueOf).orElse(null)))
                .setName(modelField.getName())
        ;

        if (TtypeEnum.isRelationType(uiField.getExactTtype())) {
            uiField.setRelationStore(Optional.ofNullable(uiField.getRelationStore()).orElse(modelField.getRelationStore()))
                    .setReferences(Optional.ofNullable(uiField.getReferences()).orElse(modelField.getReferences()))
                    .setThrough(Optional.ofNullable(uiField.getThrough()).orElse(modelField.getThrough()))
                    .setRelationFields(Optional.ofNullable(uiField.getRelationFields()).orElse(modelField.getRelationFields()))
                    .setReferenceFields(Optional.ofNullable(uiField.getReferenceFields()).orElse(modelField.getReferenceFields()))
                    .setThroughRelationFields(Optional.ofNullable(uiField.getThroughRelationFields()).orElse(modelField.getThroughRelationFields()))
                    .setThroughReferenceFields(Optional.ofNullable(uiField.getThroughReferenceFields()).orElse(modelField.getThroughReferenceFields()))
                    .setLoad(Optional.ofNullable(uiField.getLoad()).orElse(modelField.getSearch()))
                    .setDomain(Optional.ofNullable(uiField.getDomain()).orElse(modelField.getDomain()))
                    .setDomainSize(Optional.ofNullable(uiField.getDomainSize()).orElse(modelField.getDomainSize()))
                    .setPageSize(Optional.ofNullable(uiField.getPageSize()).orElse(modelField.getPageSize()))
                    .setOrdering(Optional.ofNullable(uiField.getOrdering()).orElse(modelField.getOrdering()))
            ;
        }

        if (TtypeEnum.isRelatedType(uiField.getTtype().value())) {
            uiField.setRelated(Optional.ofNullable(uiField.getRelated()).orElse(modelField.getRelated()));
        }

        return uiField;
    }

    @Override
    public UIAction compile(Action action, UIAction uiAction) {
        uiAction.setLabel(Optional.ofNullable(uiAction.getLabel()).orElse(action.getLabel()));
        uiAction.setDisplayName(Optional.ofNullable(uiAction.getDisplayName()).orElse(action.getDisplayName()));
        uiAction.setActionType(Optional.ofNullable(uiAction.getActionType()).orElse(action.getActionType()));
        uiAction.setContextType(Optional.ofNullable(uiAction.getContextType()).orElse(action.getContextType()));
        uiAction.setModel(action.getModel());
        uiAction.setName(action.getName());
        if (null != action.getContext()) {
            if (null == uiAction.getContext()) {
                uiAction.setContext(action.getContext());
            } else {
                for (String key : action.getContext().keySet()) {
                    uiAction.getContext().putIfAbsent(key, action.getContext().get(key));
                }
            }
        }
        if (null != action.getMapping()) {
            if (null == uiAction.getMapping()) {
                uiAction.setMapping(action.getMapping());
            } else {
                for (String key : action.getMapping().keySet()) {
                    uiAction.getMapping().putIfAbsent(key, action.getMapping().get(key));
                }
            }
        }
        uiAction.setInvisible(Optional.ofNullable(uiAction.getInvisible()).orElse(action.getInvisible()));
        uiAction.setDisabled(Optional.ofNullable(uiAction.getDisabled()).orElse(action.getDisable()));

        if (ActionTypeEnum.VIEW.equals(action.getActionType())) {
            ViewAction viewAction = (ViewAction) action;
            uiAction.setTarget(Optional.ofNullable(uiAction.getTarget()).orElse(viewAction.getTarget()));
            uiAction.setResModel(viewAction.getResModel());
            uiAction.setResModule(viewAction.getResModule());
            String resModule = uiAction.getResModule();
            if (StringUtils.isNotBlank(resModule)) {
                ModuleDefinition moduleDefinition = PamirsSession.getContext().getModule(resModule);
                if (null != moduleDefinition) {
                    uiAction.setResModuleDisplayName(moduleDefinition.getDisplayName());
                    uiAction.setResModuleName(moduleDefinition.getName());
                }
            }
            String resModel = uiAction.getResModel();
            if (StringUtils.isNotBlank(resModel)) {
                ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(resModel);
                if (null != modelConfig) {
                    uiAction.setResModelName(modelConfig.getName());
                }
            }
            uiAction.setViewType(viewAction.getViewType());
            uiAction.setDataType(viewAction.getDataType());
            uiAction.setTheme(viewAction.getTheme());
            uiAction.setMask(viewAction.getMask());
            uiAction.setQueryMode(viewAction.getQueryMode());
            uiAction.setLoad(viewAction.getLoad());
            uiAction.setFunction(UiViewUtils.fetchFunctionDefinition(viewAction.getModel(), viewAction.getLoad()));
            uiAction.setDomain(Optional.ofNullable(uiAction.getDomain()).orElse(viewAction.getDomain()));
            uiAction.setLimit(viewAction.getLimit());
        } else if (ActionTypeEnum.URL.equals(action.getActionType())) {
            UrlAction urlAction = (UrlAction) action;
            uiAction.setTarget(Optional.ofNullable(uiAction.getTarget()).orElse(urlAction.getTarget()));
            uiAction.setUrl(Optional.ofNullable(uiAction.getUrl()).orElse(urlAction.getUrl()));
            uiAction.setCompute(Optional.ofNullable(uiAction.getCompute()).orElse(urlAction.getCompute()));
            uiAction.setFunction(UiViewUtils.fetchFunctionDefinition(urlAction.getModel(), uiAction.getCompute()));
        } else if (ActionTypeEnum.SERVER.equals(action.getActionType())) {
            ServerAction serverAction = (ServerAction) action;
            uiAction.setFun(serverAction.getFun());
            uiAction.setFunction(UiViewUtils.fetchServActionFunction(serverAction.getModel(), serverAction.getFun()));
            if (null == uiAction.getFunction()) {
                log.error(MessageFormat.format("找不到服务器动作对应的函数，model:{0}, name:{1}, fun:{2}",
                        serverAction.getModel(), serverAction.getName(), serverAction.getFun()));
            }
        } else {
            ClientAction clientAction = (ClientAction) action;
            uiAction.setFun(clientAction.getFun());
            uiAction.setCompute(clientAction.getCompute());
            uiAction.setFunction(UiViewUtils.fetchFunctionDefinition(clientAction.getModel(), clientAction.getCompute()));
        }
        return uiAction;
    }

    @Override
    public List<View> layout(List<View> viewList) {
        List<String> layoutNames = viewList.stream()
                .map(View::getBaseLayoutName)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(layoutNames)) {
            return viewList;
        }

        List<LayoutDefinition> layoutDefinitions = LayoutDefinitionCache.queryLayoutsByName(layoutNames);
        Map<String, LayoutDefinition> name2Layout = layoutDefinitions.stream().collect(Collectors.toMap(LayoutDefinition::getName, i -> i, (a, b) -> a));
        for (View view : viewList) {
            if (StringUtils.isEmpty(view.getBaseLayoutName())) {
                continue;
            }
            LayoutDefinition originLayoutDefinition = name2Layout.get(view.getBaseLayoutName());
            LayoutDefinition layoutDefinition = compileLayout(view.getModel(), ObjectUtils.clone(originLayoutDefinition));
            view.setBaseLayoutDefinition(layoutDefinition);
        }
        return viewList;
    }

    @Override
    public View layout(View view) {
        String baseLayoutName = view.getBaseLayoutName();
        if (StringUtils.isBlank(baseLayoutName)) {
            return view;
        }

        LayoutDefinition originLayoutDefinition = LayoutDefinitionCache.queryLayoutByName(baseLayoutName);
        if (null == originLayoutDefinition) {
            return view;
        }

        LayoutDefinition layoutDefinition = compileLayout(view.getModel(), ObjectUtils.clone(originLayoutDefinition));
        view.setBaseLayoutDefinition(layoutDefinition);
        return view;
    }

    @Override
    public List<View> auth(List<View> viewList) {
        for (View view : viewList) {
            auth(view);
        }
        return viewList;
    }

    @Override
    public View auth(View view) {
        UIWidget uiView = auth0((UIView) view.getUiView(), view.getModel(), new HashMap<>());
        view.setTemplate(PamirsJsonUtils.toJSONString(uiView));
        return view;
    }

    private UIWidget auth0(UIWidget uiWidget, String model, Map<String, UIAuth> modelAuth) {
        if (uiWidget == null) {
            return null;
        }
        UIAuth uiAuth = modelAuth.computeIfAbsent(model, UIAuth::generatorUIAuth);
        String childModel = model;
        if (uiWidget instanceof UIField) {
            UIField uiField = (UIField) uiWidget;
            String field = uiField.getData();
            ModelFieldConfig modelFieldConfig = uiField.getModelFieldConfig();
            if (modelFieldConfig == null) {
                modelFieldConfig = PamirsSession.getContext().getModelField(uiField.getModel(), field);
            }
            boolean isSkipAuth = false;
            if (modelFieldConfig != null && (Boolean.TRUE.equals(modelFieldConfig.getPk()) || modelFieldConfig.isVirtual())) {
                isSkipAuth = true;
            }
            if (!isSkipAuth) {
                // 可见
                Set<String> canReadAccessFields = uiAuth.getCanReadAccessFields();
                if (canReadAccessFields != null && !canReadAccessFields.contains(field)) {
                    return null;
                }
                // 可编辑
                Set<String> canUpdateAccessFields = uiAuth.getCanUpdateAccessFields();
                if (canUpdateAccessFields != null && !canUpdateAccessFields.contains(field)) {
                    uiField.setReadonly(Boolean.TRUE.toString());
                }
            }
            // 模型切换
            childModel = Optional.ofNullable(uiField.getReferences()).orElse(model);
        } else if (uiWidget instanceof UIAction) {
            UIAction localUIAction = ((UIAction) uiWidget);
            if (!isAccessAction(localUIAction)) {
                return null;
            }
            // 模型切换
            childModel = Optional.ofNullable(localUIAction.getResModel()).orElse(model);
        }
        if (uiWidget.getWidgets() != null) {
            String finalModel = childModel;
            uiWidget.setWidgets(uiWidget.getWidgets().stream().map(_w -> auth0(_w, finalModel, modelAuth)).filter(Objects::nonNull).collect(Collectors.toList()));
        }
        return uiWidget;
    }

    private boolean isAccessAction(UIAction action) {
        String sessionPath = action.getSessionPath();
        if (StringUtils.isBlank(sessionPath)) {
            return true;
        }
        AuthApi authApi = AuthApi.get();
        boolean isAccess = authApi.canAccessAction(sessionPath).getSuccess();
        if (!isAccess) {
            String model = action.getModel();
            String actionName = action.getName();
            if (StringUtils.isNoneBlank(model, actionName)) {
                return authApi.canAccessAction(model, actionName).getSuccess();
            }
        }
        return isAccess;
    }

    @Override
    public List<View> internationalization(List<View> viewList) {
        // TODO
        return viewList;
    }

    @Override
    public View internationalization(View view) {
        return internationalization(ListUtils.asList(view)).get(0);
    }

    @Override
    public List<View> userPreference(List<View> viewList, ViewAction viewAction) {
        UserPreferenceService userPreferenceService = Spider.getDefaultExtension(UserPreferenceService.class);
        for (View view : viewList) {
            String userPreference = userPreferenceService.load(viewAction, view);
            if (userPreference == null) {
                continue;
            }
            Map<String, String> extension = Optional.ofNullable(view.getExtension()).orElse(new HashMap<>());
            extension.put(ViewConstants.Extension.userPreference, userPreference);
            view.setExtension(extension);
        }
        return viewList;
    }

    @Override
    public View userPreference(View view, ViewAction viewAction) {
        return userPreference(ListUtils.asList(view), viewAction).get(0);
    }

    private View compileView(String model, View view) {
        String template = view.getTemplate();
        if (StringUtils.isBlank(template)) {
            return view;
        }
        view.setTemplate(compile(model, view.getName(), template));
        return view;
    }

    private LayoutDefinition compileLayout(String model, LayoutDefinition layoutDefinition) {
        String template = layoutDefinition.getTemplate();
        if (StringUtils.isBlank(template)) {
            return layoutDefinition;
        }
        layoutDefinition.setTemplate(compile(model, layoutDefinition.getName(), template));
        return layoutDefinition;
    }

    private UIView parser(String model, String name, String template) {
        UIView uiView = uiioManager.parseTemplate(template);
        uiView.setModel(model);
        if (name != null) {
            uiView.setName(name);
        }
        return uiView;
    }

    private String compile(String model, String name, String template) {
        UIView uiView = parser(model, name, template);
        return compile(uiView);
    }

    private String compile(UIView uiView) {
        Deque<ViewCompileContext> viewStack = new ArrayDeque<>();
        viewStack.push(new ViewCompileContext(uiView.getModel()));
        compileWidget(viewStack, uiView);
        return PamirsJsonUtils.toJSONString(uiView);
    }

    private void compileWidget(Deque<ViewCompileContext> viewStack, UIView currentView) {
        if (null == currentView) {
            return;
        }
        String currentModel = currentView.getModel();
        compileViewWidget(currentModel, currentView);
        compileWidget(viewStack, currentView, currentView.getWidgets(), AccessResourceInfoSession.getInfo());
    }

    private void compileWidget(Deque<ViewCompileContext> viewStack, UIView currentView, List<UIWidget> widgets, AccessResourceInfo info) {
        Map<String, UiRelationFieldCollection> relationFieldMap = new HashMap<>(16);
        collectVirtualFields(viewStack.peek(), currentView);
        compileWidget(viewStack, currentView, widgets, new HashSet<>(16), relationFieldMap, info);
        // 补充关系字段
        for (Map.Entry<String, UiRelationFieldCollection> entry : relationFieldMap.entrySet()) {
            UiRelationFieldCollection relationUiField = entry.getValue();
            relationUiField.targetWidgets.add(relationUiField.field);
        }
    }

    private void collectVirtualFields(ViewCompileContext compileContext, UIView currentView) {
        UIMetadata metadata = currentView.getMetadata();
        if (metadata == null) {
            return;
        }
        List<UIModel> models = metadata.getModel();
        if (CollectionUtils.isEmpty(models)) {
            return;
        }
        for (UIModel model : models) {
            String modelModel = model.getModel();
            if (StringUtils.isBlank(modelModel)) {
                continue;
            }
            List<UIField> fields = model.getField();
            if (CollectionUtils.isEmpty(fields)) {
                continue;
            }
            for (UIField field : fields) {
                String data = field.getData();
                if (StringUtils.isNotBlank(data)) {
                    compileContext.putVirtualField(modelModel, data, field);
                }
            }
        }
    }

    private void compileWidget(Deque<ViewCompileContext> viewStack, UIView currentView, List<UIWidget> widgets,
                               Set<String> allFieldSet, Map<String, UiRelationFieldCollection> relationFieldMap,
                               AccessResourceInfo info) {
        if (CollectionUtils.isEmpty(widgets)) {
            return;
        }
        ViewCompileContext compileContext = viewStack.peek();
        String currentModel = compileContext.getModel();
        for (UIWidget uiWidget : widgets) {
            boolean isView = DslNodeConstants.NODE_VIEW.equals(uiWidget.getDslNodeType()) || uiWidget instanceof UIView;
            boolean isRouteViewAction = false;
            boolean isRouteRelationField = false;
            boolean isFieldAction = false;
            AccessResourceInfo nextInfo = info;
            if (DslNodeConstants.NODE_ACTION.equals(uiWidget.getDslNodeType()) || uiWidget instanceof UIAction) {
                if (uiWidget.isCompiled()) {
                    continue;
                }
                uiWidget.setDslNodeType(DslNodeConstants.NODE_ACTION);
                UIAction uiAction = (UIAction) uiWidget;
                String model = Optional.ofNullable(uiAction.getModel()).filter(StringUtils::isNotBlank).orElse(currentModel);
                String actionName = Optional.ofNullable(uiAction.getName()).filter(StringUtils::isNotBlank).orElse(uiAction.getFun());
                Action action = null;
                if (StringUtils.isNotBlank(actionName)) {
                    action = metaCacheManager.fetchAction(model, actionName);
                    if (action == null) {
                        action = fetchInternalClientAction(model, actionName);
                    }
                }
                if (null != action) {
                    compile(action, uiAction);
                } else {
                    uiioManager.logFindAction(uiAction.getModel(), actionName);
                }
                if (info != null) {
                    String actionModel = uiAction.getModel();
                    if (StringUtils.isNotBlank(actionModel)) {
                        nextInfo = info.clone();
                        nextInfo.addActionPath(actionModel, uiAction.getName());
                        uiAction.setSessionPath(nextInfo.toString());
                    }
                }
                // 转换为关联模型
                String resModel = Optional.ofNullable(uiAction.getResModel()).orElse(uiAction.getModel());
                isRouteViewAction = ActionTypeEnum.VIEW.equals(uiAction.getActionType())
                        && StringUtils.isNotBlank(resModel);
                if (isRouteViewAction) {
                    viewStack.push(compileContext.transfer(resModel));
                }
                // 自动填充内嵌子视图
                if (viewStack.size() < 3) {
                    autoFillSubViewForAction(uiAction);
                }
            } else if (DslNodeConstants.NODE_FIELD.equals(uiWidget.getDslNodeType()) || uiWidget instanceof UIField) {
                if (uiWidget.isCompiled()) {
                    continue;
                }
                uiWidget.setDslNodeType(DslNodeConstants.NODE_FIELD);
                UIField uiField = (UIField) uiWidget;
                uiField.setModel(Optional.ofNullable(uiField.getModel()).orElse(currentModel));
                ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(uiField.getModel(), uiField.getData());
                if (modelFieldConfig == null) {
                    uiField.addProp(new Prop().setName(FieldPropConstants.IS_VIRTUAL).setValue(FieldPropConstants.IS_VIRTUAL_TRUE_VALUE));
                    modelFieldConfig = compileContext.getVirtualField(uiField.getModel(), uiField.getData());
                    if (modelFieldConfig == null) {
                        continue;
                    }
                }
                uiField.setModelFieldConfig(modelFieldConfig);
                ModelField modelField = modelFieldConfig.getModelField();
                compile(modelField, uiField);
                allFieldSet.add(uiField.getData());

                if (info != null) {
                    nextInfo = info.clone();
                    nextInfo.addFieldPath(modelField.getModel(), modelField.getField());
                }

                // 转换为关联模型
                String references = modelField.getReferences();
                isRouteRelationField = StringUtils.isNotBlank(references) && TtypeEnum.isRelationType(modelField.getExactTtype());

                if (CollectionUtils.isNotEmpty(uiWidget.getWidgets())) {
                    isFieldAction = uiWidget.getWidgets().stream().anyMatch(widget -> widget.getDslNodeType().equals(DslNodeConstants.NODE_ACTION));
                }
                if (isRouteRelationField) {
                    viewStack.push(compileContext.transfer(references));

                    // 收集关系字段
                    ModelConfig referenceModelConfig = PamirsSession.getContext().getSimpleModelConfig(references);
                    if (referenceModelConfig != null && !ModelTypeEnum.TRANSIENT.equals(referenceModelConfig.getType())) {
                        if (CollectionUtils.isNotEmpty(modelField.getRelationFields())) {
                            for (String relationField : modelField.getRelationFields()) {
                                if (FieldUtils.isConstantRelationFieldValue(relationField)) {
                                    continue;
                                }
                                if (allFieldSet.contains(relationField)) {
                                    continue;
                                }
                                UIField relationUiField = new UIField();
                                relationUiField.setDslNodeType(DslNodeConstants.NODE_FIELD);
                                relationUiField.setModel(uiField.getModel());
                                relationUiField.setData(relationField);
                                relationUiField.setInvisible(Boolean.TRUE.toString());
                                relationUiField.setPriority(UIConstants.DEFAULT_SYSTEM_FIELD_PRIORITY);
                                ModelFieldConfig relationModelFieldConfig = PamirsSession.getContext()
                                        .getModelField(relationUiField.getModel(), relationField);
                                if (null != relationModelFieldConfig) {
                                    ModelField relationModelField = relationModelFieldConfig.getModelField();
                                    compile(relationModelField, relationUiField);
                                }
                                relationFieldMap.put(relationField, new UiRelationFieldCollection(relationUiField, widgets));
                            }
                        }
                    }
                }

                // 移除重复的关系字段
                relationFieldMap.remove(uiField.getData());

                // 自动填充可选项配置
                UiViewUtils.fillOptions(modelField, uiField,
                        model -> Optional.ofNullable(PamirsSession.getContext().getSimpleModelConfig(model))
                                .map(ModelConfig::getModelDefinition).orElse(null),
                        (model, field) -> Optional.ofNullable(PamirsSession.getContext().getModelField(model, field))
                                .map(ModelFieldConfig::getModelField).orElse(null)
                );
                // 自动填充内嵌子视图
                autoFillSubViewForRelation(uiField);
            } else if (isView) {
                uiWidget.setDslNodeType(DslNodeConstants.NODE_VIEW);
                currentView = (UIView) uiWidget;
                String model = currentView.getModel();
                if (null == model) {
                    model = currentModel;
                    currentView.setModel(model);
                }
                viewStack.push(compileContext.transfer(model));
                compileViewWidget(model, currentView);
            } else if (DslNodeConstants.NODE_TEMPLATE.equals(uiWidget.getDslNodeType()) || uiWidget instanceof UITemplate) {
                uiWidget.setDslNodeType(DslNodeConstants.NODE_TEMPLATE);
                // 自动填充动作
                fillActions(viewStack, currentView, (UITemplate) uiWidget);
            } else if (DslNodeConstants.NODE_PACK.equals(uiWidget.getDslNodeType()) || uiWidget instanceof UIPack) {
                uiWidget.setDslNodeType(DslNodeConstants.NODE_PACK);
            } else if (DslNodeConstants.NODE_XSLOT.equals(uiWidget.getDslNodeType()) || uiWidget instanceof UISlot) {
                uiWidget.setDslNodeType(DslNodeConstants.NODE_SLOT);
            }

            boolean isPopViewStack = isView || isRouteViewAction || isRouteRelationField;

            List<UIWidget> childWidgets = uiWidget.getWidgets();
            if (isPopViewStack) {
                compileWidget(viewStack, currentView, childWidgets, nextInfo);
            } else if (isFieldAction) {
                compileWidget(viewStack, currentView, childWidgets, nextInfo);
            } else {
                compileWidget(viewStack, currentView, childWidgets, allFieldSet, relationFieldMap, info);
            }

            if (isPopViewStack) {
                viewStack.pop();
            }
        }
    }

    private void compileViewWidget(String model, UIView currentView) {
        if (null == currentView) {
            return;
        }
        ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(model);
        if (null != modelConfig) {
            currentView.setModelName(modelConfig.getName());
            currentView.setModelType(modelConfig.getType());
            currentView.setPk(modelConfig.getPk());
            currentView.setIndexes(modelConfig.getIndexes());
            currentView.setUniques(modelConfig.getUniques());
            currentView.setOrdering(modelConfig.getOrdering());
            String moduleName = Optional.ofNullable(PamirsSession.getContext().getModule(modelConfig.getModule()))
                    .map(ModuleDefinition::getName).orElse(null);
            if (StringUtils.isNotBlank(moduleName)) {
                currentView.setModuleName(moduleName);
            }
        }
    }

    private void fillActions(Deque<ViewCompileContext> viewStack, UIView currentView, UITemplate uiTemplate) {
        if (null == uiTemplate.getAutoFill() || !uiTemplate.getAutoFill() || !CollectionUtils.isEmpty(uiTemplate.getWidgets())) {
            return;
        }

        int depth = viewStack.size();
        String model = viewStack.peek().getModel();
        if (1 == depth) {
            autoFillActionsForMainView(model, currentView, uiTemplate);
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

    private void autoFillActionsForMainView(String model, UIView uiView, UITemplate uiTemplate) {
        List<Action> actionList = Optional.ofNullable(metaCacheManager.fetchActions(model))
                .map(v -> v.stream().filter(vv -> !SystemSourceEnum.UI.equals(vv.getSystemSource())).collect(Collectors.toList()))
                .orElse(null);
        if (CollectionUtils.isEmpty(actionList)) {
            return;
        }

        ViewTypeEnum viewType = uiView.getType();

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
                if (StringUtils.isNotBlank(action.getBindingViewName()) && !action.getBindingViewName().equals(uiView.getName())) {
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

                for (Map.Entry<String, Object> entry : Optional.ofNullable(action.getAttributes()).orElse(Collections.emptyMap()).entrySet()) {
                    if (StringUtils.isBlank(entry.getKey())) {
                        continue;
                    }
                    uiAction.addProp(new Prop().setName(entry.getKey()).setValue(entry.getValue()));
                }

                // 处理默认动作信息
                compileDefaultActions(uiAction);

                if (ViewTypeEnum.TABLE.equals(viewType)) {
                    if (ActionContextTypeEnum.SINGLE.equals(action.getContextType())) {
                        if (ViewSlotNameEnum.ROW_ACTIONS.value().equals(uiTemplate.getSlot())) {
                            uiActionList.add(uiAction);
                        }
                    } else {
                        if (ViewSlotNameEnum.ACTIONS.value().equals(uiTemplate.getSlot())) {
                            uiActionList.add(uiAction);
                        }
                    }
                } else {
                    if (ActionContextTypeEnum.SINGLE.equals(action.getContextType()) || ActionContextTypeEnum.SINGLE_AND_BATCH.equals(action.getContextType())) {
                        if (ViewSlotNameEnum.ACTIONS.value().equals(uiTemplate.getSlot())) {
                            uiActionList.add(uiAction);
                        }
                    }
                }
            }
        }

        for (UIAction item : sortActions(uiActionList)) {
            uiTemplate.addWidget(item);
        }

    }

    private void autoFillSubViewForAction(UIAction uiAction) {
        String model = Optional.ofNullable(uiAction.getResModel()).orElse(uiAction.getModel());
        if (null == uiAction.getAutoFill() || !uiAction.getAutoFill()
                || !ActionTypeEnum.VIEW.equals(uiAction.getActionType())
                || !CollectionUtils.isEmpty(uiAction.getWidgets())
                || !ActionTargetEnum.DIALOG.equals(uiAction.getTarget())
                && !ActionTargetEnum.DRAWER.equals(uiAction.getTarget())) {
            return;
        }

        String template = metaCacheManager.fetchViewTemplate(model, uiAction.getResViewName(), uiAction.getViewType());
        UIView subView = uiioManager.parseTemplate(template);
        subView.setModel(model);
        uiAction.addWidget(subView);
    }

    private void autoFillSubViewForRelation(UIField uiField) {
        if (!WidgetEnum.TABLE.value().equals(uiField.getWidget())
                && !WidgetEnum.FORM.value().equals(uiField.getWidget())
                && !WidgetEnum.DETAIL.value().equals(uiField.getWidget())
                || !CollectionUtils.isEmpty(uiField.getWidgets())) {
            return;
        }

        String model = uiField.getReferences();
        ViewTypeEnum viewType = ViewTypeEnum.TABLE;
        String resViewName = uiField.getResViewName();

        String template = metaCacheManager.fetchViewTemplate(model, resViewName, viewType);

        UIView subView = uiioManager.parseTemplate(template);
        subView.setModel(Optional.ofNullable(model).orElse(uiField.getModel()));
        uiField.addWidget(subView);
    }

    /**
     * 处理默认动作信息
     *
     * @param uiAction 动作
     */
    private static void compileDefaultActions(UIAction uiAction) {
        if (FunctionConstants.create.equals(uiAction.getName()) || FunctionConstants.update.equals(uiAction.getName())) {
            uiAction.addProp(new Prop().setName(ActionPropConstants.ATTR_GO_BACK).setValue(Boolean.TRUE));
            uiAction.addProp(new Prop().setName(ActionPropConstants.ATTR_VALIDATE_FORM).setValue(Boolean.TRUE));
        }
    }

    /**
     * 动作排序
     *
     * @param actionList 动作列表
     * @return 排序后动作列表
     */
    private static List<UIAction> sortActions(List<UIAction> actionList) {
        return actionList.stream().sorted(Comparator.comparing(UIAction::getPriority)).collect(Collectors.toList());
    }

    private static class UiRelationFieldCollection {

        private final UIField field;

        private final List<UIWidget> targetWidgets;

        public UiRelationFieldCollection(UIField field, List<UIWidget> targetWidgets) {
            this.field = field;
            this.targetWidgets = targetWidgets;
        }
    }
}
