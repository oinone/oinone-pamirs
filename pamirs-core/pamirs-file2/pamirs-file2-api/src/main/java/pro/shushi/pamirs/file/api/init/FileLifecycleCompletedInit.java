package pro.shushi.pamirs.file.api.init;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.LifecycleCompletedInit;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.core.common.LifecycleExecutorHelper;
import pro.shushi.pamirs.core.common.cache.MemoryIterableSearchCache;
import pro.shushi.pamirs.core.common.xstream.TreeNodeXStream;
import pro.shushi.pamirs.core.common.xstream.XMLNodeContent;
import pro.shushi.pamirs.file.api.FileModule;
import pro.shushi.pamirs.file.api.builder.BlockDefinitionBuilder;
import pro.shushi.pamirs.file.api.builder.HeaderDefinitionBuilder;
import pro.shushi.pamirs.file.api.builder.WorkbookDefinitionBuilder;
import pro.shushi.pamirs.file.api.config.FileConstant;
import pro.shushi.pamirs.file.api.config.FileProperties;
import pro.shushi.pamirs.file.api.enmu.*;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.service.ExcelFileService;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.framework.common.config.AsyncTaskExecutorConfiguration;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.gateways.util.BooleanHelper;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.*;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order
public class FileLifecycleCompletedInit implements LifecycleCompletedInit {

    private static final String CONFIG_KEY = "config";

    private static final String CONFIG_EXCLUDED_ACTIONS_KEY = "excluded-actions";

    private static final String ACTION_KEY = "action";

    private static final String VIEW_KEY = "view";

    private static final String ACTION_REFS_KEY = "refs";

    private static final String FIELD_KEY = "field";

    private static final String FIELDS_KEY = "fields";

    private static final String FORM_KEY = "form";

    private static final String TEMPLATE_KEY = "template";

    private static final String SLOT_KEY = "slot";

    private static final String PACK_KEY = "pack";

    private static final String TABLE_KEY = "table";

    private static final String DATA_KEY = "data";

    private static final String TYPE_KEY = "type";

    private static final String WIDGET_KEY = "widget";

    private static final String NAME_KEY = "name";

    private static final String FIELD_LABEL_KEY = "label";

    private static final String FIELD_INVISIBLE_KEY = "invisible";

    private static final String EXPORT_DIALOG_KEY = "$$internal_GotoListExportDialog";

    private static final String EXPORT_DIALOG_KEY_V3 = "internal_GotoListExportDialog";

    private static final String DEFAULT_RELATION_LABEL_SPLIT = "-";

    @Autowired
    private ExcelFileService excelFileService;

    @Autowired
    private FileProperties fileProperties;

    @Autowired(required = false)
    @Qualifier(AsyncTaskExecutorConfiguration.FIXED_THREAD_POOL_EXECUTOR)
    private Executor globalFixedThreadPoolExecutor;

    @Override
    public void process(AppLifecycleCommand command, List<ModuleDefinition> installModules, List<ModuleDefinition> upgradeModules, List<ModuleDefinition> reloadModules) {
        log.info("Automatically create import/export system templates: {}", fileProperties.getAutoCreateTemplate());
        if (fileProperties.getAutoCreateTemplate()) {
            InitializationUtil.lifecycleCompletedInit(installModules, upgradeModules, Collections.emptyList(), (lifecycle, module) -> LifecycleExecutorHelper.execute(globalFixedThreadPoolExecutor, this::initDefaultTemplateByTableView), FileModule.MODULE_MODULE);
        }
    }

    private void initDefaultTemplateByTableView() {
        List<ExcelWorkbookDefinition> existImportExportWorkbookDefinitionList = getExistWorkbookDefinitionList(ExcelTemplateTypeEnum.IMPORT_EXPORT);
        // 默认导出模板(Table)
        List<ExcelWorkbookDefinition> existExportWorkbookDefinitionList = getExistWorkbookDefinitionList(ExcelTemplateTypeEnum.EXPORT);
        existExportWorkbookDefinitionList.addAll(existImportExportWorkbookDefinitionList);
        List<ExcelWorkbookDefinition> exportWorkbookDefinitionList = getEffectiveList(existExportWorkbookDefinitionList, ViewTypeEnum.TABLE);

        // 默认导入模板(Form)
        List<ExcelWorkbookDefinition> existImportWorkbookDefinitionList = getExistWorkbookDefinitionList(ExcelTemplateTypeEnum.IMPORT);
        existImportWorkbookDefinitionList.addAll(existImportExportWorkbookDefinitionList);
        List<ExcelWorkbookDefinition> importWorkbookDefinitionList = getEffectiveList(existImportWorkbookDefinitionList, ViewTypeEnum.FORM);

        log.info("Initialization system templates. import template: {}, export template: {}", exportWorkbookDefinitionList.size(), importWorkbookDefinitionList.size());
        refreshWorkbookDefinition(exportWorkbookDefinitionList);
        refreshWorkbookDefinition(importWorkbookDefinitionList);
    }

