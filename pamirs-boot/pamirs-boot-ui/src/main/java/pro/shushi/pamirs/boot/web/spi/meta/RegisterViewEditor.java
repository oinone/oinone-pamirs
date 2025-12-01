package pro.shushi.pamirs.boot.web.spi.meta;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.constants.ViewConstants;
import pro.shushi.pamirs.boot.base.enmu.QueryModeEnum;
import pro.shushi.pamirs.boot.base.enmu.ViewBizTypeEnum;
import pro.shushi.pamirs.boot.base.enmu.WidgetEnum;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.boot.base.ux.annotation.field.UxIgnore;
import pro.shushi.pamirs.boot.base.ux.annotation.field.UxWidget;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxDetail;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxForm;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxTable;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxTableSearch;
import pro.shushi.pamirs.boot.base.ux.constants.GridConstants;
import pro.shushi.pamirs.boot.base.ux.constants.LayoutPropNameConstants;
import pro.shushi.pamirs.boot.base.ux.constants.TablePropConstants;
import pro.shushi.pamirs.boot.base.ux.enmu.ViewSlotNameEnum;
import pro.shushi.pamirs.boot.base.ux.entity.RegisterSearchWidget;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.base.ux.model.UIWidget;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.boot.base.ux.model.view.UIField;
import pro.shushi.pamirs.boot.base.ux.model.view.UIPack;
import pro.shushi.pamirs.boot.base.ux.model.view.UITemplate;
import pro.shushi.pamirs.boot.base.ux.spi.ViewTemplateStrategyApi;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.boot.web.constants.BusinessModelConstants;
import pro.shushi.pamirs.boot.web.constants.GroupConstants;
import pro.shushi.pamirs.boot.web.spi.domain.RegisterViewContext;
import pro.shushi.pamirs.boot.web.utils.*;
import pro.shushi.pamirs.framework.common.config.TtlAsyncTaskExecutor;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.orm.systems.ModelInheritedApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;
import pro.shushi.pamirs.meta.domain.model.*;
import pro.shushi.pamirs.meta.domain.module.ModuleCategory;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.meta.util.PropUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_CLASS_IS_NOT_EXISTS_ERROR;

