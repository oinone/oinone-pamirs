package pro.shushi.pamirs.file.api.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.enmu.FileTypeEnum;
import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.boot.web.spi.api.TranslateService;
import pro.shushi.pamirs.boot.web.spi.holder.TranslateServiceHolder;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.file.api.config.ExcelConstant;
import pro.shushi.pamirs.file.api.config.FileProperties;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.enmu.*;
import pro.shushi.pamirs.file.api.model.ExcelCellDefinition;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.pmodel.ExcelModelField;
import pro.shushi.pamirs.file.api.service.ExcelFileService;
import pro.shushi.pamirs.file.api.service.ExcelWorkbookDefinitionService;
import pro.shushi.pamirs.file.api.util.ExcelFixedHeadHelper;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.file.api.util.ExcelWorkbookDefinitionUtil;
import pro.shushi.pamirs.framework.connectors.cdn.client.FileClient;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFileForm;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.*;

/**
 * @author Adamancy Zhang
 * @date 2020-11-10 12:25
 */
@Slf4j
public abstract class AbstractExcelExportTaskAction<T extends ExcelExportTask> {

    // @see pro.shushi.pamirs.framework.gateways.hook.RsqDecodeHook.RSQL_ENCODE_PREFIX
    private static final String RSQL_ENCODE_PREFIX = "base64:";

    protected ExcelWorkbookDefinitionService excelWorkbookDefinitionService;

    protected ExcelFileService excelFileService;

    protected FileProperties fileProperties;

    public AbstractExcelExportTaskAction() {
        this.excelWorkbookDefinitionService = BeanDefinitionUtils.getBean(ExcelWorkbookDefinitionService.class);
        this.excelFileService = BeanDefinitionUtils.getBean(ExcelFileService.class);
        this.fileProperties = BeanDefinitionUtils.getBean(FileProperties.class);
    }

    @Deprecated
    public AbstractExcelExportTaskAction(ExcelFileService excelFileService) {
        this.excelFileService = excelFileService;
        this.excelWorkbookDefinitionService = BeanDefinitionUtils.getBean(ExcelWorkbookDefinitionService.class);
        this.fileProperties = BeanDefinitionUtils.getBean(FileProperties.class);
    }

    /**
     * 导出执行
     *
     * @param exportTask 导出任务
     * @param context    EXCEL定义上下文
     */
    protected abstract void doExport(T exportTask, ExcelDefinitionContext context);

    public T createExportTask(T data) {
        ExcelExportMethodEnum exportMethod = data.getExportMethod();
        if (exportMethod == null) {
            exportMethod = ExcelExportMethodEnum.TEMPLATE;
        }
        T exportTask;
        switch (exportMethod) {
            case TEMPLATE:
                exportTask = createExportTaskByTemplate(data);
                break;
//            case SELECT_TEMPLATE_FIELD:
//                exportTask = createExportTaskByTemplateField(data);
//                break;
            case SELECT_FIELD:
                exportTask = createExportTaskByModelField(data);
                break;
            default:
                throw PamirsException.construct(FileExpEnumerate.EXPORT_METHOD_IS_ERROR).errThrow();
        }
        return exportTask;
    }

    protected T createExportTaskByTemplate(T data) {
        ExcelDefinitionContext context = fetchExcelDefinitionContextByWorkbookDefinition(data);

        prepareExportTask(data, context);

        data.create();

        doExport(data, context);

        return data;
    }

    protected ExcelDefinitionContext fetchExcelDefinitionContextByWorkbookDefinition(T data) {
        ExcelWorkbookDefinition workbookDefinition = this.excelWorkbookDefinitionService.queryOne(data.getWorkbookDefinition());
        if (workbookDefinition == null) {
            throw PamirsException.construct(FileExpEnumerate.EXPORT_TEMPLATE_NOT_EXIST).errThrow();
        }
        data.setWorkbookDefinition(workbookDefinition);
        processClearExportStyle(data);
        return excelFileService.refreshDefinitionContext(workbookDefinition);
    }

    protected T createExportTaskByTemplateField(T data) {
        throw new UnsupportedOperationException();
    }

    protected T createExportTaskByModelField(T data) {
        ExcelDefinitionContext context = fetchExcelDefinitionContextByModel(data);

        prepareExportTask(data, context);

        data.create();

        doExport(data, context);

        return data;
    }

