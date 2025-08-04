package pro.shushi.pamirs.file.api.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.boot.base.enmu.FileTypeEnum;
import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.boot.web.spi.api.TranslateService;
import pro.shushi.pamirs.boot.web.spi.holder.TranslateServiceHolder;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.URLHelper;
import pro.shushi.pamirs.core.common.function.lambda.PamirsSupplier;
import pro.shushi.pamirs.file.api.config.ExcelConstant;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.context.ExcelExportContext;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.easyexcel.impl.DefaultEasyExcelWriteHandler;
import pro.shushi.pamirs.file.api.enmu.ExcelTaskStateEnum;
import pro.shushi.pamirs.file.api.enmu.TaskMessageLevelEnum;
import pro.shushi.pamirs.file.api.exception.ExcelRuntimeException;
import pro.shushi.pamirs.file.api.exception.ExcelTemplateException;
import pro.shushi.pamirs.file.api.exception.NoDataException;
import pro.shushi.pamirs.file.api.function.ExcelReadCallback;
import pro.shushi.pamirs.file.api.function.impl.BatchImportExcelReadCallback;
import pro.shushi.pamirs.file.api.function.impl.DefaultExcelReadCallback;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.model.TaskMessage;
import pro.shushi.pamirs.file.api.service.ExcelExportService;
import pro.shushi.pamirs.file.api.service.ExcelFileService;
import pro.shushi.pamirs.file.api.service.ExcelImportService;
import pro.shushi.pamirs.file.api.service.ExcelWorkbookDefinitionService;
import pro.shushi.pamirs.file.api.util.*;
import pro.shushi.pamirs.framework.connectors.cdn.client.FileClient;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFile;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFileForm;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;
import pro.shushi.pamirs.trigger.annotation.XAsync;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static pro.shushi.pamirs.file.api.enmu.FileExpEnumerate.SYSTEM_ERROR;

/**
 * Excel文件服务
 *
 * @author Adamancy Zhang at 10:57 on 2021-01-15
 */
@Slf4j
@Base
@Fun(ExcelFileService.FUN_NAMESPACE)
@Service(ExcelFileServiceImpl.BEAN_NAME)
public class ExcelFileServiceImpl implements ExcelFileService {

    public static final String BEAN_NAME = "excelFileServiceImpl";

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private ExcelImportService excelImportService;

    @Autowired
    private ExcelWorkbookDefinitionService excelWorkbookDefinitionService;

    private static final List<String> SUPPORT_SUFFIX = Arrays.asList(ExcelTypeEnum.XLSX.getValue(), ExcelTypeEnum.XLS.getValue(), ExcelTypeEnum.CSV.getValue());

    @Override
    public Workbook doExport(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        return doExport0(exportTask, context, getFileClient());
    }

    @Override
    public void doExportSync(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        FileClient internalFileClient = getFileClient(exportTask);
        if (internalFileClient == null) {
            return;
        }
        FileClient fileClient = new FileClient() {
            @Override
            public CdnFile upload(String fileName, byte[] data) {
                HttpServletResponse response = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                        .map(_req -> (ServletRequestAttributes) _req)
                        .map(ServletRequestAttributes::getResponse)
                        .orElseThrow(() -> PamirsException.construct(SYSTEM_ERROR).appendMsg("未获取到Http响应信息").errThrow());
                try {
                    String _fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
                    response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length));
                    response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + _fileName);
                    ServletOutputStream sos = response.getOutputStream();
                    sos.write(data);
                    sos.flush();
                } catch (IOException exp) {
                    throw PamirsException.construct(SYSTEM_ERROR, exp)
                            .appendMsg("未获取到Http响应信息")
                            .errThrow();
                }