/**
 * 默认视图元数据编辑计算
 * <p>
 * 在加载首页和菜单元数据、生成默认窗口动作之后，业务应用修改元数据之前执行
 * 2022/3/1 3:29 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 66)
@Component
public class RegisterViewEditor implements MetaDataEditor {

    @Value("${pamirs.view.auto-create-default-view:true}")
    private Boolean autoCreateDefaultView;

    @Value("${pamirs.view.auto-create-biz-view:true}")
    private Boolean autoCreateBizView;

    private static final String ACTIVE_RECORD = "activeRecord";

    private static final Set<String> filterModules = Sets.newHashSet(
            ModuleConstants.MODULE_BASE,
            "apps",
            "auth",
            "common",
            "data_audit",
            "eip",
            "expression",
            "file",
            "management_center",
            "message",
            "my_center",
            "print",
            "sql_record",
            "sso",
            "sys_setting",
            "tp_map",
            "tp_communication",
            "tp_message",
            "timezone",
            "translate",
            "trigger",

            "sequence",
            "datavi",
            "designer_metadata",
            "workbench",
            "workflow",

            "designer_common",
            "workflow_designer_base",
            "model_designer",
            "ui_designer",
            "ui_designer_biz_widget",
            "ui_designer_data_widget",
            "workflow_designer",
            "microflow_designer",
            "eip_designer",
            "data_designer",
            "ai_designer",
            "print_designer"
    );

    private static final Set<String> systemModules = Sets.newHashSet(
            "business",
            "channel",
            "resource",
            "user"
    );

    private static final Set<String> filterSuperModels = Sets.newHashSet(
            ModuleCategory.MODEL_MODEL, ModuleDefinition.MODEL_MODEL, UeModule.MODEL_MODEL,
            ModelCategory.MODEL_MODEL, ModelDefinition.MODEL_MODEL, UeModel.MODEL_MODEL,
            ModelField.MODEL_MODEL,
            DataDictionary.MODEL_MODEL, DataDictionaryItem.MODEL_MODEL,
            SequenceConfig.MODEL_MODEL,
            Menu.MODEL_MODEL,
            View.MODEL_MODEL,
            Action.MODEL_MODEL, ClientAction.MODEL_MODEL, ServerAction.MODEL_MODEL, UrlAction.MODEL_MODEL, ViewAction.MODEL_MODEL
    );

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        if (autoCreateDefaultView != null && !autoCreateDefaultView) {
            return;
        }
        if (MapUtils.isEmpty(metaMap)) {
            return;
        }

        long startTotal = System.currentTimeMillis();
        // 按threadSize进行分组，分批处理
        int threadSize = TtlAsyncTaskExecutor.nThreads();
        Map<String, Meta> computeMetaMap = new HashMap<>();
        for (Map.Entry<String, Meta> entry : metaMap.entrySet()) {
            String module = entry.getKey();
            if (filterModules.contains(module)) {
                continue;
            }
            if (autoCreateBizView != null && !autoCreateBizView) {
                if (!systemModules.contains(module)) {
                    continue;
                }
            }
            ModuleDefinition moduleDefinition = entry.getValue().getCurrentModule();
            if (moduleDefinition == null) {
                continue;
            }
            if (Boolean.FALSE.equals(moduleDefinition.getSys()) && SystemSourceEnum.UI.equals(moduleDefinition.getSystemSource())) {
                continue;
            }
            computeMetaMap.put(module, entry.getValue());
        }
        List<Map<String, Meta>> allMetaMapList = pro.shushi.pamirs.meta.common.util.MapUtils.splitByChunkSize(computeMetaMap, threadSize);
        log.info("默认视图元数据计算Summary,totalSize:[{}],chunkSize:[{}],groupSize:[{}]", computeMetaMap.size(), threadSize, allMetaMapList.size());
        for (Map<String, Meta> oneMetaMapGroup : allMetaMapList) {
            CountDownLatch latch = new CountDownLatch(oneMetaMapGroup.size());
            for (String module : oneMetaMapGroup.keySet()) {
                TtlAsyncTaskExecutor.getExecutorService().execute(() -> {
                    try {
                        long start = System.currentTimeMillis();
                        Meta meta = oneMetaMapGroup.get(module);
                        MetaData metaData = meta.getCurrentModuleData();

                        List<ViewAction> viewActionList = metaData.getDataList(ViewAction.MODEL_MODEL);

                        // 构造模型动作映射表
                        Map<String, List<Action>> actionMap = ActionUtils.makeActionMap(metaData);

                        // 为窗口动作生成默认视图
                        Set<String> viewActionModelSet = null;
                        if (CollectionUtils.isNotEmpty(viewActionList)) {
                            viewActionModelSet = viewActionList.stream()
                                    .filter(v -> !SystemSourceEnum.UI.equals(v.getSystemSource())) // 过滤掉无代码设计的跳转动作
                                    .map(v -> StringUtils.isBlank(v.getResModel()) ? v.getModel() : v.getResModel())
                                    .filter(StringUtils::isNotBlank)
                                    .collect(Collectors.toSet());
                            log.debug("窗口动作生成默认视图，数量:[{}]", viewActionModelSet.size());
                            for (String model : viewActionModelSet) {
                                long start0 = System.currentTimeMillis();
                                ModelDefinition modelDefinition = fetchModelDefinition(meta, model);
                                if (null == modelDefinition) {
                                    continue;
                                }
                                if (filterModel(modelDefinition)) {
                                    continue;
                                }
                                makeDefaultViews(meta, modelDefinition, actionMap);
                                long end0 = System.currentTimeMillis();
                                log.debug("[{}],time:[{}]ms", model, end0 - start0);
                            }
                        }

                        // 为UX注解生成视图
                        makeDefaultViewByAnnotation(meta, metaData, actionMap, viewActionModelSet);
                        long end = System.currentTimeMillis();
                        log.info("[{}]模块,UX注解生成视图,time:[{}]ms", module, end - start);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                log.error("视图元数据编辑计算等待超时", e);
            }
        }

        long endTotal = System.currentTimeMillis();
        log.info("默认视图元数据编辑计算,time:[{}]ms", endTotal - startTotal);
    }

    private boolean filterModel(ModelDefinition modelDefinition) {
        List<String> superModels = modelDefinition.getSuperModels();
        if (CollectionUtils.isNotEmpty(superModels)) {
            for (String superModel : superModels) {
                if (filterSuperModels.contains(superModel)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void makeDefaultViewByAnnotation(Meta meta, MetaData metaData, Map<String, List<Action>> actionMap, Set<String> viewActionModelSet) {
        boolean noViewActionModel = null == viewActionModelSet;
        List<ModelDefinition> modelDefinitionList = metaData.getModelList();
        for (ModelDefinition modelDefinition : modelDefinitionList) {
            if (!noViewActionModel && viewActionModelSet.contains(modelDefinition.getModel())) {
                continue;
            }
            String lname = modelDefinition.getLname();
            if (null == lname) {
                continue;
            }
            Class<?> modelClazz = null;
            try {
                modelClazz = TypeUtils.getClass(lname);
            } catch (PamirsException e) {
                log.error(MessageFormat.format("{0}或者存在脏数据，类：{1}", BASE_CLASS_IS_NOT_EXISTS_ERROR.msg(), lname), e);
            }
            if (null == modelClazz || !TypeUtils.isModelClass(modelClazz)) {
                continue;
            }
            RegisterViewContext registerViewContext = new RegisterViewContext(meta, modelDefinition, actionMap);
            UxTable uxTable = AnnotationUtils.findAnnotation(modelClazz, UxTable.class);
            UxForm uxForm = AnnotationUtils.findAnnotation(modelClazz, UxForm.class);
            UxDetail uxDetail = AnnotationUtils.findAnnotation(modelClazz, UxDetail.class);
            if (null != uxTable) {
                // 创建默认表格视图
                makeDefaultView(registerViewContext,
                        ViewConstants.Name.tableView,
                        null,
                        ViewTypeEnum.TABLE);
            }
            if (null != uxForm) {
                // 创建默认表单视图
                makeDefaultView(registerViewContext,
                        ViewConstants.Name.formView,
                        null,
                        ViewTypeEnum.FORM);
            }
            if (null != uxDetail) {
                // 创建默认详情视图
                makeDefaultView(registerViewContext,
                        ViewConstants.Name.detailView,
                        null,
                        ViewTypeEnum.DETAIL);
            }
        }
    }

    public void makeDefaultViews(Meta meta, ModelDefinition modelDefinition, Map<String, List<Action>> actionMap) {

        RegisterViewContext registerViewContext = new RegisterViewContext(meta, modelDefinition, actionMap);

        // 创建默认表格视图
        makeDefaultView(registerViewContext,
                ViewConstants.Name.tableView,
                null,
                ViewTypeEnum.TABLE);

        // 创建默认表单视图
        makeDefaultView(registerViewContext,
                ViewConstants.Name.formView,
                null,
                ViewTypeEnum.FORM);

        // 创建默认详情视图
        makeDefaultView(registerViewContext,
                ViewConstants.Name.detailView,
                null,
                ViewTypeEnum.DETAIL);

    }

    public void makeDefaultView(RegisterViewContext context,
                                String viewName, String title, ViewTypeEnum viewType) {
        if (null == context.getClazz()) {
            return;
        }

        // 构造默认视图DSL
        makeView(context, viewName, title, viewType,
                view -> makeDefaultUiView(context, view, viewType, null, 0),
                ViewConstants.defaultPriority);
    }

    private View makeView(RegisterViewContext context,
                          String viewName, String title, ViewTypeEnum viewType,
                          Function<View, UIView> uiViewMaker, int priority) {
        ModelDefinition targetModel = context.getModelDefinition();
        String model = targetModel.getModel();
        String module = targetModel.getModule();
        String sign = View.sign(model, viewName);
        View defaultView = context.getMeta().getData().get(module)
                .getDataItem(View.MODEL_MODEL, sign);
        boolean newView = false;
        if (null == defaultView) {
            defaultView = new View();
            newView = true;
        }
        defaultView.setCompiled(false);
        View rebuildView = defaultView;
        boolean needRebuildDefaultView = newView || SystemSourceEnum.SYSTEM.equals(defaultView.getSystemSource())
                || defaultView.isMetaCompleted();
        if (!needRebuildDefaultView) {
            // 构造一个视图对象来生成自定义默认视图子视图的动作
            rebuildView = new View();
        }
        rebuildView.setTitle(title)
                .setModel(model)
                .setName(viewName)
                .setBizType(ViewBizTypeEnum.OPERATIONS_MANAGEMENT)
                .setType(viewType)
                .setShow(ActiveEnum.ACTIVE)
                .setActive(ActiveEnum.ACTIVE)
                .setPriority(priority)
                .setSystemSource(SystemSourceEnum.SYSTEM);
        rebuildView.setSign(sign);
        if (needRebuildDefaultView) {
            defaultView.setTemplate(ViewXmlUtils.toXML(uiViewMaker.apply(defaultView)));
            if (newView) {
                defaultView.construct();
                context.getMeta().getData().get(module).addData(defaultView);
            } else {
                defaultView.disableMetaCompleted();
            }
        } else {// 为自定义默认视图生成子视图的动作
            rebuildView.setTemplate(ViewXmlUtils.toXML(uiViewMaker.apply(rebuildView)));
        }
        return defaultView;
    }

    public UIView makeDefaultUiView(RegisterViewContext context, View view, ViewTypeEnum topViewType, ModelField subViewField, int depth) {
        ModelDefinition modelDefinition = context.getModelDefinition();
        String model = modelDefinition.getModel();
        UIView uiView = new UIView();
        uiView.setModel(model)
                .setTitle(view.getTitle())
                .setType(view.getType())
                .setName(view.getName());

        if (ViewTypeEnum.TABLE.equals(view.getType())) {
            UxTable uxTable = AnnotationUtils.findAnnotation(context.getClazz(), UxTable.class);
            uiView.setEnableSequence(Optional.ofNullable(uxTable).map(UxTable::enableSequence).orElse(false));

            // 处理布局
            uiView.setCols(Optional.ofNullable(uxTable).map(UxTable::grid).orElse(GridConstants.defaultViewGrid));

            // 处理动作
            if (0 == depth) {
                makeUiActions(uiView, ViewTypeEnum.TABLE);
            } else if (1 == depth) {
                makeUiActions(context.getMeta(), context.getActionMap(), uiView, topViewType, subViewField, depth);
            }

            // 处理搜索字段
            boolean enableSearch = Optional.ofNullable(uxTable).map(UxTable::enableSearch).orElse(true);
            List<UIWidget> searchFieldList = new ArrayList<>();
            List<UIWidget> autoSearchFieldList = new ArrayList<>();

            ViewTemplateStrategyApi strategyApi = Spider.getDefaultExtension(ViewTemplateStrategyApi.class);
            // 处理字段
            makeUiFields(context, model, uiView, ViewTypeEnum.TABLE, topViewType, subViewField, (field, modelField) -> {
                if (enableSearch && depth != 1) {
                    UxTableSearch.FieldWidget uxTableSearchField = AnnotationUtils.findAnnotation(field, UxTableSearch.FieldWidget.class);
                    if (null != uxTableSearchField) {
                        UxWidget uxWidget = uxTableSearchField.value();
                        UIField uiField = makeUiField(ViewTypeEnum.TABLE, modelField, uxWidget);
                        searchFieldList.add(uiField);
                    } else {
                        if (CollectionUtils.isEmpty(searchFieldList)) {
                            // 只要有一个显式配置的搜索字段，默认搜索字段就短路掉
                            RegisterSearchWidget searchWidget = strategyApi.computeSearchWidget(modelDefinition, modelField);
                            if (searchWidget != null) {
                                UIField uiField = makeUiField(ViewTypeEnum.TABLE, modelField, (UxWidget) searchWidget);
                                autoSearchFieldList.add(uiField);
                            }
                        }
                    }
                }
            }, field -> {
                UxTable.FieldWidget uxField = AnnotationUtils.findAnnotation(field, UxTable.FieldWidget.class);
                return Optional.ofNullable(uxField).map(UxTable.FieldWidget::value).orElse(null);
            }, depth);

            // 处理表格搜索
            if (enableSearch) {
                List<UIWidget> sortSearchFieldList = searchFieldList.stream().sorted(Comparator.comparing(UIWidget::getPriority)).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(sortSearchFieldList)) {
                    sortSearchFieldList = autoSearchFieldList.stream().sorted(Comparator.comparing(UIWidget::getPriority)).collect(Collectors.toList());
                }
                UxTableSearch uxTableSearch = AnnotationUtils.findAnnotation(context.getClazz(), UxTableSearch.class);
                int cols = Optional.ofNullable(uxTableSearch).map(UxTableSearch::grid).orElse(GridConstants.defaultTableSearchGrid);
                Prop prop = new Prop();
                prop.setName(LayoutPropNameConstants.PROP_COLS);
                prop.setValue(cols);
                UITemplate uiTemplate = makeFieldsSlot(ViewSlotNameEnum.SEARCH, sortSearchFieldList);
                uiTemplate.addProp(prop);
                uiView.addWidget(uiTemplate);
            }
        } else if (ViewTypeEnum.FORM.equals(view.getType())) {
            UxForm viewAnnotation = AnnotationUtils.findAnnotation(context.getClazz(), UxForm.class);

            // 处理布局
            uiView.setCols(Optional.ofNullable(viewAnnotation).map(UxForm::grid).orElse(GridConstants.defaultViewGrid));
            uiView.setDefaultGroup(Optional.ofNullable(viewAnnotation).map(UxForm::group).filter(StringUtils::isNotBlank).orElse(null));
            uiView.setTabsTable(Optional.ofNullable(viewAnnotation).map(UxForm::tabsTable).orElse(true));

            // 处理动作
            if (depth < 2) {
                makeUiActions(uiView, ViewTypeEnum.FORM);
            }

            // 处理字段
            makeUiFields(context, model, uiView, ViewTypeEnum.FORM, topViewType, subViewField, field -> {
                UxForm.FieldWidget uxField = AnnotationUtils.findAnnotation(field, UxForm.FieldWidget.class);
                return Optional.ofNullable(uxField).map(UxForm.FieldWidget::value).orElse(null);
            }, depth);
        } else if (ViewTypeEnum.DETAIL.equals(view.getType())) {
            UxDetail viewAnnotation = AnnotationUtils.findAnnotation(context.getClazz(), UxDetail.class);

            // 处理布局
            uiView.setCols(Optional.ofNullable(viewAnnotation).map(UxDetail::grid).orElse(GridConstants.defaultViewGrid));
            uiView.setDefaultGroup(Optional.ofNullable(viewAnnotation).map(UxDetail::group).filter(StringUtils::isNotBlank).orElse(null));
            uiView.setTabsTable(Optional.ofNullable(viewAnnotation).map(UxDetail::tabsTable).orElse(true));

            // 处理动作
            makeUiActions(uiView, ViewTypeEnum.DETAIL);

            // 处理字段
            makeUiFields(context, model, uiView, ViewTypeEnum.DETAIL, topViewType, subViewField, field -> {
                UxDetail.FieldWidget uxField = AnnotationUtils.findAnnotation(field, UxDetail.FieldWidget.class);
                return Optional.ofNullable(uxField).map(UxDetail.FieldWidget::value).orElse(null);
            }, depth);
        }
        return uiView;
    }

    private void makeUiActions(UIView uiView, ViewTypeEnum viewType) {
        uiView.addWidget(makeDefaultActionsSlot(ViewSlotNameEnum.ACTIONS.value()));
        if (ViewTypeEnum.TABLE.equals(viewType)) {
            uiView.addWidget(makeDefaultActionsSlot(ViewSlotNameEnum.ROW_ACTIONS.value()));
        }
    }

    private void makeUiActions(Meta meta, Map<String, List<Action>> actionMap, UIView uiView, ViewTypeEnum topViewType,
                               ModelField subViewField, int depth) {
        if (ViewTypeEnum.DETAIL.equals(topViewType)) {
            return;
        }
        if (null == subViewField) {
            return;
        }
        String model = subViewField.getReferences();
        // 处理动作
        List<UIAction> globalActionList = new ArrayList<>();
        List<UIAction> rowActionList = new ArrayList<>();
        if (TtypeEnum.M2M.equals(subViewField.getExactTtype())) {
            globalActionList.add(UiActionUtils.refreshActionMetaData(
                    meta.getCurrentModuleData(),
                    uiView,
                    makeActionSubView(meta, actionMap, ViewActionUtils.makeM2MCreateAction(model).setRefs(false).setDomain(subViewField.getDomain()),
                            topViewType, subViewField, depth),
                    SystemSourceEnum.SYSTEM));
        } else {
            globalActionList.add(UiActionUtils.refreshActionMetaData(
                    meta.getCurrentModuleData(),
                    uiView,
                    makeActionSubView(meta, actionMap, ViewActionUtils.makeO2MCreateAction(model).setRefs(false), topViewType, subViewField, depth),
                    SystemSourceEnum.SYSTEM));
            rowActionList.add(UiActionUtils.refreshActionMetaData(
                    meta.getCurrentModuleData(),
                    uiView,
                    makeActionSubView(meta, actionMap, ViewActionUtils.makeO2MEditAction(model).setRefs(false), topViewType, subViewField, depth),
                    SystemSourceEnum.SYSTEM));
        }
        UIAction x2mDeleteAction = UiActionUtils.refreshActionMetaData(
                meta.getCurrentModuleData(),
                uiView,
                ClientActionUtils.makeX2MDeleteAction().setModel(model).setRefs(false),
                SystemSourceEnum.SYSTEM);
        globalActionList.add(x2mDeleteAction);
        rowActionList.add(x2mDeleteAction);

        uiView.addWidget(makeActionsSlot(ViewSlotNameEnum.ACTIONS.value(), globalActionList));
        uiView.addWidget(makeActionsSlot(ViewSlotNameEnum.ROW_ACTIONS.value(), rowActionList));

        globalActionList.forEach(v -> v.setPriority(null));
        rowActionList.forEach(v -> v.setPriority(null));
    }

    @SuppressWarnings("unused")
    private UIAction makeActionSubView(Meta meta, Map<String, List<Action>> actionMap,
                                       UIAction uiAction,
                                       ViewTypeEnum topViewType, ModelField subViewField, int depth) {
        // 创建动作内嵌子视图
        String model = Optional.ofNullable(uiAction.getResModel()).orElse(uiAction.getModel());
        ModelDefinition refModelDefinition = fetchModelDefinition(meta, model);
        RegisterViewContext refContext = new RegisterViewContext(meta, refModelDefinition, actionMap);
        if (null != refContext.getClazz()) {
            String viewName = ViewConstants.Name.dialogFormView;
            ViewTypeEnum viewType = ViewTypeEnum.FORM;
            if (null == subViewField) {
                return uiAction;
            }
            if (TtypeEnum.M2M.equals(subViewField.getExactTtype())) {
                viewName = ViewConstants.Name.dialogTableView;
                viewType = ViewTypeEnum.TABLE;
            }
            if (!model.equals(subViewField.getModel())) {
                ModelDefinition sourceModelDefinition = fetchModelDefinition(meta, subViewField.getModel());
                if (null == sourceModelDefinition) {
                    return uiAction;
                }
                String suffix = ViewConstants.subViewNameSuffix + PStringUtils.capitalize(sourceModelDefinition.getName()) +
                        ViewConstants.subViewNameSuffix + PStringUtils.capitalize(meta.getModule());
                viewName = viewName + suffix;
                // 生成新的action
                uiAction.setName(uiAction.getName() + suffix);
            }
            View subView = makeView(refContext, viewName, null, viewType,
                    view -> makeDefaultUiView(refContext, view, topViewType, subViewField, depth + 1),
                    ViewConstants.defaultPriority + 10);
            uiAction.setResViewName(subView.getName());
            uiAction.setViewType(subView.getType());
        }
        return uiAction;
    }

    private UITemplate makeDefaultActionsSlot(String actionSlotName) {
        UITemplate globalActions = new UITemplate();
        globalActions.setSlot(actionSlotName);
        globalActions.setAutoFill(true);
        return globalActions;
    }

    private UITemplate makeActionsSlot(String actionSlotName, List<UIAction> actionList) {
        if (CollectionUtils.isNotEmpty(actionList)) {
            UITemplate globalActions = new UITemplate();
            globalActions.setSlot(actionSlotName);
            List<UIWidget> sortedList = actionList.stream()
                    .sorted(Comparator.comparing(UIAction::getPriority))
                    .collect(Collectors.toList());
            globalActions.setWidgets(sortedList);
            return globalActions;
        }
        return null;
    }

    private void makeUiFields(RegisterViewContext context,
                              String model, UIView uiView,
                              ViewTypeEnum viewType,
                              ViewTypeEnum topViewType,
                              ModelField subViewField,
                              Function<Field, UxWidget> widgetFetcher,
                              int depth) {
        makeUiFields(context, model, uiView, viewType, topViewType, subViewField, null, widgetFetcher, depth);
    }

    private void makeUiFields(RegisterViewContext context,
                              String model, UIView uiView,
                              ViewTypeEnum viewType,
                              ViewTypeEnum topViewType,
                              ModelField subViewField,
                              BiConsumer<Field, ModelField> fieldConsumer,
                              Function<Field, UxWidget> widgetFetcher,
                              int depth) {
        // 计算属性映射
        Map<String, String> computeMapping = new HashMap<>();

        // 处理字段
        List<UIWidget> uiFieldList = new ArrayList<>();
        for (Field field : context.fetchFieldList()) {
            pro.shushi.pamirs.meta.annotation.Field.field fieldFieldAnnotation = AnnotationUtils
                    .getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.field.class);
            String fieldField = Optional.ofNullable(fieldFieldAnnotation)
                    .map(pro.shushi.pamirs.meta.annotation.Field.field::value).orElse(field.getName());
            ModelField modelField = context.getMeta().getModelField(model, fieldField);
            if (null == modelField) {
                continue;
            }
            if (null != subViewField
                    && isSymmetricMappingRelation(subViewField, modelField, m -> context.getMeta().getModel(m))) {
                continue;
            }
            if (modelField.hasBitOption(FieldBitOptions.UN_SUPPORT_CLIENT.getOption())) {
                continue;
            }
            if (null != fieldConsumer) {
                fieldConsumer.accept(field, modelField);
            }
            UxIgnore uxIgnore = AnnotationUtils.findAnnotation(field, UxIgnore.class);
            if (null != uxIgnore) {
                if (ArrayUtils.isEmpty(uxIgnore.value()) || ArrayUtils.contains(uxIgnore.value(), viewType)) {
                    continue;
                }
            }
            UxWidget uxWidget = widgetFetcher.apply(field);
            UIField uiField = makeUiField(viewType, modelField, uxWidget);

            TtypeEnum ttype = modelField.getTtype();
            if (TtypeEnum.isRelationOne(ttype)) {
                if (TtypeEnum.M2O.equals(ttype)) {
                    List<String> relationFields = modelField.getRelationFields();
                    List<String> referenceFields = modelField.getReferenceFields();
                    if (CollectionUtils.isNotEmpty(relationFields) &&
                            CollectionUtils.isNotEmpty(referenceFields) &&
                            relationFields.size() == referenceFields.size()) {
                        String data = uiField.getData();
                        for (int i = 0; i < relationFields.size(); i++) {
                            String relationField = relationFields.get(i);
                            String referenceField = referenceFields.get(i);
                            computeMapping.put(relationField, String.join(CharacterConstants.SEPARATOR_DOT, ACTIVE_RECORD, data, referenceField));
                        }
                    }
                }

                if (CollectionUtils.isEmpty(uiField.getOptionFields()) && isReferencePamirsFile(modelField.getReferences())) {
                    uiField.setOptionFields(Lists.newArrayList("url"));
                }
            }

            if (depth < 2) {
                // 创建关联关系内嵌子视图
                makeFieldSubView(context, viewType, topViewType, modelField, uiField, depth);
            } else if (TtypeEnum.isRelationType(modelField.getExactTtype())) {
                // 忽略传输模型关联关系字段
                String references = modelField.getReferences();
                if (StringUtils.isNotBlank(references)) {
                    ModelDefinition referenceModelConfig = context.getMeta().getModel(references);
                    if (ModelTypeEnum.TRANSIENT.equals(referenceModelConfig.getType())) {
                        continue;
                    }
                }
                // 设置为下拉控件
                if (StringUtils.isBlank(uiField.getWidget())) {
                    uiField.setWidget(WidgetEnum.SELECT.value());
                }
            }

            // 可选项配置
            UiViewUtils.fillOptions(modelField, uiField,
                    vModel -> context.getMeta().getModel(vModel),
                    (vModel, vField) -> context.getMeta().getModelField(vModel, vField));

            uiFieldList.add(uiField);
        }

        uiFieldList = uiFieldList.stream().peek(v -> {
            if (v instanceof UIField) {
                UIField uiField = (UIField) v;
                String compute = computeMapping.get(uiField.getData());
                if (StringUtils.isNotBlank(compute)) {
                    uiField.setReadonly(Optional.ofNullable(uiField.getReadonly()).orElse(Boolean.TRUE.toString()));
                    uiField.setInvisible(Optional.ofNullable(uiField.getInvisible()).orElse(Boolean.TRUE.toString()));
                    uiField.setCompute(Optional.ofNullable(uiField.getCompute()).orElse(compute));
                }
            }
        }).sorted(Comparator.comparing(UIWidget::getPriority)).collect(Collectors.toList());

        layoutUiWidgets(context.getMeta(), uiView, viewType, model, uiFieldList, depth);
    }

    private boolean isReferencePamirsFile(String model) {
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        if (modelConfig != null) {
            if ("base.PamirsFile".equals(modelConfig.getModel())) {
                return true;
            }
            if (ModelTypeEnum.ABSTRACT.equals(modelConfig.getType()) || ModelTypeEnum.TRANSIENT.equals(modelConfig.getType())) {
                return false;
            }
            List<String> superModels = modelConfig.getSuperModels();
            if (CollectionUtils.isNotEmpty(superModels)) {
                for (String superModel : superModels) {
                    if (isReferencePamirsFile(superModel)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isSymmetricMappingRelation(ModelField subViewModelField, ModelField modelField, Function<String, ModelDefinition> modelFetcher) {
        return TtypeEnum.isRelationMany(subViewModelField.getTtype()) && TtypeEnum.isRelationOne(modelField.getTtype())

                && (subViewModelField.getModel().equals(modelField.getReferences())
                || Spider.getDefaultExtension(ModelInheritedApi.class)
                .isPropagationSuperModel(subViewModelField.getModel(), modelField.getReferences(), modelFetcher))

                && ListUtils.isEqualList(modelField.getRelationFields(), subViewModelField.getReferenceFields())
                && ListUtils.isEqualList(modelField.getReferenceFields(), subViewModelField.getRelationFields());
    }

    private void makeFieldSubView(RegisterViewContext context, ViewTypeEnum viewType, ViewTypeEnum topViewType,
                                  ModelField subViewField, UIField uiField, int depth) {
        // one2many|many2many关系，后端模型定义字段指定widget = "Select"等非Table的情况
        boolean isNotTableWidget = StringUtils.isNotBlank(uiField.getWidget()) && !uiField.getWidget().equals(WidgetEnum.TABLE.value());
        if (null == subViewField || isNotTableWidget) {
            return;
        }

        if (0 == depth && TtypeEnum.isRelationMany(subViewField.getExactTtype())
                && (ViewTypeEnum.FORM.equals(viewType) || ViewTypeEnum.DETAIL.equals(viewType))) {
            ModelDefinition refModelDefinition = fetchModelDefinition(context.getMeta(), subViewField.getReferences());
            if (null == refModelDefinition) {
                return;
            }
            RegisterViewContext refContext = new RegisterViewContext(context.getMeta(), refModelDefinition, context.getActionMap());
            if (null != refContext.getClazz()) {
                View subView = new View();
                subView.setType(ViewTypeEnum.TABLE);
                UIView refView = makeDefaultUiView(refContext, subView, topViewType, subViewField, ++depth);
                if (ViewTypeEnum.DETAIL.equals(topViewType)) {
                    refView.addProp(new Prop().setName(TablePropConstants.ATTR_OPERATION).setValue(false));
                }
                uiField.setWidget(WidgetEnum.TABLE.value());
                uiField.addWidget(refView);
            }
        }
    }

    private void layoutUiWidgets(Meta meta, UIView uiView, ViewTypeEnum viewType,
                                 String model, List<UIWidget> uiFieldList, int depth) {
        if (ViewTypeEnum.TABLE.equals(viewType)) {
            uiView.addWidget(makeFieldsSlot(ViewSlotNameEnum.FIELDS, uiFieldList));
        } else {
            boolean tabsTable = null == uiView.getTabsTable() || uiView.getTabsTable();
            uiView.setTabsTable(null);

            List<UIWidget> widgets = new ArrayList<>();
            List<UIWidget> defaultTabsWidgets = new ArrayList<>();
            UIPack uiGroup = null;
            UIPack uiTabs = null;
            UIPack uiTab = null;
            for (UIWidget uiWidget : uiFieldList) {
                // one2many|many2many关系，后端模型定义字段指定widget = "Select"等非Table的情况
                boolean isNotTableWidget = StringUtils.isNotBlank(uiWidget.getWidget()) && !uiWidget.getWidget().equals(WidgetEnum.TABLE.value());

                if (tabsTable && uiWidget instanceof UIField) {
                    UIField uiField = (UIField) uiWidget;
                    ModelField modelField = meta.getModelField(model, uiField.getData());
                    if (null == modelField) {
                        continue;
                    }
                    if (!isNotTableWidget && TtypeEnum.isRelationMany(modelField.getExactTtype()) && depth < 2) {
                        UIPack relationTab = new UIPack();
                        relationTab.setWidget(WidgetEnum.TAB.value());
                        relationTab.setTitle(((UIField) uiWidget).getLabel());
                        ((UIField) uiWidget).setLabel(Boolean.FALSE.toString());
                        relationTab.addWidget(uiWidget);
                        defaultTabsWidgets.add(relationTab);
                        continue;
                    }
                }

                boolean newGroup = null != uiWidget.getNewGroup();
                if (newGroup || null == uiGroup) {
                    uiTabs = null;
                    uiTab = null;
                    uiGroup = new UIPack();
                    uiGroup.setWidget(WidgetEnum.GROUP.value());
                    if (newGroup) {
                        uiGroup.setTitle(uiWidget.getNewGroup());
                    } else {
                        uiGroup.setTitle(Optional.ofNullable(uiView.getDefaultGroup()).orElse(GroupConstants.DEFAULT_TITLE));
                    }
                    if (depth > 1) {
                        uiGroup.setBorder(false);
                    }
                    uiGroup.setWidgets(new ArrayList<>());
                    widgets.add(uiGroup);
                }
                if (null == uiWidget.getBreakTab() || uiWidget.getBreakTab()) {
                    uiTabs = null;
                    uiTab = null;
                }
                if (null != uiWidget.getNewTab()) {
                    if (null == uiTabs) {
                        uiTabs = new UIPack();
                        uiTabs.setWidget(WidgetEnum.TABS.value());
                        if (depth > 1) {
                            uiTabs.setBorder(false);
                        }
                        uiTabs.setWidgets(new ArrayList<>());
                    }
                    uiTab = new UIPack();
                    uiTab.setWidget(WidgetEnum.TAB.value());
                    uiTab.setTitle(uiWidget.getNewTab());
                    uiTab.setWidgets(new ArrayList<>());
                    uiTabs.getWidgets().add(uiTab);
                }
                if (null != uiTab) {
                    uiTab.getWidgets().add(uiWidget);
                    uiGroup.getWidgets().add(uiTabs);
                } else {
                    uiGroup.getWidgets().add(uiWidget);
                }

            }
            if (CollectionUtils.isNotEmpty(defaultTabsWidgets)) {
                UIPack relationTabs = new UIPack();
                relationTabs.setWidget(WidgetEnum.TABS.value());
                Prop prop = new Prop();
                prop.setName(LayoutPropNameConstants.PROP_COLS);
                prop.setValue(GridConstants.defaultViewGrid);
                relationTabs.addProp(prop);
                relationTabs.setWidgets(defaultTabsWidgets);
                widgets.add(relationTabs);
            }
            uiView.addWidget(makeFieldsSlot(ViewSlotNameEnum.FIELDS, widgets));
        }
    }

    private UITemplate makeFieldsSlot(ViewSlotNameEnum slotName, List<UIWidget> widgets, Prop... props) {
        UITemplate fields = new UITemplate();
        fields.setSlot(slotName.value());
        if (CollectionUtils.isNotEmpty(widgets)) {
            fields.setWidgets(widgets);
            if (ArrayUtils.isNotEmpty(props)) {
                for (Prop prop : props) {
                    fields.addProp(prop);
                }
            }
        } else {
            fields.setAutoFill(true);
        }
        return fields;
    }

    public static UIField makeUiField(ViewTypeEnum viewType, ModelField modelField, UxWidget uxWidget) {
        UIField uiField = new UIField();
        uiField.setData(modelField.getField())
                .setLabel(Optional.ofNullable(uxWidget).map(UxWidget::label).filter(StringUtils::isNotBlank).orElse(modelField.getDisplayName()))
                .setWidget(Optional.ofNullable(uxWidget).map(UxWidget::widget).filter(StringUtils::isNotBlank).orElseGet(() -> fetchDefaultWidget(viewType, modelField)))
                .setPropList(PropUtils.convertPropListFromAnnotation(Optional.ofNullable(uxWidget).map(UxWidget::config).orElse(null)))
                .setContext(PropUtils.convertPropMapFromAnnotation(Optional.ofNullable(uxWidget).map(UxWidget::context).orElse(null)));
        uiField.setMapping(PropUtils.convertPropMapFromAnnotation(Optional.ofNullable(uxWidget).map(UxWidget::context).orElse(null)))
                .setHint(Optional.ofNullable(uxWidget).map(UxWidget::hint).filter(StringUtils::isNotBlank).orElse(null))
                .setPlaceholder(Optional.ofNullable(uxWidget).map(UxWidget::placeholder).filter(StringUtils::isNotBlank).orElse(null))
                .setReadonly(Optional.ofNullable(uxWidget).map(UxWidget::readonly).filter(StringUtils::isNotBlank).orElse(null))
                .setDisabled(Optional.ofNullable(uxWidget).map(UxWidget::disable).filter(StringUtils::isNotBlank).orElse(null))
                .setRequired(Optional.ofNullable(uxWidget).map(UxWidget::required).filter(StringUtils::isNotBlank).orElse(null))
                .setInvisible(Optional.ofNullable(uxWidget).map(UxWidget::invisible).filter(StringUtils::isNotBlank).orElse(null))

                .setNewGroup(Optional.ofNullable(uxWidget).map(UxWidget::group).filter(v -> !CharacterConstants.SEPARATOR_HYPHEN.equals(v))
                        .orElse(null))
                .setNewTab(Optional.ofNullable(uxWidget).map(UxWidget::tab).filter(v -> !CharacterConstants.SEPARATOR_HYPHEN.equals(v))
                        .orElse(null))
                .setBreakTab(Optional.ofNullable(uxWidget).map(UxWidget::breakTab).orElse(false))
                .setPriority(Optional.ofNullable(uxWidget).map(UxWidget::priority).filter(v -> v != MetaDefaultConstants.FAKE_PRIORITY_VALUE_INT)
                        .orElse(Optional.ofNullable(modelField.getPriority()).map(Long::intValue).orElse(MetaDefaultConstants.PRIORITY_VALUE_INT)))
                .setOffset(Optional.ofNullable(uxWidget).map(UxWidget::offset).filter(v -> v != 0).orElse(null));

        uiField.setQueryMode(Optional.ofNullable(uxWidget).map(UxWidget::queryMode).filter(v -> !QueryModeEnum.DOMAIN.equals(v)).orElse(null));
        if (TtypeEnum.isRelationType(modelField.getTtype())) {
            if (TtypeEnum.O2O.equals(modelField.getTtype()) || TtypeEnum.M2O.equals(modelField.getTtype())) {
                uiField.setSpan(Optional.ofNullable(uxWidget).map(UxWidget::span).orElse(1));
            } else {
                uiField.setSpan(Optional.ofNullable(uxWidget).map(UxWidget::span).orElse(null));
            }
        } else {
            if (TtypeEnum.HTML.equals(modelField.getTtype()) || TtypeEnum.TEXT.equals(modelField.getTtype())) {
                uiField.setSpan(Optional.ofNullable(uxWidget).map(UxWidget::span).orElse(null));
            } else {
                uiField.setSpan(Optional.ofNullable(uxWidget).map(UxWidget::span).orElse(1));
            }
        }

        // 处理抽象基类字段视图信息
        compileAbstractFields(viewType, uiField);

        return uiField;
    }

    private static String fetchDefaultWidget(ViewTypeEnum viewType, ModelField modelField) {
        String references = modelField.getReferences();
        if (StringUtils.isBlank(references)) {
            return null;
        }
        String businessModel = BusinessModelHelper.isInheritedBusinessModel(references);
        if (StringUtils.isBlank(businessModel)) {
            return null;
        }
        TtypeEnum ttype = modelField.getTtype();
        if (TtypeEnum.M2O.equals(ttype)) {
            switch (businessModel) {
                case BusinessModelConstants.RESOURCE_ADDRESS:
                    return BusinessModelConstants.RESOURCE_ADDRESS_WIDGET;
                case BusinessModelConstants.COMPANY:
                    return BusinessModelConstants.COMPANY_WIDGET;
                case BusinessModelConstants.DEPARTMENT:
                    return BusinessModelConstants.DEPARTMENT_WIDGET;
                case BusinessModelConstants.EMPLOYEE:
                    return BusinessModelConstants.EMPLOYEE_WIDGET;
                case BusinessModelConstants.ROLE:
                    return BusinessModelConstants.ROLE_WIDGET;
            }
        }
        return null;
    }

    /**
     * 处理抽象基类字段视图信息
     *
     * @param viewType 视图类型
     * @param uiField  视图字段
     */
    private static void compileAbstractFields(ViewTypeEnum viewType, UIField uiField) {
        String readonly = null;
        String invisible = null;
        if (FieldConstants.CREATE_DATE.equals(uiField.getData()) || FieldConstants.WRITE_DATE.equals(uiField.getData())) {
            readonly = Boolean.TRUE.toString();
            if (ViewTypeEnum.FORM.equals(viewType)) {
                invisible = Boolean.TRUE.toString();
            }
        } else if (FieldConstants.ID.equals(uiField.getData())) {
            readonly = Boolean.TRUE.toString();
            invisible = Boolean.TRUE.toString();
        }

        uiField.setReadonly(Optional.ofNullable(uiField.getReadonly()).orElse(readonly))
                .setInvisible(Optional.ofNullable(uiField.getInvisible()).orElse(invisible));
    }

    private ModelDefinition fetchModelDefinition(Meta meta, String model) {
        ModelDefinition modelDefinition = meta.getModel(model);
        if (null == modelDefinition) {
            log.error("模型不存在, model:{}", model);
            return null;
        }
        return modelDefinition;
    }

}