    protected ExcelDefinitionContext fetchExcelDefinitionContextByModel(T data) {
        String model = data.getModel();
        if (StringUtils.isBlank(model)) {
            throw PamirsException.construct(FileExpEnumerate.EXPORT_MODEL_IS_NULL).errThrow();
        }
        ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(model);
        if (modelConfig == null) {
            throw PamirsException.construct(FileExpEnumerate.EXPORT_MODEL_NOT_EXIST).errThrow();
        }
        List<ExcelModelField> selectedFields = data.getSelectedFields();
        if (CollectionUtils.isEmpty(selectedFields)) {
            throw PamirsException.construct(FileExpEnumerate.EXPORT_FIELD_IS_NOT_SELECTED).errThrow();
        }
        ExcelFixedHeadHelper fixedHeadHelper = ExcelHelper.fixedHeader(model, ExcelConstant.SELECT_FIELD_AUTOMATIC_TEMPLATE)
                .setDisplayName(modelConfig.getDisplayName() + ExcelConstant.EXPORT_NAME)
                .createBlock(modelConfig.getDisplayName(), model);
        MemoryListSearchCache<String, ModelFieldConfig> modelFieldCache = new MemoryListSearchCache<>(modelConfig.getModelFieldConfigList(), ModelFieldConfig::getField);
        for (ExcelModelField selectedField : selectedFields) {
            String field = selectedField.getField();
            String displayName = selectedField.getDisplayName();
            ModelFieldConfig modelFieldConfig = modelFieldCache.get(field);
            if (modelFieldConfig == null) {
                throw PamirsException.construct(FileExpEnumerate.EXPORT_MODEL_FIELD_NOT_EXIST).errThrow();
            }
            ExcelCellDefinition cellDefinition = new ExcelCellDefinition().setValue(displayName);
            String optionLabel = selectedField.getOptionLabel();
            if (StringUtils.isBlank(optionLabel)) {
                fixedHeadHelper.addColumn(field, cellDefinition);
            } else {
                if (ExcelFixedHeadHelper.fillCellDefinition(modelFieldConfig, cellDefinition)) {
                    if (ExcelValueTypeEnum.OBJECT.equals(cellDefinition.getType())) {
                        ModelField modelField = modelFieldConfig.getModelField();
                        TtypeEnum ttype = modelField.getTtype();
                        if (TtypeEnum.isRelatedType(ttype.value())) {
                            ttype = modelField.getRelatedTtype();
                        }
                        if (TtypeEnum.isRelationOne(ttype)) {
                            cellDefinition.setFormat(optionLabel);
                        } else if (TtypeEnum.isRelationMany(ttype)) {
                            cellDefinition.setFormat(String.format("results = []; for (activeRecord : activeRecords) { results.add(%s); } return results.stream().collect(java.util.stream.Collectors.joining(\",\"));", optionLabel));
                        }
                    }
                    fixedHeadHelper.addColumn(field, cellDefinition);
                }
            }
        }
        ExcelWorkbookDefinition workbookDefinition = fixedHeadHelper.build();
        data.setWorkbookDefinition(workbookDefinition);
        processClearExportStyle(data);
        return ExcelWorkbookDefinitionUtil.getDefinitionContext(workbookDefinition);
    }

    protected void prepareExportTask(T data, ExcelDefinitionContext context) {
        FileClient fileClient = FileClientFactory.getClient();
        if (fileClient == null) {
            throw PamirsException.construct(FileExpEnumerate.FILE_SERVER_NOT_FOUND_ERROR).errThrow();
        }

        // 设置当前语言
        TranslateService translateService = TranslateServiceHolder.get();
        context.setCurrentLang(translateService.getCurrentLang());

        CdnFileForm result = fileClient.getFormData(ExcelHelper.translateFilename(context, context.getFilename()));
        data.setFile(new PamirsFile().setName(result.getFileName())
                .setUrl(result.getDownloadUrl())
                .setType(FileTypeEnum.URL));

        // 处理RSQL的前端加密
        if (data.getConditionWrapper() != null) {
            String rsql = data.getConditionWrapper().getRsql();
            if (StringUtils.isNotBlank(rsql)) {
                if (rsql.startsWith(RSQL_ENCODE_PREFIX)) {
                    rsql = rsql.substring(RSQL_ENCODE_PREFIX.length());
                    log.debug("rsql解密,密文:{}", rsql);
                    try {
                        rsql = new String(Base64.getMimeDecoder().decode(rsql));
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                    log.debug("rsql解密,明文:{}", rsql);
                }
                data.setRsql(rsql);
                data.getConditionWrapper().setRsql(rsql);
            }
        }

        ExcelWorkbookDefinition workbookDefinition = data.getWorkbookDefinition();
        String workbookName = workbookDefinition.getName();
        String taskName = Optional.ofNullable(workbookDefinition.getDisplayName())
                .filter(StringUtils::isNotBlank)
                .orElse(workbookName);
        if (translateService.needTranslate()) {
            taskName = ExcelConstant.EXPORT_TASK_NAME_TRANSLATE + context.translate(taskName);
        } else {
            taskName = ExcelConstant.EXPORT_TASK_NAME + taskName;
        }
        data.setName(taskName)
                .setWorkbookDefinition(workbookDefinition)
                .setWorkbookName(workbookName)
                .setState(ExcelTaskStateEnum.PROCESSING)
                .setModule(Optional.ofNullable(ExcelWorkbookDefinitionUtil.getCurrentModule(workbookDefinition.getModel())).map(ModuleDefinition::getModule).orElse(null))
                .setCreateUid(PamirsSession.getUserId())
                .setWriteUid(PamirsSession.getUserId());
    }

    protected void processClearExportStyle(ExcelExportTask exportTask) {
        ExcelWorkbookDefinition workbookDefinition = exportTask.getWorkbookDefinition();
        Boolean clearExportStyle = workbookDefinition.getClearExportStyle();
        if (clearExportStyle != null) {
            workbookDefinition.setClearExportStyle(clearExportStyle);
            return;
        }
        ExcelExportFileTypeEnum fileType = exportTask.getFileType();
        if (fileType != null) {
            clearExportStyle = ExcelExportFileTypeEnum.CSV.equals(fileType);
            workbookDefinition.setClearExportStyle(clearExportStyle);
            return;
        }
        clearExportStyle = fileProperties.getExportProperty().getDefaultClearExportStyle();
        workbookDefinition.setClearExportStyle(clearExportStyle);
    }
}