    private List<ExcelWorkbookDefinition> getExistWorkbookDefinitionList(ExcelTemplateTypeEnum type) {
        List<ExcelWorkbookDefinition> workbookDefinitions = Models.data().queryListByWrapper(Pops.<ExcelWorkbookDefinition>lambdaQuery().from(ExcelWorkbookDefinition.MODEL_MODEL).eq(ExcelWorkbookDefinition::getType, type).setBatchSize(200));
        if (CollectionUtils.isNotEmpty(workbookDefinitions)) {
            workbookDefinitions = Models.data().listFieldQuery(workbookDefinitions, ExcelWorkbookDefinition::getLocations);
        }
        return workbookDefinitions;
    }

    private List<ExcelWorkbookDefinition> getEffectiveList(List<ExcelWorkbookDefinition> existWorkbookDefinitionList, ViewTypeEnum viewType) {
        List<View> allViewList = Models.data().queryListByWrapper(Pops.<View>lambdaQuery().from(View.MODEL_MODEL).select(View::getModel, View::getName, View::getTitle, View::getTemplate).eq(View::getType, viewType).eq(View::getActive, ActiveEnum.ACTIVE.value()).orderByAsc(View::getPriority).setBatchSize(200));
        List<View> distinctViewList = allViewList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(View::getModel))), ArrayList::new));
        TreeNodeXStream xs = new TreeNodeXStream();
        Set<String> repeatSet = new HashSet<>();
        Map<ExcelTemplateSourceEnum, List<ExcelWorkbookDefinition>> resultMap = new HashMap<>(4);
        Map<String, ExcelWorkbookDefinition> existWorkbookDefinitionMap = new LinkedHashMap<>(existWorkbookDefinitionList.size());
        for (ExcelWorkbookDefinition item : existWorkbookDefinitionList) {
            resultMap.computeIfAbsent(item.getTemplateSource(), k -> new ArrayList<>()).add(item);
            existWorkbookDefinitionMap.put(keyGenerator(item), item);
            if (!ExcelTemplateSourceEnum.SYSTEM.equals(item.getTemplateSource())) {
                repeatSet.add(item.getModel());
            }
        }
        List<ExcelWorkbookDefinition> createOrUpdateList = new ArrayList<>();
        MemoryIterableSearchCache<String, ExcelWorkbookDefinition> cache = new MemoryIterableSearchCache<>(resultMap.get(ExcelTemplateSourceEnum.SYSTEM), this::keyGenerator);
        for (View view : distinctViewList) {
            String model = view.getModel();
            if (!repeatSet.contains(model)) {
                ExcelWorkbookDefinition workbookDefinition;
                try {
                    workbookDefinition = createOrUpdateExcelTemplate(xs, view);
                } catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error("Failed to generate Excel template model: {}", model, e);
                    }
                    continue;
                }
                if (workbookDefinition != null) {
                    cache.compute(keyGenerator(workbookDefinition), (k, v) -> v);
                    createOrUpdateList.add(workbookDefinition);
                }
            }
        }
        cache.fill();
        List<ExcelWorkbookDefinition> deleteList = new ArrayList<>(cache.getNotComputedCache().values());
        if (!deleteList.isEmpty()) {
            Models.data().deleteByPks(deleteList);
            for (ExcelWorkbookDefinition item : deleteList) {
                String originKey = keyGenerator(item);
                existWorkbookDefinitionMap.remove(originKey);
            }
        }
        if (!createOrUpdateList.isEmpty()) {
            Models.data().createOrUpdateBatch(createOrUpdateList);
            for (ExcelWorkbookDefinition item : createOrUpdateList) {
                existWorkbookDefinitionMap.put(keyGenerator(item), item);
            }
        }
        return new ArrayList<>(existWorkbookDefinitionMap.values());
    }

    private void refreshWorkbookDefinition(List<ExcelWorkbookDefinition> workbookDefinitionList) {
        try {
            excelFileService.refreshDefinitionContextBatch(workbookDefinitionList);
        } catch (Exception e) {
            log.error("Refresh excel workbook definition error.", e);
        }
    }

    private String keyGenerator(ExcelWorkbookDefinition item) {
        return item.getModel() + CharacterConstants.SEPARATOR_OCTOTHORPE + item.getName();
    }

    private ExcelWorkbookDefinition createOrUpdateExcelTemplate(TreeNodeXStream xs, View view) {
        String xmlTemplate = view.getTemplate();
        if (StringUtils.isBlank(xmlTemplate)) {
            return null;
        }
        String model = view.getModel();
        RequestContext requestContext = PamirsSession.getContext();
        if (requestContext == null) {
            return null;
        }
        ModelConfig modelConfig = requestContext.getModelConfig(model);
        if (modelConfig == null) {
            return null;
        }
        ModelTypeEnum modelType = modelConfig.getType();
        if (!ModelTypeEnum.STORE.equals(modelType) && !ModelTypeEnum.PROXY.equals(modelType)) {
            return null;
        }
        try {
            TreeNode<XMLNodeContent> viewRoot = xs.fromXML(xmlTemplate);
            XMLNodeContent rootContent = viewRoot.getValue();
            String type = rootContent.getAttribute(TYPE_KEY);
            if (StringUtils.isBlank(type)) {
                type = rootContent.getAttribute(WIDGET_KEY);
            }
            String viewType = type.trim().toUpperCase();
            if (StringUtils.isBlank(type) || (!ViewTypeEnum.TABLE.name().equals(viewType) && !ViewTypeEnum.FORM.name().equals(viewType))) {
                return null;
            }
            String templateDisplayName = generatorDefaultTemplateDisplayName(view, modelConfig, viewType);
            String sheetName = generatorDefaultSheetName(view, modelConfig, viewType);
            WorkbookDefinitionBuilder workbookDefinitionBuilder = WorkbookDefinitionBuilder.newInstance(model, FileConstant.DEFAULT_TEMPLATE_NAME + "_" + view.getName());
            workbookDefinitionBuilder.setBindingViewName(view.getName());
            BlockDefinitionBuilder blockDefinitionBuilder = workbookDefinitionBuilder.createSheet().setName(sheetName).createBlock(model, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, 0, 1, 0, 12).setPresetNumber(10);
            HeaderDefinitionBuilder configHeaderDefinitionBuilder = blockDefinitionBuilder.createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(true);
            HeaderDefinitionBuilder headerDefinitionBuilder = blockDefinitionBuilder.createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(true)));
            if (ViewTypeEnum.TABLE.name().equals(viewType)) {
                if (!createExportCell(configHeaderDefinitionBuilder, headerDefinitionBuilder, viewRoot, requestContext, model, view.getName())) {
                    return null;
                }
                log.info("Successfully generated default export template [Model {}] [ViewName {}] [ViewType {}]", view.getModel(), view.getName(), view.getType());
                blockDefinitionBuilder.modifyDesignRange(0, 1, 0, configHeaderDefinitionBuilder.cellSize() - 1);
                return workbookDefinitionBuilder.setType(ExcelTemplateTypeEnum.EXPORT).setDisplayName(templateDisplayName).build().setTemplateSource(ExcelTemplateSourceEnum.SYSTEM);
            } else {
                if (!createImportCell(configHeaderDefinitionBuilder, headerDefinitionBuilder, viewRoot, requestContext, model, view.getName())) {
                    return null;
                }
                log.info("Successfully generated default import template [Model {}] [ViewName {}] [ViewType {}]", view.getModel(), view.getName(), view.getType());
                blockDefinitionBuilder.modifyDesignRange(0, 1, 0, configHeaderDefinitionBuilder.cellSize() - 1);
                return workbookDefinitionBuilder.setType(ExcelTemplateTypeEnum.IMPORT).setDisplayName(templateDisplayName).build().setTemplateSource(ExcelTemplateSourceEnum.SYSTEM);
            }
        } catch (Exception e) {
            log.error("Failed to generate default template [Model {}] [ViewName {}] [ViewType {}]", view.getModel(), view.getName(), view.getType(), e);
        }
        return null;
    }

    private String generatorDefaultTemplateDisplayName(View view, ModelConfig modelConfig, String viewType) {
        if (ViewTypeEnum.TABLE.name().equals(viewType.trim().toUpperCase())) {
            return generatorDefaultSheetName(view, modelConfig, viewType) + I18nUtils.getMessage("file.template.default.suffix.export");
        } else {
            return generatorDefaultSheetName(view, modelConfig, viewType) + I18nUtils.getMessage("file.template.default.suffix.import");
        }
    }

    private String generatorDefaultSheetName(View view, ModelConfig modelConfig, String viewType) {
        String name = view.getTitle();
        if (StringUtils.isBlank(name)) {
            name = modelConfig.getDisplayName();
            if (StringUtils.isBlank(name)) {
                if (ViewTypeEnum.TABLE.name().equals(viewType.trim().toUpperCase())) {
                    name = I18nUtils.getMessage("file.template.default.export");
                } else {
                    name = I18nUtils.getMessage("file.template.default.import");
                }
            }
        }
        return name;
    }

    private boolean createExportCell(HeaderDefinitionBuilder configHeaderDefinitionBuilder,
                                     HeaderDefinitionBuilder headerDefinitionBuilder,
                                     TreeNode<XMLNodeContent> viewRoot,
                                     RequestContext requestContext,
                                     String model, String viewName) {
        boolean isCreate = false;
        for (TreeNode<XMLNodeContent> child : viewRoot.getChildren()) {
            XMLNodeContent childValue = child.getValue();
            String key = child.getKey();
            if (FIELD_KEY.equals(key)) {
                boolean invisible = childValue.getBooleanAttribute(FIELD_INVISIBLE_KEY);
                if (invisible) {
                    continue;
                }
                String field = childValue.getAttribute(DATA_KEY);
                if (StringUtils.isBlank(field)) {
                    continue;
                }
                String label = childValue.getAttribute(FIELD_LABEL_KEY);
                ModelFieldConfig modelFieldConfig = requestContext.getModelField(model, field);
                if (modelFieldConfig == null) {
                    continue;
                }
                if (Boolean.TRUE.equals(modelFieldConfig.getInvisible())) {
                    continue;
                }
                if (StringUtils.isBlank(label) || !BooleanHelper.isFalseWithoutException(label)) {
                    label = modelFieldConfig.getDisplayName();
                    if (StringUtils.isBlank(label)) {
                        label = field;
                    }
                }
                label = I18nUtils.translateFieldInViewTemplate(model, viewName, field, "label", label);
                if (createCell(configHeaderDefinitionBuilder, headerDefinitionBuilder, requestContext, modelFieldConfig, viewName, field, label, true)) {
                    isCreate = true;
                }
            } else if (CONFIG_KEY.equals(key)) {
                String excludedActions = childValue.getAttribute(CONFIG_EXCLUDED_ACTIONS_KEY);
                if (excludedActions != null) {
                    if (excludedActions.contains(EXPORT_DIALOG_KEY)) {
                        return false;
                    }
                }
            } else if (TEMPLATE_KEY.equals(key)) {
                String slot = childValue.getAttribute(SLOT_KEY);
                if (StringUtils.isBlank(slot)) {
                    continue;
                }
                slot = slot.trim().toLowerCase();
                if (TABLE_KEY.equals(slot) || FIELDS_KEY.equals(slot)) {
                    isCreate = createExportCell(configHeaderDefinitionBuilder, headerDefinitionBuilder, child, requestContext, model, viewName);
                }
            }
        }
        return isCreate;
    }

    private boolean createImportCell(HeaderDefinitionBuilder configHeaderDefinitionBuilder,
                                     HeaderDefinitionBuilder headerDefinitionBuilder,
                                     TreeNode<XMLNodeContent> viewRoot,
                                     RequestContext requestContext,
                                     String model, String viewName) {
        boolean isCreate = false;
        for (TreeNode<XMLNodeContent> child : viewRoot.getChildren()) {
            XMLNodeContent childValue = child.getValue();
            String key = child.getKey();
            if (FIELD_KEY.equals(key)) {
                boolean invisible = childValue.getBooleanAttribute(FIELD_INVISIBLE_KEY);
                if (invisible) {
                    continue;
                }
                String field = childValue.getAttribute(DATA_KEY);
                if (StringUtils.isBlank(field) || FieldConstants.CREATE_UID.equalsIgnoreCase(field) || FieldConstants.WRITE_UID.equalsIgnoreCase(field)) {
                    continue;
                }
                String label = childValue.getAttribute(FIELD_LABEL_KEY);
                ModelFieldConfig modelFieldConfig = requestContext.getModelField(model, field);
                if (modelFieldConfig == null) {
                    continue;
                }
                if (Boolean.TRUE.equals(modelFieldConfig.getInvisible())) {
                    continue;
                }
                if (StringUtils.isBlank(label) || !BooleanHelper.isFalseWithoutException(label)) {
                    label = modelFieldConfig.getDisplayName();
                    if (StringUtils.isBlank(label)) {
                        label = field;
                    }
                }
                label = I18nUtils.translateFieldInViewTemplate(model, viewName, field, "label", label);
                if (createCell(configHeaderDefinitionBuilder, headerDefinitionBuilder, requestContext, modelFieldConfig, viewName, field, label, false)) {
                    isCreate = true;
                }
            } else if (CONFIG_KEY.equals(key)) {
                String excludedActions = childValue.getAttribute(CONFIG_EXCLUDED_ACTIONS_KEY);
                if (excludedActions != null) {
                    if (excludedActions.contains(EXPORT_DIALOG_KEY)) {
                        return false;
                    }
                }
            } else if (PACK_KEY.equals(key)) {
                isCreate = createImportCell(configHeaderDefinitionBuilder, headerDefinitionBuilder, child, requestContext, model, viewName);
            } else if (TEMPLATE_KEY.equals(key)) {
                String slot = childValue.getAttribute(SLOT_KEY);
                if (StringUtils.isBlank(slot)) {
                    continue;
                }
                slot = slot.trim().toLowerCase();
                if (TABLE_KEY.equals(slot) || FIELDS_KEY.equals(slot) || FORM_KEY.equals(slot)) {
                    isCreate = createImportCell(configHeaderDefinitionBuilder, headerDefinitionBuilder, child, requestContext, model, viewName);
                }
            }
        }
        return isCreate;
    }

    private boolean createCell(HeaderDefinitionBuilder configHeaderDefinitionBuilder,
                               HeaderDefinitionBuilder headerDefinitionBuilder,
                               RequestContext requestContext,
                               ModelFieldConfig modelFieldConfig,
                               String viewName,
                               String field,
                               String label,
                               Boolean isExport) {
        String ttype = modelFieldConfig.getTtype();
        if (TtypeEnum.RELATED.value().equals(ttype)) {
            if (Boolean.TRUE.equals(isExport) || Boolean.TRUE.equals(modelFieldConfig.getStore())) {
                ttype = modelFieldConfig.getRelatedTtype();
            } else {
                return false;
            }
        }
        boolean isMulti = Boolean.TRUE.equals(modelFieldConfig.getMulti());
        ExcelValueTypeEnum valueType = ExcelValueTypeEnum.STRING;
        String format = modelFieldConfig.getFormat();
        boolean isSampleField = true;
        if (TtypeEnum.STRING.value().equals(ttype)) {
            if (isMulti) {
                format = ExcelHelper.generatorMultiValueFormatExpression();
                valueType = ExcelValueTypeEnum.OBJECT;
            }
        } else if (TtypeEnum.ENUM.value().equals(ttype)) {
            Map<String, String> enumerationMapping = new HashMap<>();
            DataDictionary dictionary = requestContext.getDictionary(modelFieldConfig.getDictionary());
            for (DataDictionaryItem dictionaryItem : dictionary.getOptions()) {
                enumerationMapping.put(dictionaryItem.getValue(), dictionaryItem.getDisplayName());
            }
            format = JSON.toJSONString(enumerationMapping);
            valueType = ExcelValueTypeEnum.ENUMERATION;
        } else if (TtypeEnum.DATE.value().equals(ttype)) {
            if (StringUtils.isBlank(format)) {
                format = DateFormatEnum.DATE.value();
            }
            valueType = ExcelValueTypeEnum.DATETIME;
        } else if (TtypeEnum.TIME.value().equals(ttype)) {
            if (StringUtils.isBlank(format)) {
                format = DateFormatEnum.TIME.value();
            }
            valueType = ExcelValueTypeEnum.DATETIME;
        } else if (TtypeEnum.DATETIME.value().equals(ttype)) {
            if (StringUtils.isBlank(format)) {
                format = DateFormatEnum.DATETIME.value();
            }
            valueType = ExcelValueTypeEnum.DATETIME;
        } else if (TtypeEnum.YEAR.value().equals(ttype)) {
            if (StringUtils.isBlank(format)) {
                format = DateFormatEnum.YEAR.value();
            }
            valueType = ExcelValueTypeEnum.DATETIME;
        } else if (TtypeEnum.INTEGER.value().equals(ttype)) {
            if (isMulti) {
                format = ExcelHelper.generatorMultiValueFormatExpression();
                valueType = ExcelValueTypeEnum.OBJECT;
            } else {
                format = ExcelValueTypeEnum.INTEGER.defaultFormat();
                valueType = ExcelValueTypeEnum.INTEGER;
            }
        } else if (TtypeEnum.BOOLEAN.value().equals(ttype)) {
            format = ExcelValueTypeEnum.BOOLEAN.defaultFormat();
            valueType = ExcelValueTypeEnum.BOOLEAN;
        } else if (TtypeEnum.MONEY.value().equals(ttype) || TtypeEnum.FLOAT.value().equals(ttype)) {
            if (isMulti) {
                format = ExcelHelper.generatorMultiValueFormatExpression();
                valueType = ExcelValueTypeEnum.OBJECT;
            } else {
                format = ExcelHelper.getNumberFormat(modelFieldConfig.getDecimal());
                valueType = ExcelValueTypeEnum.NUMBER;
            }
        } else if (TtypeEnum.M2O.value().equals(ttype) || TtypeEnum.O2O.value().equals(ttype)) {
            relationFieldProcess(configHeaderDefinitionBuilder, headerDefinitionBuilder, requestContext, modelFieldConfig, viewName, field, label, false, isExport);
            isSampleField = false;
        } else if (TtypeEnum.O2M.value().equals(ttype) || TtypeEnum.M2M.value().equals(ttype)) {
            relationFieldProcess(configHeaderDefinitionBuilder, headerDefinitionBuilder, requestContext, modelFieldConfig, viewName, field, label, true, isExport);
            isSampleField = false;
        }
        if (isSampleField) {
            configHeaderDefinitionBuilder.createCell().setField(field).setType(valueType).setFormat(format);
            headerDefinitionBuilder.createCell().setValue(label);
        }
        return true;
    }

    private void relationFieldProcess(HeaderDefinitionBuilder configHeaderDefinitionBuilder,
                                      HeaderDefinitionBuilder headerDefinitionBuilder,
                                      RequestContext requestContext,
                                      ModelFieldConfig modelFieldConfig,
                                      String viewName,
                                      String field,
                                      String label,
                                      Boolean isMulti,
                                      Boolean isExport) {
        if (isExport) {
            exportRelationFieldProcess(configHeaderDefinitionBuilder, headerDefinitionBuilder, requestContext, modelFieldConfig, field, label, isMulti);
        } else {
            importRelationFieldProcess(configHeaderDefinitionBuilder, headerDefinitionBuilder, requestContext, modelFieldConfig, viewName, field, label, isMulti);
        }
    }

    private void exportRelationFieldProcess(HeaderDefinitionBuilder configHeaderDefinitionBuilder,
                                            HeaderDefinitionBuilder headerDefinitionBuilder,
                                            RequestContext requestContext,
                                            ModelFieldConfig modelFieldConfig,
                                            String field,
                                            String label,
                                            Boolean isMulti) {
        String referenceModel = modelFieldConfig.getReferences();
        ModelConfig referenceModelConfig = getReferenceModelConfig(requestContext, modelFieldConfig);
        if (referenceModelConfig == null) {
            return;
        }
        List<String> optionLabels = Optional.ofNullable(referenceModelConfig.getModelDefinition()).map(ModelDefinition::getLabelFields).filter(CollectionUtils::isNotEmpty).orElse(null);
        if (CollectionUtils.isEmpty(optionLabels)) {
            ModelFieldConfig labelFieldConfig = requestContext.getModelField(referenceModel, FieldConstants.NAME);
            if (labelFieldConfig == null) {
                labelFieldConfig = requestContext.getModelField(referenceModel, FieldConstants.CODE);
            }
            if (labelFieldConfig == null) {
                labelFieldConfig = requestContext.getModelField(referenceModel, FieldConstants.ID);
            }
            if (labelFieldConfig == null) {
                return;
            }
            optionLabels = Collections.singletonList(labelFieldConfig.getField());
        }
        String optionLabel = optionLabels.get(0);
        String formatExpression;
        if (isMulti) {
            formatExpression = ExcelHelper.generatorMultiObjectFormatExpression(referenceModel, optionLabel);
        } else {
            formatExpression = ExcelHelper.generatorSingleObjectFormatExpression(optionLabel);
        }
        configHeaderDefinitionBuilder.createCell().setField(field).setType(ExcelValueTypeEnum.OBJECT).setFormat(formatExpression);
        headerDefinitionBuilder.createCell().setValue(label);
    }

    /**
     * 导入关联关系处理仅处理包含唯一键或主键的多对一字段
     */
    private void importRelationFieldProcess(HeaderDefinitionBuilder configHeaderDefinitionBuilder,
                                            HeaderDefinitionBuilder headerDefinitionBuilder,
                                            RequestContext requestContext,
                                            ModelFieldConfig modelFieldConfig,
                                            String viewName,
                                            String field,
                                            String label,
                                            Boolean isMulti) {
        if (isMulti) {
            // 过滤所有 o2m/m2m 类型字段
            return;
        }
        String referenceModel = modelFieldConfig.getReferences();
        ModelConfig referenceModelConfig = getReferenceModelConfig(requestContext, modelFieldConfig);
        if (referenceModelConfig == null) {
            return;
        }
        //此处仅处理指定唯一索引的关联对象
        List<String> uniqueList = referenceModelConfig.getUniques();
        if (CollectionUtils.isEmpty(uniqueList)) {
            return;
        }
        List<String> labelFields = Optional.ofNullable(referenceModelConfig.getModelDefinition()).map(ModelDefinition::getLabelFields).filter(CollectionUtils::isNotEmpty).orElse(null);
        if (CollectionUtils.isEmpty(labelFields)) {
            return;
        }
        String firstUnique = uniqueList.get(0);
        String[] uniques = firstUnique.split(CharacterConstants.SEPARATOR_COMMA);
        Map<String, String> referenceFieldMap = new LinkedHashMap<>();
        Map<String, ModelFieldConfig> referenceFieldConfigMap = new LinkedHashMap<>();
        List<String> relationFields = new ArrayList<>(Arrays.asList(uniques));
        relationFields.add(labelFields.get(0));
        for (String relationField : relationFields) {
            relationField = relationField.trim();
            ModelFieldConfig referenceModelFieldConfig = requestContext.getModelField(referenceModel, relationField);
            if (referenceModelFieldConfig == null) {
                return;
            }
            String referenceTtype = referenceModelFieldConfig.getTtype();
            if (TtypeEnum.isRelationType(referenceTtype)) {
                return;
            }
            String referenceLabel = referenceModelFieldConfig.getDisplayName();
            if (StringUtils.isBlank(referenceLabel)) {
                referenceLabel = relationField;
            }
            String newRelationField;
            if (isMulti) {
                newRelationField = field + FileConstant.LIST_FLAG_CHARACTER + FileConstant.POINT_CHARACTER + relationField;
            } else {
                newRelationField = field + FileConstant.POINT_CHARACTER + relationField;
            }
            label = I18nUtils.translateFieldInViewTemplate(modelFieldConfig.getModel(), viewName, field, "label", label);
            referenceFieldMap.put(newRelationField, I18nUtils.translateFieldInViewTemplate(modelFieldConfig.getModel(), viewName, newRelationField, "label", label + DEFAULT_RELATION_LABEL_SPLIT + referenceLabel));
            referenceFieldConfigMap.put(newRelationField, referenceModelFieldConfig);
        }
        for (Map.Entry<String, String> entry : referenceFieldMap.entrySet()) {
            String key = entry.getKey();
            createCell(configHeaderDefinitionBuilder, headerDefinitionBuilder, requestContext, referenceFieldConfigMap.get(key), viewName, key, entry.getValue(), null);
        }
    }

    private ModelConfig getReferenceModelConfig(RequestContext requestContext, ModelFieldConfig modelFieldConfig) {
        String referenceModel = modelFieldConfig.getReferences();
        ModelConfig referenceModelConfig = requestContext.getModelConfig(referenceModel);
        if (referenceModelConfig == null) {
            log.error("Invalid reference model config. model: {}, field: {}, referenceModel: {}",
                    modelFieldConfig.getModel(), modelFieldConfig.getField(), referenceModel);
            return null;
        }
        return referenceModelConfig;
    }
}

