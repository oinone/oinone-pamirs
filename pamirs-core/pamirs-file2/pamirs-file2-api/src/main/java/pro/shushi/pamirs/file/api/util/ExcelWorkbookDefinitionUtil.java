package pro.shushi.pamirs.file.api.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate;
import pro.shushi.pamirs.boot.web.spi.holder.TranslateServiceHolder;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.ObjectHelper;
import pro.shushi.pamirs.core.common.TreeHelper;
import pro.shushi.pamirs.core.common.function.lambda.PamirsSupplier;
import pro.shushi.pamirs.file.api.config.ExcelConstant;
import pro.shushi.pamirs.file.api.config.FileConstant;
import pro.shushi.pamirs.file.api.config.FileProperties;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.context.ExcelExportContext;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.context.ExcelProcessContext;
import pro.shushi.pamirs.file.api.easyexcel.impl.DefaultEasyExcelLoopMergeWriteHandler;
import pro.shushi.pamirs.file.api.easyexcel.impl.DefaultEasyExcelTemplateWriteHandler;
import pro.shushi.pamirs.file.api.easyexcel.impl.DefaultExcelAnalysisEventListener;
import pro.shushi.pamirs.file.api.enmu.*;
import pro.shushi.pamirs.file.api.entity.*;
import pro.shushi.pamirs.file.api.function.ExcelReadCallback;
import pro.shushi.pamirs.file.api.model.*;
import pro.shushi.pamirs.file.api.util.analysis.ExcelAnalysisHelper;
import pro.shushi.pamirs.file.api.util.analysis.ExcelFixedFormatAnalysisHelper;
import pro.shushi.pamirs.file.api.util.analysis.ExcelFixedHeaderAnalysisHelper;
import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.TxConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiFunction;

import java.util.function.Consumer;

import static pro.shushi.pamirs.meta.common.util.TypeReferences.TR_MAP_SS;
@Slf4j

public class ExcelWorkbookDefinitionUtil {
    public static final BiFunction<EasyExcelBlockDefinition, String[], String> EASY_EXCEL_FIXED_HEADER_FILL_KEY_GENERATOR = (blockDefinition, fields) -> {
        boolean isFirst = true;
        StringBuilder builder = new StringBuilder();
        for (String field : fields) {
            if (!isFirst) {
                builder.append(FileConstant.SEPARATION_CHARACTER);
            }
            isFirst = false;
            if (field.endsWith(FileConstant.LIST_FLAG_CHARACTER)) {
                field = field.substring(0, field.length() - FileConstant.LIST_FLAG_CHARACTER.length());
            }
            builder.append(field);
        }
        return builder.toString();
    };
    /**
     * <h>获取Excel定义上下文</h>
     * 1、获取工作表定义的JSON字符串（以下简称定义）
     * 2、解析定义
     * 3、获取定义的哈希值
     * 4、获取定义的检查值
     * 5、查看是否已经存在Excel定义上下文，若不存在，则进行初始化并返回；若存在，则继续判断。
     * 6、根据哈希值和检查值判断该上下文是否发生变化，若未发生变化，则直接返回；否则进行初始化并返回；
     * 补充说明：每次初始化会使用update方法更新数据库的内容
     * </p>
     *
     * @param workbookDefinition 工作簿定义
     * @return Excel定义上下文
     */
    public static ExcelDefinitionContext getDefinitionContext(ExcelWorkbookDefinition workbookDefinition) {
        String sheetDefinitions = workbookDefinition.getSheetDefinitions();
        String scope = sheetDefinitions.substring(0, 32);
        int hashCode = sheetDefinitions.substring(32).hashCode();
        String definitionContextString = workbookDefinition.getDefinitionContext();
        if (StringUtils.isBlank(definitionContextString)) {
            return initialization(workbookDefinition, hashCode, scope);
        }
        ExcelDefinitionContext context = JSON.parseObject(definitionContextString, ExcelDefinitionContext.class, JSON.DEFAULT_PARSER_FEATURE & ~Feature.DisableCircularReferenceDetect.getMask());
        boolean isInitialization = context.getHashCode() == hashCode && context.getScope().equals(scope);
        if (isInitialization) {
            List<ExcelLocation> locations = workbookDefinition.getLocations();
            if (locations != null) {
                Map<String, Map<String, String>> locationCache = context.getLocations();
                if (locationCache == null) {
                    isInitialization = false;
                } else {
                    Map<String, Map<String, String>> newLocationCache = convertLocations(locations);
                    isInitialization = newLocationCache != null && JSON.toJSONString(locationCache).equals(JSON.toJSONString(newLocationCache));
                }
            }
        }
        if (isInitialization) {
            if (workbookDefinition.getSheetList() == null) {
                workbookDefinition.analysisSheetDefinitions();
            }
            context.setOriginSheetList(workbookDefinition.getSheetList())
                    .setFilename(ExcelHelper.generatorFilename(workbookDefinition));
            return context;
        }
        return initialization(workbookDefinition, hashCode, scope);
    }

    public static void initImportTask(ExcelDefinitionContext context, ExcelWorkbookDefinition workbookDefinition, ExcelImportTask importTask) {
        initImportTask(context, workbookDefinition, importTask, true);
    }

    public static void initImportTask(ExcelDefinitionContext context, ExcelWorkbookDefinition workbookDefinition, ExcelImportTask importTask, boolean autoCreate) {
        FileProperties.FileImportProperties importProperty = BeanDefinitionUtils.getBean(FileProperties.class).getImportProperty();
        Boolean eachImport = importTask.getEachImport();
        if (eachImport == null) {
            eachImport = workbookDefinition.getEachImport();
            if (eachImport == null) {
                eachImport = importProperty.getDefaultEachImport();
            }
        }
        Boolean hasErrorRollback = importTask.getHasErrorRollback();
        if (hasErrorRollback == null) {
            hasErrorRollback = workbookDefinition.getHasErrorRollback();
            if (hasErrorRollback == null) {
                hasErrorRollback = false;
            }
        }
        Integer maxErrorLength = importTask.getMaxErrorLength();
        if (maxErrorLength == null) {
            maxErrorLength = workbookDefinition.getMaxErrorLength();
            if (maxErrorLength == null) {
                maxErrorLength = importProperty.getMaxErrorLength();
            }
        }
        String module = importTask.getModule();
        if (StringUtils.isBlank(module)) {
            module = Optional.ofNullable(getCurrentModule(workbookDefinition.getModel())).map(ModuleDefinition::getModule).orElse(null);
        }
        String workbookName = workbookDefinition.getName();
        String taskName = Optional.ofNullable(workbookDefinition.getDisplayName()).filter(StringUtils::isNotBlank).orElse(workbookName);
        if (TranslateServiceHolder.get().needTranslate()) {
            taskName = ExcelConstant.IMPORT_TASK_NAME_TRANSLATE + context.translate(taskName);
        } else {
            taskName = I18nUtils.getMessage(ExcelConstant.IMPORT_TASK_NAME) + taskName;
        }
        importTask.setEachImport(eachImport)
                .setHasErrorRollback(hasErrorRollback)
                .setMaxErrorLength(maxErrorLength)
                .setName(taskName)
                .setWorkbookDefinition(workbookDefinition)
                .setWorkbookName(workbookName)
                .setState(ExcelTaskStateEnum.PROCESSING)
                .setModule(module)
                .setCreateUid(PamirsSession.getUserId())
                .setWriteUid(PamirsSession.getUserId());
        if (autoCreate) {
            importTask.create();
        }
    }

    @Deprecated
    public static void initImportTask(ExcelWorkbookDefinition workbookDefinition, ExcelImportTask importTask) {
        initImportTask(workbookDefinition, importTask, true);
    }

