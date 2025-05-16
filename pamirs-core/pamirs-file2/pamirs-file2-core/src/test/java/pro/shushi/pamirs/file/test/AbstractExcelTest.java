package pro.shushi.pamirs.file.test;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.poi.ss.usermodel.Workbook;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.core.common.function.lambda.PamirsSupplier;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.context.ExcelExportContext;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.easyexcel.ExcelAnalysisEventListener;
import pro.shushi.pamirs.file.api.easyexcel.impl.DefaultEasyExcelWriteHandler;
import pro.shushi.pamirs.file.api.enmu.ExcelTaskStateEnum;
import pro.shushi.pamirs.file.api.enmu.TaskMessageLevelEnum;
import pro.shushi.pamirs.file.api.function.ExcelReadCallback;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.util.ExcelImportErrorFileHelper;
import pro.shushi.pamirs.file.api.util.ExcelWorkbookDefinitionUtil;
import pro.shushi.pamirs.middleware.schedule.common.Result;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Excel导入/导出抽象测试类
 *
 * @author Adamancy Zhang at 10:28 on 2021-08-13
 */
public abstract class AbstractExcelTest {

    protected abstract ExcelWorkbookDefinition mockDefinition();

    protected abstract void appendMockData(ExcelExportContext exportContext);

    protected void appendErrorMockData(ExcelExportContext exportContext) {

    }

    protected void testImportTemplate() throws IOException {
        ExcelWorkbookDefinition workbookDefinition = mockDefinition();

        workbookDefinition.storeSheetDefinitions();

        ExcelDefinitionContext context = getDefinitionContext(workbookDefinition);

        Workbook workbook = ExcelWorkbookDefinitionUtil.createImportTemplate(context);

        try (FileOutputStream outputStream = new FileOutputStream(TestConstant.IMPORT_TEMPLATE_PATH)) {
            workbook.write(outputStream);
        }
        System.out.println("导入模板生成成功");
    }

    protected void testExportTemplate() throws IOException {
        ExcelWorkbookDefinition workbookDefinition = mockDefinition();

        workbookDefinition.storeSheetDefinitions();

        ExcelDefinitionContext context = getDefinitionContext(workbookDefinition);

        Workbook workbook = ExcelWorkbookDefinitionUtil.createExportTemplate(context);

        try (FileOutputStream outputStream = new FileOutputStream(TestConstant.EXPORT_TEMPLATE_PATH)) {
            workbook.write(outputStream);
        }
        System.out.println("EasyExcel导出模板生成成功");
    }

    protected void fillTemplateData() throws IOException {
        ExcelWorkbookDefinition workbookDefinition = mockDefinition();

        workbookDefinition.storeSheetDefinitions();

        ExcelDefinitionContext context = getDefinitionContext(workbookDefinition);

        OutputStream outputStream = new FileOutputStream(TestConstant.EXPORT_PATH);
        OutputStream bufferOutputStream = new BufferedOutputStream(outputStream, 8192 * 4);

        ExcelExportContext exportContext = new ExcelExportContext(EasyExcel.write(bufferOutputStream)
                .withTemplate(TestConstant.EXPORT_TEMPLATE_PATH)
                .registerWriteHandler(new DefaultEasyExcelWriteHandler(context))
                .build(), context, new ExcelExportTask());

//        ExcelExportContext exportContext = null;
//
//        Workbook workbook = ExcelWorkbookDefinitionUtil.createExportTemplate(context);
//
//        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//            workbook.write(outputStream);
//            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
//                exportContext = new ExcelExportContext(EasyExcel.write(TestConstant.EXPORT_PATH)
//                        .withTemplate(inputStream)
//                        .registerWriteHandler(new DefaultEasyExcelWriteHandler(context))
//                        .build(), context, new ExcelExportTask());
//            }
//        }

        appendMockData(exportContext);

        ExcelWorkbookDefinitionUtil.fillTemplate(exportContext);
    }

    protected void testImportData() {
        ExcelWorkbookDefinition workbookDefinition = mockDefinition();

        workbookDefinition.storeSheetDefinitions();

        ExcelDefinitionContext context = getDefinitionContext(workbookDefinition);

        ExcelImportTask importTask = new ExcelImportTask();
        importTask.setEachImport(true)
                .setMaxErrorLength(100)
                .setWorkbookDefinition(workbookDefinition)
                .setWorkbookName(workbookDefinition.getName())
                .setState(ExcelTaskStateEnum.PROCESSING);

        ExcelImportContext importContext = new ExcelImportContext(EasyExcel.read(TestConstant.EXPORT_PATH).build(), context, importTask);

        ExcelWorkbookDefinitionUtil.readImportExcel(importContext, TestExcelReadCallback::new);
    }