                CdnFile cdnFile = internalFileClient.upload(fileName, data);
                log.info("cdnFile: {}", cdnFile.getUrl());
                return cdnFile;
            }
        };
        doExport0(exportTask, context, fileClient);
    }

    @XAsync(displayName = "导出异步处理", limitRetryNumber = 0)
    @Function
    @Override
    public void doExportAsync(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        FileClient fileClient = getFileClient(exportTask);
        if (fileClient == null) {
            return;
        }
        doExport0(exportTask, context, fileClient);
    }

    protected Workbook doExport0(ExcelExportTask exportTask, ExcelDefinitionContext context, FileClient fileClient) {
        if (context.getCurrentLang() == null) {
            TranslateService translateService = TranslateServiceHolder.get();
            String currentLang = translateService.getCurrentLang();
            context.setCurrentLang(currentLang);
        }
        try {
            return excelExportService.doExport(exportTask, context, (outputStream) -> writeExportTaskFile(exportTask, context, fileClient, outputStream));
        } catch (Throwable e) {
            exportExceptionProcess(exportTask, e);
        } finally {
            updateExportTask(exportTask);
            exportTask.updateById();
        }
        return null;
    }

    protected void exportExceptionProcess(ExcelExportTask exportTask, Throwable e) {
        if (e instanceof ExcelRuntimeException) {
            exportExceptionProcess(exportTask, e.getCause());
            return;
        }
        if (e instanceof NoDataException) {
            exportTask.setState(ExcelTaskStateEnum.FAILURE);
            exportTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "未选择数据，无需导出");
        } else if (e instanceof ExcelTemplateException) {
            exportTask.setState(ExcelTaskStateEnum.FAILURE);
            exportTask.addTaskMessage(TaskMessageLevelEnum.ERROR, ExcelConstant.TEMPLATE_IS_NULL);
        } else {
            log.error("Excel export error.", e);
            exportTask.setState(ExcelTaskStateEnum.FAILURE);
            exportTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "导出数据过程中出现不可预知的异常");
            exportTask.addTaskMessage(TaskMessageLevelEnum.ERROR, EasyExcelHelper.getErrorMessage(e));
        }
    }

    @Override
    public boolean doImport(ExcelImportTask importTask, ExcelDefinitionContext context, PamirsSupplier<ExcelReadCallback> callbackSupplier) {
        return doImport0(importTask, context, callbackSupplier);
    }

    @XAsync(displayName = "导入异步处理", limitRetryNumber = 0)
    @Function
    @Override
    public void doImportAsync(ExcelImportTask importTask, ExcelDefinitionContext context) {
        ExcelWorkbookDefinition workbookDefinition = importTask.getWorkbookDefinition();
        workbookDefinition.analysisSheetDefinitions();
        context.setOriginSheetList(workbookDefinition.getSheetList());
        Boolean eachImport = workbookDefinition.getEachImport();
        boolean isSuccess;
        if (BooleanUtils.isFalse(eachImport)) {
            isSuccess = doImport0(importTask, context, BatchImportExcelReadCallback::new);
        } else {
            isSuccess = doImport0(importTask, context, DefaultExcelReadCallback::new);
        }
        if (!isSuccess) {
            if (PamirsSession.getMessageHub().isSuccess()) {
                PamirsSession.getMessageHub()
                        .error("导入失败，请查看导入记录中的错误信息进行更正");
            }
        }
    }

    @Override
    public ExcelImportTask doImportByUrl(String fileUrl, String templateName, PamirsSupplier<ExcelReadCallback> callbackSupplier) {
        ExcelImportTask importTask = (ExcelImportTask) new ExcelImportTask().setMessages(new ArrayList<>());
        if (StringUtils.isBlank(fileUrl)) {
            importTask.setState(ExcelTaskStateEnum.FAILURE);
            importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "文件路径不存在");
            return importTask;
        }
        ExcelWorkbookDefinition workbookDefinition = new ExcelWorkbookDefinition().setName(templateName).queryOne();
        if (workbookDefinition == null) {
            importTask.setState(ExcelTaskStateEnum.FAILURE);
            importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "找不到导入模板");
            return importTask;
        }
        ExcelDefinitionContext definitionContext = ExcelWorkbookDefinitionUtil.getDefinitionContext(workbookDefinition);
        definitionContext.setCurrentLang(TranslateServiceHolder.get().getCurrentLang());

        importTask.setFile(new PamirsFile().setUrl(fileUrl).setName(templateName).create());

        ExcelWorkbookDefinitionUtil.initImportTask(definitionContext, workbookDefinition, importTask);

        doImport0(importTask, definitionContext, callbackSupplier);
        return importTask;
    }

    @Override
    public ExcelImportTask doImportByUrlTemporary(String fileUrl, ExcelWorkbookDefinition workbookDefinition, PamirsSupplier<ExcelReadCallback> callbackSupplier) {
        ExcelImportTask importTask = (ExcelImportTask) new ExcelImportTask().setMessages(new ArrayList<>());
        if (StringUtils.isBlank(fileUrl)) {
            importTask.setState(ExcelTaskStateEnum.FAILURE);
            importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "文件路径不存在");
            return importTask;
        }
        workbookDefinition = FetchUtil.fetchOne(workbookDefinition);
        if (workbookDefinition == null) {
            importTask.setState(ExcelTaskStateEnum.FAILURE);
            importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "找不到导入模板");
            return importTask;
        }
        ExcelDefinitionContext definitionContext = ExcelWorkbookDefinitionUtil.getDefinitionContext(workbookDefinition);
        definitionContext.setCurrentLang(TranslateServiceHolder.get().getCurrentLang());

        importTask.setFile(new PamirsFile().setUrl(fileUrl));

        ExcelWorkbookDefinitionUtil.initImportTask(definitionContext, workbookDefinition, importTask, Boolean.FALSE);

        doImport0(importTask, definitionContext, callbackSupplier);
        return importTask;
    }

    @Override
    public ExcelDefinitionContext refreshDefinitionContext(ExcelWorkbookDefinition data) {
        Models.origin().fieldQuery(data, ExcelWorkbookDefinition::getLocations);
        RefreshWorkbookDefinitionResult result = refreshDefinitionContext0(data);
        if (result.readyRefreshObject != null) {
            excelWorkbookDefinitionService.update(result.readyRefreshObject);
        }
        return result.definitionContext;
    }

    @Override
    public Boolean refreshDefinitionContextBatch(List<ExcelWorkbookDefinition> workbookDefinitions) {
        if (CollectionUtils.isEmpty(workbookDefinitions)) {
            return Boolean.FALSE;
        }
        Models.origin().listFieldQuery(workbookDefinitions, ExcelWorkbookDefinition::getLocations);
        boolean isAllSuccess = true;
        List<ExcelWorkbookDefinition> readyRefreshObjects = new ArrayList<>();
        for (ExcelWorkbookDefinition workbookDefinition : workbookDefinitions) {
            try {
                ExcelWorkbookDefinition readyRefreshObject = refreshDefinitionContext0(workbookDefinition).readyRefreshObject;
                if (readyRefreshObject != null) {
                    readyRefreshObjects.add(readyRefreshObject);
                }
            } catch (Throwable e) {
                isAllSuccess = false;
                log.error("Refresh excel workbook definition error. model: {}, name: {}", workbookDefinition.getModel(), workbookDefinition.getName(), e);
            }
        }
        if (!readyRefreshObjects.isEmpty()) {
            excelWorkbookDefinitionService.updateBatch(readyRefreshObjects);
        }
        return isAllSuccess;
    }

    @Override
    public void downloadImportTemplate(ExcelWorkbookDefinition data, HttpServletResponse response) throws IOException {
        data.setClearExportStyle(false);
        ExcelDefinitionContext definitionContext = refreshDefinitionContext(data);

        // 设置当前语言
        TranslateService translateService = TranslateServiceHolder.get();
        definitionContext.setCurrentLang(translateService.getCurrentLang());

        // 创建Workbook对象
        Workbook workbook = ExcelWorkbookDefinitionUtil.createImportTemplate(definitionContext);

        // 设置响应头参数
        response.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String filename = ExcelHelper.translateFilename(definitionContext, definitionContext.getFilename());
        int dotIndex = filename.lastIndexOf(CharacterConstants.SEPARATOR_DOT);
        if (dotIndex != -1) {
            String suffix = filename.substring(dotIndex);
            if (SUPPORT_SUFFIX.contains(suffix)) {
                filename = definitionContext.translate(filename.substring(0, dotIndex)).concat(suffix);
            }
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < filename.length(); i++) {
            char c = filename.charAt(i);
            if (c == 32) {
                builder.append(c);
            } else {
                builder.append(URLEncoder.encode(String.valueOf(c), StandardCharsets.UTF_8.name()));
            }
        }
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + builder.toString());

        // 写入Workbook
        workbook.write(response.getOutputStream());
    }

    private RefreshWorkbookDefinitionResult refreshDefinitionContext0(ExcelWorkbookDefinition data) {
        String model = data.getModel();
        if (StringUtils.isBlank(model)) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg("模板模型不允许为空").errThrow();
        }
        String name = data.getName();
        if (StringUtils.isBlank(name)) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg("模版名称不允许为空").errThrow();
        }
        if (data.getSheetList() == null && StringUtils.isBlank(data.getSheetDefinitions())) {
            data = FetchUtil.fetchOne(data);
            if (StringUtils.isBlank(data.getSheetDefinitions())) {
                throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg("无法初始化没有表定义的模板").errThrow();
            }
            data.analysisSheetDefinitions();
        }
        ExcelDefinitionContext definitionContext = ExcelWorkbookDefinitionUtil.getDefinitionContext(data);
        ExcelWorkbookDefinition readyRefreshObject = null;
        if (definitionContext.getIsRefresh()) {
            readyRefreshObject = new ExcelWorkbookDefinition();
            if (log.isInfoEnabled()) {
                log.info("Refresh excel workbook definition. model: {}, name: {}, type: {}", model, name, data.getType());
            }
            readyRefreshObject.setDefinitionContext(data.getDefinitionContext()).setModel(model).setName(name);
        }
        return new RefreshWorkbookDefinitionResult(definitionContext, readyRefreshObject);
    }

    private boolean doImport0(ExcelImportTask importTask, ExcelDefinitionContext context, PamirsSupplier<ExcelReadCallback> callbackSupplier) {
        ExcelImportContext importContext = null;
        String url = importTask.getFile().getUrl();
        url = URLHelper.decode(url);
        url = URLHelper.encodeFileName(url);
        try (BufferedInputStream inputStream = FileUtil.getRemoteBufferedInputStream(url)) {
            importContext = new ExcelImportContext(EasyExcelHelper.generatorReadBuilder(inputStream).build(), context, importTask);
            ExcelWorkbookDefinitionUtil.readImportExcel(importContext, callbackSupplier);
        } catch (Throwable e) {
            if (e instanceof ExcelRuntimeException) {
                e = e.getCause();
            }
            log.error("do import error. url: {}", url, e);
            importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, ExcelConstant.DEFAULT_ERROR_MESSAGE);
            importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, EasyExcelHelper.getErrorMessage(e));
        }
        return updateImportTask(importTask, importContext);
    }

    private boolean generatorErrorFile(ExcelImportTask importTask, ExcelImportContext importContext) {
        boolean isNeedGeneratorErrorFile = false;
        List<List<Map<Integer, String>>> errorDataList = importContext.getErrorDataList();
        for (List<Map<Integer, String>> dataList : errorDataList) {
            if (!dataList.isEmpty()) {
                isNeedGeneratorErrorFile = true;
                break;
            }
        }
        if (isNeedGeneratorErrorFile) {
            generatorErrorFile0(importTask, importContext.getDefinitionContext(), errorDataList);
            importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "请根据错误数据后面的提示内容进行修正");
        }
        return isNeedGeneratorErrorFile;
    }

    @SuppressWarnings("unchecked")
    private void generatorErrorFile0(ExcelImportTask importTask, ExcelDefinitionContext context, List<List<Map<Integer, String>>> errorDataList) {
        Result<List<List<Map<String, String>>>> result = ExcelImportErrorFileHelper.generatorErrorFile(context).get(errorDataList);
        if (!result.isSuccess()) {
            importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "错误信息收集失败");
            importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, result.getErrorMessage());
            return;
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            FileClient fileClient = FileClientFactory.getClient();
            DefaultEasyExcelWriteHandler writeHandler = new DefaultEasyExcelWriteHandler(context);
            ExcelWorkbookDefinition workbookDefinition = importTask.getWorkbookDefinition();
            workbookDefinition.setClearExportStyle(false);
            CdnFileForm formData = fileClient.getFormData(ExcelHelper.generatorFilename(workbookDefinition, ExcelConstant.DEFAULT_ERROR_FILE_SUFFIX));
            ExcelExportTask exportTask = (ExcelExportTask) new ExcelExportTask()
                    .setFile(new PamirsFile().setName(formData.getFileName())
                            .setUrl(formData.getDownloadUrl())
                            .setType(FileTypeEnum.URL))
                    .setWorkbookDefinition(workbookDefinition)
                    .setWorkbookName(workbookDefinition.getName());
            Workbook workbook = ExcelWorkbookDefinitionUtil.createExportTemplate(context);
            workbook.write(outputStream);
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
                try (ByteArrayOutputStream bufferOutputStream = new ByteArrayOutputStream()) {
                    ExcelExportContext exportContext = new ExcelExportContext(EasyExcelHelper.generatorWriteBuilder(bufferOutputStream, inputStream, writeHandler).build(), context, exportTask);
                    exportContext.setDataList((List<Object>) (Object) result.getData());
                    ExcelWorkbookDefinitionUtil.fillTemplate(exportContext);
                    PamirsFile errorFile = exportTask.getFile();
                    fileClient.upload(errorFile.getName(), bufferOutputStream.toByteArray());
                    errorFile.create();
                    importTask.setErrorFile(errorFile);
                    importTask.updateById();
                }
            }
        } catch (Throwable e) {
            log.error("错误信息收集失败: importTaskId: {}", importTask.getId(), e);
            importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "错误信息收集失败");
            importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, EasyExcelHelper.getErrorMessage(e));
        }
    }

    private FileClient getFileClient() {
        FileClient fileClient = FileClientFactory.getClient();
        if (fileClient == null) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg("未找到文件服务器，无法执行导出").errThrow();
        }
        return fileClient;
    }

    private FileClient getFileClient(ExcelExportTask exportTask) {
        FileClient fileClient = FileClientFactory.getClient();
        if (fileClient == null) {
            exportTask.setState(ExcelTaskStateEnum.FAILURE);
            exportTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "未找到文件服务器，无法执行导出");
            exportTask.updateById();
            return null;
        }
        return fileClient;
    }

    protected void writeExportTaskFile(ExcelExportTask exportTask, ExcelDefinitionContext context, FileClient fileClient, ByteArrayOutputStream outputStream) {
        if (updateExportTask(exportTask)) {
            PamirsFile file = exportTask.getFile();
            fileClient.upload(file.getName(), outputStream.toByteArray());
            file.create();
        }
        exportTask.updateById();
    }

    private boolean updateImportTask(ExcelImportTask importTask, ExcelImportContext importContext) {
        boolean isSuccess;
        ExcelTaskStateEnum state = importTask.getState();
        if (ExcelTaskStateEnum.PROCESSING.equals(state)) {
            isSuccess = !generatorErrorFile(importTask, importContext);
        } else {
            isSuccess = ExcelTaskStateEnum.SUCCESS.equals(state);
            if (!isSuccess) {
                generatorErrorFile(importTask, importContext);
            }
        }
        if (isSuccess) {
            isSuccess = searchErrorMessage(importTask.getMessages());
        }
        if (isSuccess) {
            importTask.setState(ExcelTaskStateEnum.SUCCESS)
                    .addTaskMessage(TaskMessageLevelEnum.INFO, "导入成功");
        } else {
            importTask.setState(ExcelTaskStateEnum.FAILURE)
                    .addTaskMessage(TaskMessageLevelEnum.ERROR, "导入失败");
        }
        if (importTask.getId() != null) {
            importTask.updateById();
        }
        return isSuccess;
    }

    private boolean updateExportTask(ExcelExportTask exportTask) {
        boolean isSuccess;
        ExcelTaskStateEnum state = exportTask.getState();
        if (ExcelTaskStateEnum.PROCESSING.equals(state)) {
            if (searchErrorMessage(exportTask.getMessages())) {
                isSuccess = true;
                exportTask.setState(ExcelTaskStateEnum.SUCCESS)
                        .addTaskMessage(TaskMessageLevelEnum.INFO, "导出成功");
            } else {
                isSuccess = false;
                exportTask.setState(ExcelTaskStateEnum.FAILURE)
                        .addTaskMessage(TaskMessageLevelEnum.ERROR, "导出失败");
            }
        } else {
            isSuccess = ExcelTaskStateEnum.SUCCESS.equals(state);
        }
        return isSuccess;
    }

    private boolean searchErrorMessage(List<TaskMessage> messages) {
        if (CollectionUtils.isNotEmpty(messages)) {
            ListIterator<TaskMessage> listIterator = messages.listIterator(messages.size());
            while (listIterator.hasPrevious()) {
                if (TaskMessageLevelEnum.ERROR.equals(listIterator.previous().getLevel())) {
                    return false;
                }
            }
        }
        return true;
    }

    private static class RefreshWorkbookDefinitionResult {

        private final ExcelDefinitionContext definitionContext;

        private final ExcelWorkbookDefinition readyRefreshObject;

        public RefreshWorkbookDefinitionResult(ExcelDefinitionContext definitionContext, ExcelWorkbookDefinition readyRefreshObject) {
            this.definitionContext = definitionContext;
            this.readyRefreshObject = readyRefreshObject;
        }
    }
}