    @Deprecated
    public static void initImportTask(ExcelWorkbookDefinition workbookDefinition, ExcelImportTask importTask, boolean autoCreate) {
        FileProperties.FileImportProperties importProperty = BeanDefinitionUtils.getBean(FileProperties.class).getImportProperty();
        Boolean eachImport = importTask.getEachImport();
        if (eachImport == null) {
            eachImport = workbookDefinition.getEachImport();
            if (eachImport == null) {
                eachImport = importProperty.getDefaultEachImport();
            }
        }
        Boolean hasErrorRollback = importTask.getHasErrorRollback();
        if (hasErrorRollback == null) {
            hasErrorRollback = workbookDefinition.getHasErrorRollback();
            if (hasErrorRollback == null) {
                hasErrorRollback = false;
            }
        }
        Integer maxErrorLength = importTask.getMaxErrorLength();
        if (maxErrorLength == null) {
            maxErrorLength = workbookDefinition.getMaxErrorLength();
            if (maxErrorLength == null) {
                maxErrorLength = importProperty.getMaxErrorLength();
            }
        }
        String module = importTask.getModule();
        if (StringUtils.isBlank(module)) {
            module = Optional.ofNullable(getCurrentModule(workbookDefinition.getModel())).map(ModuleDefinition::getModule).orElse(null);
        }
        String workbookName = Optional.ofNullable(workbookDefinition.getDisplayName()).filter(StringUtils::isNotBlank).orElse(workbookDefinition.getName());
        importTask.setEachImport(eachImport)
                .setHasErrorRollback(hasErrorRollback)
                .setMaxErrorLength(maxErrorLength)
                .setName(I18nUtils.getMessage(ExcelConstant.IMPORT_TASK_NAME) + workbookName)
                .setWorkbookDefinition(workbookDefinition)
                .setWorkbookName(workbookDefinition.getName())
                .setState(ExcelTaskStateEnum.PROCESSING)
                .setModule(module)
                .setCreateUid(PamirsSession.getUserId())
                .setWriteUid(PamirsSession.getUserId());
        if (autoCreate) {
            importTask.create();
        }
    }