    protected void fillErrorTemplateData() throws IOException {
        ExcelWorkbookDefinition workbookDefinition = mockDefinition();

        workbookDefinition.storeSheetDefinitions();

        ExcelDefinitionContext context = getDefinitionContext(workbookDefinition);

        OutputStream outputStream = new FileOutputStream(TestConstant.EXPORT_PATH);
        OutputStream bufferOutputStream = new BufferedOutputStream(outputStream, 8192 * 4);

        ExcelExportContext exportContext = new ExcelExportContext(EasyExcel.write(bufferOutputStream)
                .withTemplate(TestConstant.EXPORT_TEMPLATE_PATH)
                .registerWriteHandler(new DefaultEasyExcelWriteHandler(context))
                .build(), context, new ExcelExportTask());

//        ExcelExportContext exportContext = null;
//
//        Workbook workbook = ExcelWorkbookDefinitionUtil.createExportTemplate(context);
//
//        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//            workbook.write(outputStream);
//            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
//                exportContext = new ExcelExportContext(EasyExcel.write(TestConstant.EXPORT_PATH)
//                        .withTemplate(inputStream)
//                        .registerWriteHandler(new DefaultEasyExcelWriteHandler(context))
//                        .build(), context, new ExcelExportTask());
//            }
//        }

        appendErrorMockData(exportContext);

        ExcelWorkbookDefinitionUtil.fillTemplate(exportContext);
    }

    protected void testErrorImportData(PamirsSupplier<ExcelReadCallback> callback) throws FileNotFoundException {
        ExcelWorkbookDefinition workbookDefinition = mockDefinition();

        workbookDefinition.storeSheetDefinitions();

        ExcelDefinitionContext context = getDefinitionContext(workbookDefinition);

        ExcelImportTask importTask = new ExcelImportTask();
        importTask.setEachImport(true)
                .setMaxErrorLength(100)
                .setWorkbookDefinition(workbookDefinition)
                .setWorkbookName(workbookDefinition.getName())
                .setState(ExcelTaskStateEnum.PROCESSING);

        ExcelImportContext importContext = new ExcelImportContext(EasyExcel.read(TestConstant.EXPORT_PATH).build(), context, importTask);

        ExcelWorkbookDefinitionUtil.readImportExcel(importContext, callback);

        System.out.println(JSON.toJSONString(importTask.getMessages(), SerializerFeature.PrettyFormat));

        System.out.println(JSON.toJSONString(importContext.getErrorDataList(), SerializerFeature.PrettyFormat));

        testGeneratorErrorFile(importTask, context, importContext.getErrorDataList());
    }

    @SuppressWarnings("unchecked")
    protected void testGeneratorErrorFile(ExcelImportTask importTask, ExcelDefinitionContext context, List<List<Map<Integer, String>>> errorDataList) throws FileNotFoundException {
        Result<List<List<Map<String, String>>>> result = ExcelImportErrorFileHelper.generatorErrorFile(context).get(errorDataList);
        if (!result.isSuccess()) {
            importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "错误信息收集失败");
            importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, result.getErrorMessage());
            return;
        }
        OutputStream outputStream = new FileOutputStream(TestConstant.ERROR_IMPORT_PATH);
        OutputStream bufferOutputStream = new BufferedOutputStream(outputStream, 8192 * 4);

        ExcelExportContext exportContext = new ExcelExportContext(EasyExcel.write(bufferOutputStream)
                .withTemplate(TestConstant.EXPORT_TEMPLATE_PATH)
                .registerWriteHandler(new DefaultEasyExcelWriteHandler(context))
                .build(), context, new ExcelExportTask());

        exportContext.setDataList((List<Object>) (Object) result.getData());

        ExcelWorkbookDefinitionUtil.fillTemplate(exportContext);
    }

    protected ExcelDefinitionContext getDefinitionContext(ExcelWorkbookDefinition workbookDefinition) {
        return ExcelWorkbookDefinitionUtil.getDefinitionContext(workbookDefinition);
    }

    public static class TestExcelReadCallback implements ExcelReadCallback {

        private Map<String, Object> onceData;

        private final AtomicInteger aci = new AtomicInteger(0);

        @Override
        public void process(ExcelImportContext importContext, String modelModel, Map<String, Object> data) {
            ExcelAnalysisEventListener listener = importContext.getCurrentListener();
            if (listener.getCurrentSheet().getOnceFetchData()) {
                if (listener.hasNext()) {
                    if (onceData == null) {
                        onceData = data;
                    } else if (data != null) {
                        MapHelper.deepMerge(onceData, data);
                    }
                    return;
                } else {
                    if (data == null) {
                        data = onceData;
                    } else {
                        if (onceData != null) {
                            MapHelper.deepMerge(onceData, data);
                            data = onceData;
                        }
                    }
                }
            }
            System.out.println("count: " + aci.incrementAndGet() + "; data: " + JSON.toJSONString(data));
        }
    }
}