    /**
     * @deprecated since 5.0.0 using PageLoadHelper#getPageLoadModule
     */
    @Deprecated
    public static ModuleDefinition getCurrentModule(String model) {
        String module = PamirsSession.getRequestFromModule();
        if (StringUtils.isNotBlank(module)) {
            return PamirsSession.getContext().getModuleCache().get(module);
        }
        module = Optional.ofNullable(model).filter(StringUtils::isNotBlank).map(v -> PamirsSession.getContext().getModelConfig(v)).map(ModelConfig::getModule).orElse(null);
        if (StringUtils.isBlank(module)) {
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_MODULE_CAN_NOT_ACCESS_ERROR).errThrow();
        }
        return PamirsSession.getContext().getModule(module);
    }

    /**
     * 创建导入模板
     *
     * @param definitionContext Excel定义上下文
     * @return Excel工作簿 {@link Workbook}
     */
    public static Workbook createImportTemplate(ExcelDefinitionContext definitionContext) {
        return createTemplate0(definitionContext,
                ExcelWorkbookDefinitionUtil::presetRowProcess,
                ExcelWorkbookDefinitionUtil::translateProcess,
                ExcelWorkbookDefinitionUtil::importTemplateFillBlock,
                ExcelWorkbookDefinitionUtil::importTemplateAddMergedRegion,
                ExcelWorkbookDefinitionUtil::afterPropertySheet,
                ExcelWorkbookDefinitionUtil::sheetLoopMergeRangeProcess);
    }

    /**
     * 创建导出模板
     *
     * @param definitionContext Excel定义上下文
     * @return Excel工作簿 {@link Workbook}
     */
    public static Workbook createExportTemplate(ExcelDefinitionContext definitionContext) {
        return createTemplate0(definitionContext,
                ExcelWorkbookDefinitionUtil::translateProcess,
                ExcelWorkbookDefinitionUtil::exportTemplateFillBlock,
                ExcelWorkbookDefinitionUtil::afterPropertySheet);
    }

    /**
     * <h>填充模板</h>
     * <p>
     * 按区块分批次填充，每个区块填充一次
     * </p>
     *
     * @param exportContext 导出上下文
     */
    public static void fillTemplate(ExcelExportContext exportContext) {
        ExcelWriter writer = exportContext.getWriter();
        List<Object> objects = exportContext.getDataList();
        if (objects.size() == 0) {
            writer.finish();
            return;
        }
        Object object = objects.get(0);
        if (object instanceof EasyExcelSheetData) {
            dynamicFillTemplate(exportContext, FetchUtil.cast(objects));
        } else {
            standardFillTemplate(exportContext);
        }
    }

    @SuppressWarnings("unchecked")
    private static void fillTemplate(ExcelDefinitionContext definitionContext, EasyExcelBlockDefinition blockDefinition, ExcelWriter writer, WriteSheet writeSheet, Object data) {
        String blockPrefix = FileConstant.BLOCK_PREFIX + blockDefinition.getBlockNumber();
        List<FillWrapper> fillWrappers = new ArrayList<>();
        if (data instanceof List) {
            List<Object> list = (List<Object>) data;
            if (list.isEmpty()) {
                fillWrappers.add(new FillWrapper(blockPrefix, Collections.emptyList()));
            } else {
                int eachFillSize = 5000, fromOffset, toOffset = 0, size = list.size();
                do {
                    fromOffset = toOffset;
                    toOffset += eachFillSize;
                    if (toOffset >= size) {
                        toOffset = size;
                    }
                    fillWrappers.add(new FillWrapper(blockPrefix, DataConvertHelper.convertDataByList(definitionContext, blockDefinition, list.subList(fromOffset, toOffset))));
                } while (toOffset != size);
            }
        } else {
            if (data == null) {
                fillWrappers.add(new FillWrapper(blockPrefix, Collections.emptyList()));
            } else {
                fillWrappers.add(new FillWrapper(blockPrefix, DataConvertHelper.convertDataByObject(definitionContext, blockDefinition, data)));
            }
        }
        FillConfig fillConfig = FillConfig.builder()
                .forceNewRow(true)
                .direction(blockDefinition.getDirection().getEasyExcelWriteDirection())
                .build();
        int index = 0;
        for (FillWrapper fillWrapper : fillWrappers) {
            writer.fill(fillWrapper, fillConfig, writeSheet);
            fillWrappers.set(index, null);
        }
    }

    private static void standardFillTemplate(ExcelExportContext exportContext) {
        ExcelDefinitionContext definitionContext = exportContext.getDefinitionContext();
        clearDefinitionContext(definitionContext);
        List<Object> objects = exportContext.getDataList();
        ExcelWriter writer = exportContext.getWriter();
        int maxBlockNumber = objects.size();
        WriteSheet writeSheet;
        int blockNumber = 0;
        int sheetNumber = 0;
        try {
            fillSheet:
            for (EasyExcelSheetDefinition sheetDefinition : definitionContext.getSheetList()) {
                writeSheet = EasyExcel.writerSheet()
                        .sheetName(sheetDefinition.getName())
                        .sheetNo(sheetNumber)
                        .autoTrim(true)
                        .build();
                for (EasyExcelBlockDefinition blockDefinition : sheetDefinition.getBlockDefinitions()) {
                    if (blockNumber >= maxBlockNumber) {
                        break fillSheet;
                    }
                    Object object = objects.get(blockNumber);
                    if (object == null) {
                        continue;
                    }
                    if (MapUtils.isNotEmpty(blockDefinition.getFieldCells())) {
                        fillTemplate(definitionContext, blockDefinition, writer, writeSheet, object);
                    }
                    blockNumber++;
                }
                ExcelProcessContext processContext = new ExcelProcessContext()
                        .setDefinitionContext(definitionContext)
                        .setSheetDefinition(sheetDefinition)
                        .setWorkbook(sheetDefinition.getWorkbook())
                        .setSheet(sheetDefinition.getSheet());
                afterPropertySheet(processContext);
                sheetMergeRangeProcess(processContext);
                sheetLoopMergeRangeProcess(processContext);
                sheetNumber++;
            }
        } finally {
            if (writer != null) {
                writer.finish();
            }
        }
    }

    private static void dynamicFillTemplate(ExcelExportContext exportContext, List<EasyExcelSheetData> dataList) {
        ExcelDefinitionContext definitionContext = exportContext.getDefinitionContext();
        clearDefinitionContext(definitionContext);
        ExcelWriter writer = exportContext.getWriter();
        EasyExcelSheetDefinition sheetDefinition = definitionContext.getSheetList().get(0);
        WriteSheet writeSheet = EasyExcel.writerSheet()
                .sheetName(sheetDefinition.getName())
                .sheetNo(0)
                .autoTrim(true)
                .build();
        fillTemplateBySheet(definitionContext, sheetDefinition, writer, writeSheet, dataList);
    }

    private static void fillTemplateBySheet(ExcelDefinitionContext definitionContext, EasyExcelSheetDefinition sheetDefinition, ExcelWriter writer, WriteSheet writeSheet, List<EasyExcelSheetData> dataList) {
        List<EasyExcelBlockDefinition> blockDefinitions = sheetDefinition.getBlockDefinitions();
        int dataSize = dataList.size();
        int blockSize = blockDefinitions.size();
        InputStream templateInputStream = null;
        try {
            if (dataSize > 1) {
                try {
                    templateInputStream = rewriteExportTemplate(sheetDefinition, writer, writeSheet, dataSize);
                    if (templateInputStream == null) {
                        log.error("Failed to rewrite template");
                        dataSize = 1;
                    }
                } catch (IOException e) {
                    log.error("Failed to rewrite template", e);
                    dataSize = 1;
                }
            }
            if (templateInputStream != null) {
                ExcelWriter replaceWriter = rebuildExcelWriter(writer, templateInputStream, new DefaultEasyExcelLoopMergeWriteHandler(sheetDefinition));
                if (replaceWriter == null) {
                    log.error("Redirect write failed");
                    dataSize = 1;
                } else {
                    writer = replaceWriter;
                }
            }
            for (int i = 0; i < dataSize; i++) {
                List<Object> objects = dataList.get(i).getDataList();
                int maxDataSize = objects.size();
                for (int k = 0; k < blockSize; k++) {
                    EasyExcelBlockDefinition blockDefinition = blockDefinitions.get(k);
                    if (k >= maxDataSize) {
                        break;
                    }
                    Object object = objects.get(k);
                    if (object == null) {
                        continue;
                    }
                    if (i > 0) {
                        blockDefinition.setBlockNumber(blockDefinition.getBlockNumber() + blockSize);
                    }
                    if (MapUtils.isNotEmpty(blockDefinition.getFieldCells())) {
                        fillTemplate(definitionContext, blockDefinition, writer, writeSheet, object);
                    }
                }
            }
            ExcelProcessContext processContext = new ExcelProcessContext()
                    .setDefinitionContext(definitionContext)
                    .setSheetDefinition(sheetDefinition)
                    .setWorkbook(sheetDefinition.getWorkbook())
                    .setSheet(sheetDefinition.getSheet());
            afterPropertySheet(processContext);
            sheetMergeRangeProcess(processContext);
            sheetLoopMergeRangeProcess(processContext);
        } finally {
            IOUtils.closeQuietly(templateInputStream);
            if (writer != null) {
                writer.finish();
            }
        }
    }

    private static InputStream rewriteExportTemplate(EasyExcelSheetDefinition sheetDefinition, ExcelWriter writer, WriteSheet writeSheet, int dataSize) throws IOException {
        int blockSize = sheetDefinition.getBlockDefinitions().size();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            return rebuildTemplateExcelWriter(writer, outputStream, new DefaultEasyExcelTemplateWriteHandler(sheetDefinition), (writer0) -> {
                for (int i = 1; i < dataSize; i++) {
                    appendFillTemplate(sheetDefinition, writer0, writeSheet, blockSize * i);
                }
            });
        }
    }

    private static InputStream rebuildTemplateExcelWriter(ExcelWriter writer, ByteArrayOutputStream outputStream,
                                                          WriteHandler templateWriteHandler, Consumer<ExcelWriter> consumer) {
        WriteWorkbookHolder writeWorkbookHolder = writer.writeContext().writeWorkbookHolder();

        ExcelWriter templateWriter = null;
        try {
            templateWriter = EasyExcel.write(outputStream).registerWriteHandler(templateWriteHandler).build();
            WriteWorkbookHolder templateWriteWorkbookHolder = templateWriter.writeContext().writeWorkbookHolder();
            templateWriteWorkbookHolder.setWorkbook(writeWorkbookHolder.getWorkbook());
            templateWriteWorkbookHolder.setCachedWorkbook(writeWorkbookHolder.getCachedWorkbook());
            templateWriteWorkbookHolder.setTempTemplateInputStream(writeWorkbookHolder.getTempTemplateInputStream());
            consumer.accept(templateWriter);
            templateWriter.finish();
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Throwable e) {
            log.error("rebuild excel writer error.", e);
        } finally {
            if (templateWriter != null) {
                templateWriter.finish();
            }
        }
        return null;
    }

    private static ExcelWriter rebuildExcelWriter(ExcelWriter writer, InputStream templateInputStream, WriteHandler writeHandler) {
        try {
            return EasyExcelHelper.cloneRegister(EasyExcel.write(writer.writeContext().writeWorkbookHolder().getOutputStream()).withTemplate(templateInputStream), writer)
                    .registerWriteHandler(writeHandler).build();
        } catch (Throwable e) {
            log.error("rebuild excel writer error.", e);
        }
        return null;
    }

    private static void appendFillTemplate(EasyExcelSheetDefinition sheetDefinition, ExcelWriter writer, WriteSheet writeSheet, int blockNumberOffset) {
        List<List<String>> list = new ArrayList<>();
        for (EasyExcelBlockDefinition blockDefinition : sheetDefinition.getBlockDefinitions()) {
            List<ExcelRowDefinition> allRowDefinitionList = new ArrayList<>();
            Optional.ofNullable(blockDefinition.getHeaderDefinitionList()).ifPresent(allRowDefinitionList::addAll);
            if (ExcelAnalysisTypeEnum.FIXED_HEADER.equals(blockDefinition.getAnalysisType())) {
                allRowDefinitionList.add(blockDefinition.getConfigHeader());
            }
            Optional.ofNullable(blockDefinition.getRowDefinitionList()).ifPresent(allRowDefinitionList::addAll);
            Map<String, EasyExcelCellDefinition> fieldCells = blockDefinition.getFieldCells();
            for (ExcelRowDefinition rowDefinition : allRowDefinitionList) {
                List<String> row = new ArrayList<>();
                List<ExcelCellDefinition> cellList = rowDefinition.getCellList();
                if (CollectionUtils.isNotEmpty(cellList)) {
                    for (ExcelCellDefinition cell : cellList) {
                        String dataKey = null;
                        String field = cell.getField();
                        if (StringUtils.isNotBlank(field)) {
                            String temp = Optional.ofNullable(fieldCells).map(v -> v.get(field)).map(EasyExcelCellDefinition::getValue).orElse(null);
                            if (StringUtils.isNotBlank(temp)) {
                                int index = temp.indexOf('.');
                                if (index != -1) {
                                    String dataKeySuffix = temp.substring(index);
                                    dataKey = "{" + FileConstant.BLOCK_PREFIX + (blockDefinition.getBlockNumber() + blockNumberOffset) + dataKeySuffix;
                                }
                            }
                        }
                        if (StringUtils.isNotBlank(dataKey)) {
                            row.add(dataKey);
                        } else {
                            row.add(Optional.ofNullable(cell.getValue()).orElse(""));
                        }
                    }
                }
                list.add(row);
            }
        }
        writer.write(list, writeSheet);
    }

    /**
     * 读取导入Excel
     *
     * @param importContext    导入上下文
     * @param callbackSupplier 数据读取回调提供者
     */
    public static void readImportExcel(ExcelImportContext importContext, PamirsSupplier<ExcelReadCallback> callbackSupplier) {
        ExcelDefinitionContext definitionContext = importContext.getDefinitionContext();
        clearDefinitionContext(definitionContext);
        ExcelReader reader = importContext.getReader();
        List<ReadSheet> readSheetList = new ArrayList<>();
        int sheetIndex = 0;
        List<ReadSheet> sheets = reader.excelExecutor().sheetList();
        int realSheetSize = CollectionUtils.size(sheets);
        ExcelImportModeEnum excelImportMode = definitionContext.getExcelImportMode();
        List<ExcelReadCallback> readCallbackList = new ArrayList<>();
        ExcelImportTask importTask = importContext.getImportTask();
        importTask.setReadCallbackList(readCallbackList);
        switch (excelImportMode) {
            case SINGLE_MODEL:
                EasyExcelSheetDefinition sheetDefinition = definitionContext.getSheetList().get(0);
                for (int i = 0; i < realSheetSize; i++) {
                    ExcelReadCallback readCallback = callbackSupplier.get();
                    readCallbackList.add(readCallback);
                    EasyExcelSheetDefinition currentSheet = ObjectUtils.clone(sheetDefinition);
                    DefaultExcelAnalysisEventListener eventListener = new DefaultExcelAnalysisEventListener(importContext, i, currentSheet, readCallback);
                    importContext.getEventListenerList().add(eventListener);
                    readSheetList.add(EasyExcel.readSheet(i, sheets.get(i).getSheetName())
                            .headRowNumber(sheetDefinition.getDesignRange().getBeginRowIndex())
                            .registerReadListener(eventListener)
                            .build());
                }
                break;
            case MULTI_MODEL:
            default:
                //MULTI_MODEL是默认
                for (EasyExcelSheetDefinition currentSheet : definitionContext.getSheetList()) {
                    ExcelReadCallback readCallback = callbackSupplier.get();
                    readCallbackList.add(readCallback);
                    DefaultExcelAnalysisEventListener eventListener = new DefaultExcelAnalysisEventListener(importContext, sheetIndex, currentSheet, readCallback);
                    importContext.getEventListenerList().add(eventListener);
                    readSheetList.add(EasyExcel.readSheet(sheetIndex)
                            .headRowNumber(currentSheet.getDesignRange().getBeginRowIndex())
                            .registerReadListener(eventListener)
                            .build());
                    sheetIndex++;
                }
                break;
        }
        try {
            TxConfig txConfig = PamirsSession.getContext().getTxConfig(definitionContext.getModel(), ExcelDefinitionContext.EXCEL_TX_CONFIG_PREFIX + definitionContext.getName());
            boolean hasErrorRollback = importTask.getHasErrorRollback();
            if (txConfig == null && hasErrorRollback) {
                txConfig = new TxConfig();
            }
            if (txConfig == null) {
                reader.read(readSheetList);
            } else {
                Tx.build(txConfig).executeWithoutResult(status -> {
                    reader.read(readSheetList);
                    if (hasErrorRollback) {
                        List<TaskMessage> messages = importTask.getMessages();
                        for (TaskMessage message : Optional.ofNullable(messages).orElse(Collections.emptyList())) {
                            if (TaskMessageLevelEnum.ERROR.equals(message.getLevel())) {
                                status.setRollbackOnly();
                                break;
                            }
                        }
                    }
                });
            }
        } finally {
            reader.finish();
        }
    }

    /**
     * <h>清理Excel定义上下文</h>
     * <p>
     * Excel定义上下文是在生成导入/导出模板时同步生成的，其中包含了部分不可复用的数据。<br>
     * 但这些不可复用的属性在生成导入/导出模板时起到了关键的加速作用。<br>
     * 在执行具体的读取/写入功能时，需先清理这部分数据，将它还原为可被读取/写入的数据。<br>
     * </p>
     *
     * @param definitionContext Excel定义上下文
     */
    private static void clearDefinitionContext(ExcelDefinitionContext definitionContext) {
        for (EasyExcelSheetDefinition sheetDefinition : definitionContext.getSheetList()) {
            List<ExcelCellRangeDefinition> mergeRangeList = sheetDefinition.getMergeRangeList();
            if (CollectionUtils.isNotEmpty(mergeRangeList)) {
                Models.modelDirective().enableReentry(mergeRangeList);
            }
            sheetDefinition.setCurrentRange(sheetDefinition.getDesignRange().clone());
            List<EasyExcelBlockDefinition> blockDefinitions = sheetDefinition.getBlockDefinitions();
            for (EasyExcelBlockDefinition blockDefinition : blockDefinitions) {
                ExcelCellRangeDefinition designRange = blockDefinition.getDesignRange();
                ExcelCellRangeDefinition currentRange = blockDefinition.getCurrentRange();
                int rowOffsetIndex = currentRange.getBeginRowIndex() - designRange.getBeginRowIndex(),
                        columnOffsetIndex = currentRange.getBeginColumnIndex() - designRange.getBeginColumnIndex();
                ExcelCellRangeHelper.translation(blockDefinition, -rowOffsetIndex, -columnOffsetIndex);
                currentRange.setEndRowIndex(designRange.getEndRowIndex())
                        .setEndColumnIndex(designRange.getEndColumnIndex());
            }
        }
    }

    /**
     * 初始化解析
     *
     * @param workbookDefinition 工作簿定义
     * @param hashCode           工作表定义的JSON字符串的哈希值
     * @param scope              工作表定义的JSON字符串的前32位
     * @return Excel定义上下文
     */
    private static ExcelDefinitionContext initialization(ExcelWorkbookDefinition workbookDefinition, int hashCode, String scope) {
        if (workbookDefinition.getSheetList() == null) {
            workbookDefinition.analysisSheetDefinitions();
        }
        return initialization0(workbookDefinition, hashCode, scope).setIsRefresh(true);
    }

    /**
     * 初始化解析
     *
     * @param workbookDefinition 工作簿定义
     * @param hashCode           工作表定义的JSON字符串的哈希值
     * @param scope              工作表定义的JSON字符串的前32位
     * @return Excel定义上下文
     */
    private static ExcelDefinitionContext initialization0(ExcelWorkbookDefinition workbookDefinition, int hashCode, String scope) {
        ExcelDefinitionContext context = new ExcelDefinitionContext()
                .setHashCode(hashCode)
                .setScope(scope)
                .setTemplateId(workbookDefinition.getId())
                .setFilename(ExcelHelper.generatorFilename(workbookDefinition))
                .setModel(workbookDefinition.getModel())
                .setName(workbookDefinition.getName())
                .setVersion(workbookDefinition.getVersion())
                .setImportStrategy(workbookDefinition.getImportStrategy())
                .setExportStrategy(workbookDefinition.getExportStrategy())
                .setExcelImportMode(workbookDefinition.getExcelImportMode())
                .setOriginSheetList(workbookDefinition.getSheetList())
                .setLocations(convertLocations(workbookDefinition.getLocations()));
        List<EasyExcelSheetDefinition> sheetList = new ArrayList<>();
        context.setSheetList(sheetList);
        int sheetIndex = 0;
        // 块编号，整个Excel的块根据定义的先后顺序进行排列，填充数据的入参与块定义的顺序保持一致，即块级别的数据隔离
        int blockNumber = 0;
        for (ExcelSheetDefinition sheetDefinition : workbookDefinition.getSheetList()) {
            List<EasyExcelBlockDefinition> easyExcelBlockDefinitions = new ArrayList<>();
            List<ExcelCellRangeDefinition> sheetMergeRangeList = new ArrayList<>();
            EasyExcelSheetDefinition easyExcelSheetDefinition = new EasyExcelSheetDefinition()
                    .setName(ExcelHelper.generatorSheetName(sheetDefinition, sheetIndex))
                    .setAutoSizeColumn(ObjectHelper.getOrDefault(sheetDefinition.getAutoSizeColumn(), true))
                    .setOnceFetchData(ObjectHelper.getOrDefault(sheetDefinition.getOnceFetchData(), false))
                    .setBlockDefinitions(easyExcelBlockDefinitions)
                    .setColumnStyles(new HashMap<>())
                    .setMergeRangeList(sheetMergeRangeList)
                    .setLoopMergeRangeList(new ArrayList<>())
                    .setUniqueDefinitions(new HashMap<>());
            sheetList.add(easyExcelSheetDefinition);
            prepareSheetMergeRangeList(sheetMergeRangeList, sheetDefinition);
            List<ExcelUniqueDefinition> uniqueDefinitions = sheetDefinition.getUniqueDefinitions();
            if (CollectionUtils.isNotEmpty(uniqueDefinitions)) {
                for (ExcelUniqueDefinition uniqueDefinition : uniqueDefinitions) {
                    easyExcelSheetDefinition.getUniqueDefinitions().put(uniqueDefinition.getModel(), new HashSet<>(uniqueDefinition.getUniques()));
                }
            }
            List<ExcelBlockDefinition> blockDefinitions = sheetDefinition.getBlockDefinitionList();
            for (ExcelBlockDefinition blockDefinition : blockDefinitions) {
                //处理区块定义内容，转换为可快速解析的格式
                ExcelCellRangeDefinition designRange = blockDefinition.getDesignRange();
                int rowOffsetIndex = designRange.getBeginRowIndex(),
                        columnOffsetIndex = designRange.getBeginColumnIndex();
                EasyExcelBlockDefinition easyExcelBlockDefinition = new EasyExcelBlockDefinition()
                        .setBindingModel(blockDefinition.getBindingModel())
                        .setFetchNamespace(blockDefinition.getFetchNamespace())
                        .setFetchFun(blockDefinition.getFetchFun())
                        .setAnalysisType(blockDefinition.getAnalysisType())
                        .setDirection(blockDefinition.getDirection())
                        .setDomain(blockDefinition.getDomain())
                        .setBlockNumber(blockNumber)
                        .setDesignRange(designRange)
                        .setCurrentRange(designRange.clone())
                        .setUsingCascadingStyle(blockDefinition.getUsingCascadingStyle());
                prepareBlockMergeRangeList(sheetMergeRangeList, blockDefinition, easyExcelBlockDefinition, rowOffsetIndex, columnOffsetIndex);
                easyExcelBlockDefinitions.add(easyExcelBlockDefinition);
                ExcelAnalysisHelper.analysisConfigHeader(easyExcelBlockDefinition, blockDefinition);
                ExcelAnalysisHelper.computeSheetDesignRange(easyExcelSheetDefinition, blockDefinition);
                ExcelAnalysisHelper.lookupLoopMergeRangeList(easyExcelBlockDefinition);
                blockNumber++;
            }
            ExcelAnalysisHelper.influenceBlockAnalysis(easyExcelSheetDefinition);
            int i = 0;
            for (EasyExcelBlockDefinition easyExcelBlockDefinition : easyExcelBlockDefinitions) {
                ExcelBlockDefinition blockDefinition = blockDefinitions.get(i);
                blockDefinitionProcess(easyExcelSheetDefinition, easyExcelBlockDefinition, blockDefinition);
                i++;
            }
            sheetIndex++;
        }
        workbookDefinition.setDefinitionContext(JSON.toJSONString(context, JSON.DEFAULT_GENERATE_FEATURE & ~SerializerFeature.DisableCircularReferenceDetect.getMask()));
        return context;
    }

    private static Map<String, Map<String, String>> convertLocations(List<ExcelLocation> locations) {
        if (CollectionUtils.isEmpty(locations)) {
            return null;
        }
        Map<String, Map<String, String>> locationsMap = new HashMap<>();
        for (ExcelLocation location : locations) {
            String lang = location.getLang();
            if (StringUtils.isBlank(lang)) {
                continue;
            }
            List<ExcelLocationItem> locationItems = location.getLocationItems();
            if (CollectionUtils.isEmpty(locationItems)) {
                continue;
            }
            for (ExcelLocationItem locationItem : locationItems) {
                String origin = locationItem.getOrigin();
                String target = locationItem.getTarget();
                if (StringUtils.isNoneBlank(origin, target)) {
                    locationsMap.computeIfAbsent(lang, k -> new HashMap<>()).put(origin, target);
                }
            }
        }
        return locationsMap;
    }

    private static void prepareSheetMergeRangeList(List<ExcelCellRangeDefinition> sheetMergeRangeList, ExcelSheetDefinition sheetDefinition) {
        List<ExcelCellRangeDefinition> mergeRangeList = sheetDefinition.getMergeRangeList();
        if (CollectionUtils.isNotEmpty(mergeRangeList)) {
            sheetMergeRangeList.addAll(mergeRangeList);
        }
    }

    private static void prepareBlockMergeRangeList(List<ExcelCellRangeDefinition> sheetMergeRangeList, ExcelBlockDefinition blockDefinition, EasyExcelBlockDefinition easyExcelBlockDefinition, int rowOffsetIndex, int columnOffsetIndex) {
        List<ExcelCellRangeDefinition> blockMergeRangeList = blockDefinition.getMergeRangeList();
        List<ExcelCellRangeDefinition> realBlockMergeRangeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(blockMergeRangeList)) {
            for (ExcelCellRangeDefinition blockMergeRange : blockMergeRangeList) {
                ExcelCellRangeDefinition realMergeRange = blockMergeRange.clone();
                if (!realMergeRange.getFixedBeginRowIndex()) {
                    realMergeRange.setBeginRowIndex(realMergeRange.getBeginRowIndex() + rowOffsetIndex)
                            .setEndRowIndex(realMergeRange.getEndRowIndex() + rowOffsetIndex);
                }
                if (!realMergeRange.getFixedBeginColumnIndex()) {
                    realMergeRange.setBeginColumnIndex(realMergeRange.getBeginColumnIndex() + columnOffsetIndex)
                            .setEndColumnIndex(realMergeRange.getEndColumnIndex() + columnOffsetIndex);
                }
                realBlockMergeRangeList.add(realMergeRange);
                sheetMergeRangeList.add(realMergeRange);
            }
        }
        easyExcelBlockDefinition.setMergeRangeList(realBlockMergeRangeList);
    }

    /**
     * 统一的区块处理
     *
     * @param easyExcelSheetDefinition EasyExcel工作表定义
     * @param easyExcelBlockDefinition EasyExcel块定义
     * @param blockDefinition          原始块定义
     */
    private static void blockDefinitionProcess(EasyExcelSheetDefinition easyExcelSheetDefinition, EasyExcelBlockDefinition easyExcelBlockDefinition, ExcelBlockDefinition blockDefinition) {
        ExcelAnalysisTypeEnum analysisType = easyExcelBlockDefinition.getAnalysisType();
        switch (analysisType) {
            case FIXED_HEADER:
                fixedHeaderBlockDefinitionProcess(easyExcelSheetDefinition, easyExcelBlockDefinition, blockDefinition);
                ExcelAnalysisHelper.fillLoopMergeRangeStyle(easyExcelBlockDefinition);
                break;
            case FIXED_FORMAT:
                ExcelFixedFormatAnalysisHelper.configHeaderStyleProcess(easyExcelSheetDefinition, easyExcelBlockDefinition);
                fixedFormatBlockDefinitionProcess(easyExcelSheetDefinition, easyExcelBlockDefinition, blockDefinition);
                break;
            default:
                throw new IllegalArgumentException("Invalid block analysis type. value=" + analysisType);
        }
    }

    private static void fixedHeaderBlockDefinitionProcess(EasyExcelSheetDefinition easyExcelSheetDefinition, EasyExcelBlockDefinition easyExcelBlockDefinition, ExcelBlockDefinition blockDefinition) {
        designRegionColumnStyleProcess(easyExcelSheetDefinition, easyExcelBlockDefinition);
        headerProcess(easyExcelSheetDefinition, easyExcelBlockDefinition, easyExcelBlockDefinition.getHeaderDefinitionList());
        ExcelHeaderDefinition fieldRowDefinition = easyExcelBlockDefinition.getConfigHeader();
        ExcelAnalysisHelper.cellStyleProcess(easyExcelBlockDefinition.getDesignRange(), easyExcelSheetDefinition, easyExcelBlockDefinition, fieldRowDefinition, easyExcelBlockDefinition.getUsingCascadingStyle());
        Map<String, EasyExcelCellDefinition> fieldCells = easyExcelBlockDefinition.getFieldCells();
        for (ExcelCellDefinition cellDefinition : fieldRowDefinition.getCellList()) {
            String field = cellDefinition.getField();
            if (field == null) {
                continue;
            }
            EasyExcelCellDefinition fieldCellDefinition = fieldCells.get(field);
            if (fieldCellDefinition == null) {
                continue;
            }
            cellDefinition.setValue(fieldCellDefinition.getValue());
            ExcelValueTypeEnum type = fieldCellDefinition.getType();
            if (ExcelValueTypeEnum.BOOLEAN.equals(type) || ExcelValueTypeEnum.ENUMERATION.equals(type)) {
                cellDefinition.setType(ExcelValueTypeEnum.STRING);
                cellDefinition.setFormat(null);
            } else {
                cellDefinition.setType(fieldCellDefinition.getType());
                cellDefinition.setFormat(fieldCellDefinition.getFormat());
            }
        }
    }

    private static void fixedFormatBlockDefinitionProcess(EasyExcelSheetDefinition easyExcelSheetDefinition, EasyExcelBlockDefinition easyExcelBlockDefinition, ExcelBlockDefinition blockDefinition) {
        ExcelCellRangeDefinition designRange = easyExcelBlockDefinition.getDesignRange();
        ExcelCellRangeHelper.switchBeginAndEndIndex(designRange, null, (rowBegin, rowEnd, columnBegin, columnEnd) -> {
            Map<Integer, ExcelStyleDefinition> columnStyles = easyExcelSheetDefinition.getColumnStyles();
            Boolean usingCascadingStyle = easyExcelBlockDefinition.getUsingCascadingStyle();
            List<ExcelRowDefinition> rowList = blockDefinition.getRowList();
            if (rowList == null) {
                rowList = new ArrayList<>();
            }
            int rowSize = rowList.size();
            Map<String, EasyExcelCellDefinition> fieldCells = new HashMap<>();
            List<EasyExcelCellDefinition> fieldCellList = new ArrayList<>();
            Set<String> cellKeySet = new HashSet<>();
            ExcelRowDefinition rowDefinition = null;
            for (int i = rowBegin; i <= rowEnd; i++) {
                int realRowIndex = i - rowBegin;
                if (rowSize > realRowIndex) {
                    rowDefinition = rowList.get(realRowIndex);
                } else {
                    if (rowDefinition != null) {
                        rowDefinition = rowDefinition.clone();
                    } else {
                        rowDefinition = new ExcelRowDefinition();
                    }
                    rowList.add(rowDefinition);
                }
                List<ExcelCellDefinition> cellList = rowDefinition.getCellList();
                if (cellList == null) {
                    cellList = new ArrayList<>();
                    rowDefinition.setCellList(cellList);
                }
                ExcelStyleDefinition rowStyleDefinition = ExcelDefinitionConverter.mergeCellStyleDefinition(columnStyles.get(i), rowDefinition.getStyle(), usingCascadingStyle);
                int cellSize = cellList.size();
                for (int k = columnBegin; k <= columnEnd; k++) {
                    ExcelCellDefinition cell;
                    int realColumnIndex = k - columnBegin;
                    if (cellSize > realColumnIndex) {
                        cell = cellList.get(realColumnIndex);
                    } else {
                        cell = new ExcelCellDefinition();
                        cellList.add(cell);
                    }
                    ExcelStyleDefinition cellStyleDefinition = ExcelDefinitionConverter.mergeCellStyleDefinition(rowStyleDefinition, cell.getStyle(), usingCascadingStyle);
                    if (cellStyleDefinition != null) {
                        cellStyleDefinition = cellStyleDefinition.clone();
                        cell.setStyle(cellStyleDefinition);
                    }
                    String fieldCellKey = fieldCellKeyGenerator(easyExcelBlockDefinition, cell, EASY_EXCEL_FIXED_HEADER_FILL_KEY_GENERATOR);
                    if (fieldCellKey == null) {
                        cell.setIsFieldValue(false);
                    } else {
                        cell.setIsFieldValue(true);
                        //创建属性单元格
                        String field = cell.getField();
                        ExcelValueTypeEnum valueType = cell.getType();
                        if (valueType == null) {
                            valueType = ExcelValueTypeEnum.STRING;
                        }
                        String format = cell.getFormat();
                        //仅对布尔的格式化进行默认设置
                        if (ExcelValueTypeEnum.BOOLEAN.equals(valueType) && StringUtils.isBlank(format)) {
                            format = valueType.defaultFormat();
                            cell.setFormat(format);
                        }
                        String bindingModel = easyExcelBlockDefinition.getBindingModel();
                        EasyExcelCellDefinition fieldCell = new EasyExcelCellDefinition()
                                .setKey(fieldCellKey)
                                .setModel(bindingModel)
                                .setField(field)
                                .setValue("{" + FileConstant.BLOCK_PREFIX + easyExcelBlockDefinition.getBlockNumber() + FileConstant.POINT_CHARACTER + fieldCellKey + "}")
                                .setType(valueType)
                                .setFormat(format)
                                .setTranslate(cell.getTranslate())
                                .setIsStatic(cell.getIsStatic());
                        fieldCells.put(field, fieldCell);
                        //构造所需的树节点列表
                        fieldCellList.add(fieldCell);
                        int p = field.lastIndexOf(FileConstant.POINT_CHARACTER);
                        while (p != -1) {
                            String temp = field.substring(0, p);
                            if (cellKeySet.contains(temp)) {
                                break;
                            }
                            cellKeySet.add(temp);
                            EasyExcelCellDefinition tempCellDefinition = new EasyExcelCellDefinition()
                                    .setField(temp)
                                    .setModel(bindingModel);
                            tempCellDefinition.setKey(fieldCellKeyGenerator(easyExcelBlockDefinition, new ExcelCellDefinition().setField(temp), EASY_EXCEL_FIXED_HEADER_FILL_KEY_GENERATOR));
                            if (temp.endsWith(FileConstant.LIST_FLAG_CHARACTER)) {
                                temp = temp.substring(0, temp.length() - FileConstant.LIST_FLAG_CHARACTER.length());
                            }
                            fieldCellList.add(tempCellDefinition.setValue(temp));
                            p = field.lastIndexOf(FileConstant.POINT_CHARACTER, p - 1);
                        }
                    }
                }
            }
            easyExcelBlockDefinition.setFieldCells(fieldCells);
            easyExcelBlockDefinition.setFieldNodeList(TreeHelper.convert(fieldCellList, v -> FileConstant.REGEX_LIST_FLAG_CHARACTER.matcher(v.getField()).replaceAll(""), v -> {
                int p = v.getField().lastIndexOf(FileConstant.POINT_CHARACTER);
                if (p == -1) {
                    return null;
                } else {
                    return FileConstant.REGEX_LIST_FLAG_CHARACTER.matcher(v.getField().substring(0, p)).replaceAll("");
                }
            }));
            easyExcelBlockDefinition.setRowDefinitionList(rowList);
        });
    }

    /**
     * 设计区域的列样式处理 样式: 当前列样式 < 配置行样式 < 单元格样式
     *
     * @param easyExcelSheetDefinition EasyExcel工作表定义
     * @param easyExcelBlockDefinition EasyExcel块定义
     */
    private static void designRegionColumnStyleProcess(EasyExcelSheetDefinition easyExcelSheetDefinition, EasyExcelBlockDefinition easyExcelBlockDefinition) {
        ExcelHeaderDefinition configHeader = easyExcelBlockDefinition.getConfigHeader();
        ExcelStyleDefinition headerStyle = configHeader.getStyle();
        List<ExcelCellDefinition> cellList = configHeader.getCellList();
        if (CollectionUtils.isEmpty(cellList)) {
            log.warn("cellList is null");
            return;
        }
        Map<Integer, ExcelStyleDefinition> columnStyles = easyExcelSheetDefinition.getColumnStyles();
        ExcelCellRangeDefinition designRange = easyExcelBlockDefinition.getDesignRange();
        Boolean usingCascadingStyle = easyExcelBlockDefinition.getUsingCascadingStyle();
        ExcelCellRangeHelper.switchBeginAndEndIndex(designRange, easyExcelBlockDefinition.getDirection(), (rowBegin, rowEnd, columnBegin, columnEnd) -> {
            Map<Integer, EasyExcelCellDefinition> columnFieldCells = new HashMap<>();
            Map<String, EasyExcelCellDefinition> fieldCells = new HashMap<>();
            List<EasyExcelCellDefinition> fieldCellList = new ArrayList<>();
            Set<String> cellKeySet = new HashSet<>();
            int cellSize = cellList.size();
            for (int i = columnBegin; i <= columnEnd; i++) {
                ExcelCellDefinition cell;
                int realColumnIndex = i - columnBegin;
                if (cellSize > realColumnIndex) {
                    cell = cellList.get(realColumnIndex);
                } else {
                    cell = new ExcelCellDefinition();
                    cellList.add(cell);
                }
                ExcelStyleDefinition styleDefinition = ExcelDefinitionConverter.mergeCellStyleDefinition(columnStyles.get(i), headerStyle, usingCascadingStyle);
                styleDefinition = ExcelDefinitionConverter.mergeCellStyleDefinition(styleDefinition, cell.getStyle(), usingCascadingStyle);
                if (styleDefinition != null) {
                    styleDefinition = styleDefinition.clone();
                    cell.setStyle(styleDefinition);
                }
                String fieldCellKey = fieldCellKeyGenerator(easyExcelBlockDefinition, cell, EASY_EXCEL_FIXED_HEADER_FILL_KEY_GENERATOR);
                if (fieldCellKey == null) {
                    cell.setIsFieldValue(false);
                } else {
                    cell.setIsFieldValue(true);
                    //创建属性单元格
                    String field = cell.getField();
                    ExcelValueTypeEnum valueType = cell.getType();
                    if (valueType == null) {
                        valueType = ExcelValueTypeEnum.STRING;
                    }
                    String format = cell.getFormat();
                    // 仅对布尔的格式化进行默认设置
                    if (ExcelValueTypeEnum.BOOLEAN.equals(valueType) && StringUtils.isBlank(format)) {
                        format = valueType.defaultFormat();
                        cell.setFormat(format);
                    }
                    String bindingModel = easyExcelBlockDefinition.getBindingModel();
                    EasyExcelCellDefinition fieldCell = new EasyExcelCellDefinition()
                            .setKey(fieldCellKey)
                            .setModel(bindingModel)
                            .setField(field)
                            .setValue("{" + FileConstant.BLOCK_PREFIX + easyExcelBlockDefinition.getBlockNumber() + FileConstant.POINT_CHARACTER + fieldCellKey + "}")
                            .setType(valueType)
                            .setFormat(format)
                            .setTranslate(cell.getTranslate())
                            .setIsStatic(cell.getIsStatic());
                    columnFieldCells.put(i, fieldCell);
                    fieldCells.put(field, fieldCell);

                    //构造所需的树节点列表
                    fieldCellList.add(fieldCell);
                    int p = field.lastIndexOf(FileConstant.POINT_CHARACTER);
                    while (p != -1) {
                        String temp = field.substring(0, p);
                        if (cellKeySet.contains(temp)) {
                            break;
                        }
                        cellKeySet.add(temp);
                        EasyExcelCellDefinition tempCellDefinition = new EasyExcelCellDefinition()
                                .setField(temp)
                                .setModel(bindingModel);
                        tempCellDefinition.setKey(fieldCellKeyGenerator(easyExcelBlockDefinition, new ExcelCellDefinition().setField(temp), EASY_EXCEL_FIXED_HEADER_FILL_KEY_GENERATOR));
                        if (temp.endsWith(FileConstant.LIST_FLAG_CHARACTER)) {
                            temp = temp.substring(0, temp.length() - FileConstant.LIST_FLAG_CHARACTER.length());
                        }
                        fieldCellList.add(tempCellDefinition.setValue(temp));
                        p = field.lastIndexOf(FileConstant.POINT_CHARACTER, p - 1);
                    }
                }
                columnStyles.put(i, styleDefinition);
            }
            easyExcelBlockDefinition.setColumnFieldCells(columnFieldCells);
            easyExcelBlockDefinition.setFieldCells(fieldCells);
            easyExcelBlockDefinition.setFieldNodeList(TreeHelper.convert(fieldCellList, v -> FileConstant.REGEX_LIST_FLAG_CHARACTER.matcher(v.getField()).replaceAll(""), v -> {
                int p = v.getField().lastIndexOf(FileConstant.POINT_CHARACTER);
                if (p == -1) {
                    return null;
                } else {
                    return FileConstant.REGEX_LIST_FLAG_CHARACTER.matcher(v.getField().substring(0, p)).replaceAll("");
                }
            }));
        });
    }

    /**
     * 表头处理
     *
     * @param easyExcelSheetDefinition EasyExcel工作表定义
     * @param easyExcelBlockDefinition EasyExcel块定义
     * @param headerDefinitionList     Excel表头定义列表
     */
    private static void headerProcess(EasyExcelSheetDefinition easyExcelSheetDefinition, EasyExcelBlockDefinition easyExcelBlockDefinition, List<ExcelHeaderDefinition> headerDefinitionList) {
        ExcelCellRangeDefinition designRange = easyExcelBlockDefinition.getDesignRange();
        Boolean usingCascadingStyle = easyExcelBlockDefinition.getUsingCascadingStyle();
        for (ExcelHeaderDefinition headerDefinition : headerDefinitionList) {
            ExcelAnalysisHelper.cellStyleProcess(designRange, easyExcelSheetDefinition, easyExcelBlockDefinition, headerDefinition, usingCascadingStyle);
        }
        easyExcelBlockDefinition.setHeaderDefinitionList(headerDefinitionList);
    }

    public static String fieldCellKeyGenerator(EasyExcelBlockDefinition blockDefinition, ExcelCellDefinition cellDefinition, BiFunction<EasyExcelBlockDefinition, String[], String> generator) {
        String field = cellDefinition.getField();
        if (StringUtils.isBlank(field)) {
            return null;
        }
        return generator.apply(blockDefinition, FileConstant.REGEX_POINT_CHARACTER.split(field));
    }

    @SafeVarargs
    private static Workbook createTemplate0(ExcelDefinitionContext definitionContext, Consumer<ExcelProcessContext>... consumers) {
        Workbook workbook = WorkbookHelper.createWorkbook(definitionContext.getVersion());
        ExcelProcessContext processContext = new ExcelProcessContext()
                .setDefinitionContext(definitionContext)
                .setWorkbook(workbook);
        Sheet sheet;
        List<ExcelSheetDefinition> originSheetList = definitionContext.getOriginSheetList();
        int sheetIndex = 0;
        for (EasyExcelSheetDefinition sheetDefinition : definitionContext.getSheetList()) {
            sheet = WorkbookHelper.createSheet(workbook, definitionContext.translate(sheetDefinition.getName()));
            processContext.setSheetDefinition(sheetDefinition)
                    .setSheet(sheet);
            if (originSheetList != null) {
                processContext.setOriginSheetDefinition(originSheetList.get(sheetIndex));
            }
            for (Consumer<ExcelProcessContext> consumer : consumers) {
                consumer.accept(processContext);
            }
            sheetIndex++;
        }
        return workbook;
    }

    private static void importTemplateAddMergedRegion(ExcelProcessContext processContext) {
        List<ExcelCellRangeDefinition> mergeRangeList = processContext.getSheetDefinition().getMergeRangeList();
        if (CollectionUtils.isEmpty(mergeRangeList)) {
            return;
        }
        Sheet sheet = processContext.getSheet();
        for (ExcelCellRangeDefinition mergeRange : mergeRangeList) {
            sheet.addMergedRegion(ExcelDefinitionConverter.convertCellRangeAddress(mergeRange));
        }
    }

    private static void exportTemplateAddMergedRegion(ExcelProcessContext processContext) {
        Sheet sheet = processContext.getSheet();
        List<EasyExcelBlockDefinition> blockDefinitions = processContext.getSheetDefinition().getBlockDefinitions();
        Set<Integer> ignoredAddMergedRangeSet = new HashSet<>(blockDefinitions.size());
        for (EasyExcelBlockDefinition blockDefinition : blockDefinitions) {
            List<EasyExcelBlockDefinition> influenceFillBlockList = blockDefinition.getInfluenceFillBlockList();
            if (CollectionUtils.isNotEmpty(influenceFillBlockList)) {
                for (EasyExcelBlockDefinition influenceBlock : influenceFillBlockList) {
                    ignoredAddMergedRangeSet.add(influenceBlock.getBlockNumber());
                }
            }
        }
        for (EasyExcelBlockDefinition blockDefinition : blockDefinitions) {
            List<ExcelCellRangeDefinition> mergeRangeList = blockDefinition.getMergeRangeList();
            if (CollectionUtils.isNotEmpty(mergeRangeList)) {
                if (ignoredAddMergedRangeSet.contains(blockDefinition.getBlockNumber())) {
                    continue;
                }
                ExcelCellRangeDefinition designRange = blockDefinition.getDesignRange();
                ExcelCellRangeDefinition currentRange = blockDefinition.getCurrentRange();
                int rowOffsetIndex = currentRange.getBeginRowIndex() - designRange.getBeginRowIndex(),
                        columnOffsetIndex = currentRange.getBeginColumnIndex() - designRange.getBeginColumnIndex();
                for (ExcelCellRangeDefinition mergeRange : mergeRangeList) {
                    sheet.addMergedRegion(ExcelDefinitionConverter.convertCellRangeAddress(ExcelCellRangeHelper.translation(mergeRange.clone(), -rowOffsetIndex, -columnOffsetIndex, true)));
                }
            }
        }
    }

    private static void translateProcess(ExcelProcessContext processContext) {
        ExcelDefinitionContext definitionContext = processContext.getDefinitionContext();
        if (!definitionContext.isNeedTranslate()) {
            return;
        }
        for (EasyExcelBlockDefinition blockDefinition : processContext.getSheetDefinition().getBlockDefinitions()) {
            List<ExcelHeaderDefinition> headerDefinitionList = blockDefinition.getHeaderDefinitionList();
            if (CollectionUtils.isNotEmpty(headerDefinitionList)) {
                translateRowDefinition(definitionContext, headerDefinitionList);
            }
            List<ExcelRowDefinition> rowDefinitionList = blockDefinition.getRowDefinitionList();
            if (CollectionUtils.isNotEmpty(rowDefinitionList)) {
                translateRowDefinition(definitionContext, rowDefinitionList);
            }
        }
    }

    private static <T extends ExcelRowDefinition> void translateRowDefinition(ExcelDefinitionContext definitionContext, List<T> rowDefinitionList) {
        for (T rowDefinition : rowDefinitionList) {
            for (ExcelCellDefinition cellDefinition : rowDefinition.getCellList()) {
                cellDefinition.setValue(definitionContext.translate(cellDefinition.getValue()));
                ExcelValueTypeEnum type = cellDefinition.getType();
                if (ExcelValueTypeEnum.BOOLEAN.equals(type) || ExcelValueTypeEnum.ENUMERATION.equals(type)) {
                    Map<String, String> enumerationMap = Optional.ofNullable(cellDefinition.getFormat())
                            .filter(StringUtils::isNotBlank)
                            .map(v -> JSON.<Map<String, String>>parseObject(v, TR_MAP_SS.getType(), Feature.OrderedField))
                            .orElse(null);
                    if (enumerationMap != null) {
                        Map<String, String> translateEnumerationMap = new LinkedHashMap<>(enumerationMap.size());
                        for (Map.Entry<String, String> entry : enumerationMap.entrySet()) {
                            translateEnumerationMap.put(entry.getKey(), definitionContext.translate(entry.getValue()));
                        }
                        cellDefinition.setFormat(JSON.toJSONString(translateEnumerationMap, SerializerFeature.SortField));
                    }
                }
            }
        }
    }

    private static void presetRowProcess(ExcelProcessContext processContext) {
        ExcelSheetDefinition originSheetDefinition = processContext.getOriginSheetDefinition();
        if (originSheetDefinition == null) {
            return;
        }
        EasyExcelSheetDefinition sheetDefinition = processContext.getSheetDefinition();
        List<ExcelBlockDefinition> originBlockDefinitions = originSheetDefinition.getBlockDefinitionList();
        int blockIndex = 0;
        for (EasyExcelBlockDefinition blockDefinition : sheetDefinition.getBlockDefinitions()) {
            ExcelBlockDefinition originBlockDefinition = originBlockDefinitions.get(blockIndex);
            ExcelAnalysisTypeEnum analysisType = blockDefinition.getAnalysisType();
            if (ExcelAnalysisTypeEnum.FIXED_HEADER.equals(analysisType)) {
                ExcelFixedHeaderAnalysisHelper.presetRowProcess(sheetDefinition, blockDefinition, originBlockDefinition);
            }
            blockIndex++;
        }
    }

    private static void importTemplateFillBlock(ExcelProcessContext processContext) {
        Workbook workbook = processContext.getWorkbook();
        Sheet sheet = processContext.getSheet();
        for (EasyExcelBlockDefinition blockDefinition : processContext.getSheetDefinition().getBlockDefinitions()) {
            List<ExcelRowDefinition> allRowDefinitionList = new ArrayList<>();
            List<ExcelHeaderDefinition> headerDefinitionList = blockDefinition.getHeaderDefinitionList();
            if (CollectionUtils.isNotEmpty(headerDefinitionList)) {
                allRowDefinitionList.addAll(headerDefinitionList);
            }
            List<ExcelRowDefinition> rowDefinitionList = blockDefinition.getRowDefinitionList();
            if (CollectionUtils.isNotEmpty(rowDefinitionList)) {
                allRowDefinitionList.addAll(rowDefinitionList);
            }
            ExcelAnalysisHelper.fillCell(workbook, sheet, blockDefinition, EasyExcelBlockDefinition::getCurrentRange, allRowDefinitionList, false);
        }
    }

    private static void exportTemplateFillBlock(ExcelProcessContext processContext) {
        Workbook workbook = processContext.getWorkbook();
        Sheet sheet = processContext.getSheet();
        for (EasyExcelBlockDefinition blockDefinition : processContext.getSheetDefinition().getBlockDefinitions()) {
            List<ExcelRowDefinition> allRowDefinitionList = new ArrayList<>();
            ExcelAnalysisTypeEnum analysisType = blockDefinition.getAnalysisType();
            switch (analysisType) {
                case FIXED_HEADER:
                    allRowDefinitionList.addAll(blockDefinition.getHeaderDefinitionList());
                    allRowDefinitionList.add(blockDefinition.getConfigHeader());
                    break;
                case FIXED_FORMAT:
                    Map<String, EasyExcelCellDefinition> fieldCells = blockDefinition.getFieldCells();
                    List<ExcelRowDefinition> rowDefinitionList = blockDefinition.getRowDefinitionList();
                    for (ExcelRowDefinition rowDefinition : rowDefinitionList) {
                        List<ExcelCellDefinition> cellList = rowDefinition.getCellList();
                        if (CollectionUtils.isNotEmpty(cellList)) {
                            for (ExcelCellDefinition cell : cellList) {
                                String field = cell.getField();
                                if (StringUtils.isBlank(field)) {
                                    continue;
                                }
                                EasyExcelCellDefinition fieldCell = fieldCells.get(field);
                                if (fieldCell == null) {
                                    continue;
                                }
                                cell.setValue(fieldCell.getValue());
                            }
                        }
                        allRowDefinitionList.add(rowDefinition);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid block analysis type. value=" + analysisType);
            }
            ExcelAnalysisHelper.fillCell(workbook, sheet, blockDefinition, EasyExcelBlockDefinition::getDesignRange, allRowDefinitionList, true);
        }
    }

    private static void afterPropertySheet(ExcelProcessContext processContext) {
        Sheet sheet = processContext.getSheet();
        EasyExcelSheetDefinition sheetDefinition = processContext.getSheetDefinition();
        Map<Integer, ExcelStyleDefinition> columnStyles = processContext.getSheetDefinition().getColumnStyles();
        Map<Integer, Boolean> autoSizeColumnMap = new HashMap<>();
        for (EasyExcelBlockDefinition blockDefinition : sheetDefinition.getBlockDefinitions()) {
            ExcelHeaderDefinition configHeader = blockDefinition.getConfigHeader();
            List<ExcelCellDefinition> cellList = configHeader.getCellList();
            int cellSize = cellList.size();
            ExcelCellRangeDefinition designRange = blockDefinition.getDesignRange();
            int begin, end;
            ExcelAnalysisTypeEnum analysisType = blockDefinition.getAnalysisType();
            switch (analysisType) {
                case FIXED_HEADER:
                    ExcelDirectionEnum direction = blockDefinition.getDirection();
                    switch (direction) {
                        case HORIZONTAL:
                            begin = designRange.getBeginColumnIndex();
                            end = designRange.getEndColumnIndex();
                            break;
                        case VERTICAL:
                            begin = designRange.getBeginRowIndex();
                            end = cellSize;
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid excel block direction type. value=" + direction);
                    }
                    break;
                case FIXED_FORMAT:
                    begin = designRange.getBeginColumnIndex();
                    end = cellSize;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid excel block analysis type. value=" + analysisType);
            }
            boolean isAutoSizeColumn = sheetDefinition.getAutoSizeColumn();
            for (int i = begin; i <= end; i++) {
                Boolean acb = autoSizeColumnMap.get(i);
                if (acb != null) {
                    continue;
                }
                Integer width = null;
                ExcelStyleDefinition styleDefinition = columnStyles.get(i);
                if (styleDefinition != null) {
                    width = styleDefinition.getWidth();
                }
                if (width == null) {
                    int realCellIndex = i - begin;
                    if (cellSize > realCellIndex) {
                        ExcelCellDefinition cellDefinition = cellList.get(realCellIndex);
                        isAutoSizeColumn = ObjectHelper.getOrDefault(cellDefinition.getAutoSizeColumn(), isAutoSizeColumn);
                    }
                    if (isAutoSizeColumn) {
                        sheet.autoSizeColumn(i, true);
                    }
                    autoSizeColumnMap.put(i, isAutoSizeColumn);
                } else {
                    sheet.setColumnWidth(i, width);
                }
            }
        }
    }

    private static void sheetMergeRangeProcess(ExcelProcessContext processContext) {
        Sheet sheet = processContext.getSheet();
        EasyExcelSheetDefinition sheetDefinition = processContext.getSheetDefinition();
        List<ExcelCellRangeDefinition> mergeRangeList = sheetDefinition.getMergeRangeList();
        boolean isNotAddMergedRange = true;
        for (ExcelCellRangeDefinition mergeRange : mergeRangeList) {
            if (!Models.modelDirective().isReentry(mergeRange)) {
                isNotAddMergedRange = false;
                break;
            }
        }
        if (isNotAddMergedRange) {
            for (ExcelCellRangeDefinition mergeRange : mergeRangeList) {
                sheet.addMergedRegion(ExcelDefinitionConverter.convertCellRangeAddress(mergeRange));
            }
        } else {
            for (ExcelCellRangeDefinition mergeRange : mergeRangeList) {
                if (Models.modelDirective().isReentry(mergeRange)) {
                    continue;
                }
                sheet.addMergedRegion(ExcelDefinitionConverter.convertCellRangeAddress(mergeRange));
            }
        }
    }

    private static void sheetLoopMergeRangeProcess(ExcelProcessContext processContext) {
        Workbook workbook = processContext.getWorkbook();
        Sheet sheet = processContext.getSheet();
        EasyExcelSheetDefinition sheetDefinition = processContext.getSheetDefinition();
        List<EasyExcelLoopMergeDefinition> loopMergeRangeList = sheetDefinition.getLoopMergeRangeList();
        for (EasyExcelLoopMergeDefinition loopMergeRange : loopMergeRangeList) {
            ExcelCellRangeDefinition mergeRange = loopMergeRange.getMergeRange();
            ExcelStyleDefinition firstStyle = loopMergeRange.getFirstStyle();
            if (firstStyle != null) {
                Row row = WorkbookHelper.getOrCreateRow(sheet, mergeRange.getBeginRowIndex());
                for (int k = mergeRange.getBeginColumnIndex() + 1; k <= mergeRange.getEndColumnIndex(); k++) {
                    Cell cell = WorkbookHelper.getOrCreateCell(row, k);
                    cell.setCellStyle(firstStyle.getOrCreateCellStyle(workbook));
                }
            }
            sheet.addMergedRegion(ExcelDefinitionConverter.convertCellRangeAddress(mergeRange));
        }
    }
}
