package pro.shushi.pamirs.file.api.executor.impl;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.config.FileConstant;
import pro.shushi.pamirs.file.api.config.FileProperties;
import pro.shushi.pamirs.file.api.context.CSVExportContext;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.context.ExcelExportContext;
import pro.shushi.pamirs.file.api.easyexcel.impl.DefaultEasyExcelWriteHandler;
import pro.shushi.pamirs.file.api.enmu.ExcelExportStrategyEnum;
import pro.shushi.pamirs.file.api.enmu.TaskMessageLevelEnum;
import pro.shushi.pamirs.file.api.executor.ExcelExportExecutor;
import pro.shushi.pamirs.file.api.extpoint.ExcelExportFetchDataExtPoint;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.util.CSVWorkbookHelper;
import pro.shushi.pamirs.file.api.util.EasyExcelHelper;
import pro.shushi.pamirs.file.api.util.ExcelWorkbookDefinitionUtil;
import pro.shushi.pamirs.meta.api.Ext;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 标准Excel导出执行器
 *
 * @author Adamancy Zhang at 18:39 on 2024-03-28
 * @author wangxian at 21:19 on 2026-04-28
 */
@Order
@Component
@SPI.Service(ExcelExportStrategyEnum.standard)
public class StandardExcelExportExecutor extends AbstractExcelExportExecutor implements ExcelExportExecutor {

    @Autowired
    private FileProperties fileProperties;

    @Override
    public Workbook doExport(ExcelExportTask exportTask, ExcelDefinitionContext context, StreamConsumer<ByteArrayOutputStream> consumer) throws IOException {
        List<Object> dataList = fetchExportData(exportTask, context);
        ExcelWorkbookDefinition workbookDefinition = exportTask.getWorkbookDefinition();
        if (BooleanUtils.isTrue(workbookDefinition.getClearExportStyle())) {
            doExport0ByCSV(exportTask, context, dataList, consumer);
            return null;
        } else {
            try {
                return doExport0ByPOI(exportTask, context, dataList, consumer);
            } catch (OutOfMemoryError e) {
                exportTask.addTaskMessage(TaskMessageLevelEnum.WARNING, "导出数据量过大，自动尝试转为CSV格式进行导出");
                workbookDefinition.setClearExportStyle(true);
                doExport0ByCSV(exportTask, context, dataList, consumer);
            }
        }
        return null;
    }

    protected List<Object> fetchExportData(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        List<Object> dataList = singleFetchExportData(exportTask, context);
        if (dataList == null) {
            return null;
        }
        // 数据为空时不抛出异常，返回空列表，由 doExport0ByPOI 生成只含表头的文件
        return dataList;
    }

    /**
     * 一次性获取全部导出数据
     *
     * @param exportTask 导出任务
     * @param context    Excel定义上下文
     * @return 全部导出数据
     */
    protected List<Object> singleFetchExportData(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        return Ext.run(ExcelExportFetchDataExtPoint::fetchExportData, exportTask, context);
    }

    private Workbook doExport0ByPOI(ExcelExportTask exportTask, ExcelDefinitionContext context, List<Object> dataList, StreamConsumer<ByteArrayOutputStream> consumer) throws IOException {
        // 数据为空时，绕过 EasyExcel，直接用 POI 删除占位符行后输出
        // 原因：EasyExcel fill 空列表时 forceNewRow=true 不会删除模板行，导致 {data0.xxx} 占位符残留
        if (dataList != null && dataList.isEmpty()) {
            Workbook workbook = ExcelWorkbookDefinitionUtil.createExportTemplate(context);
            removePlaceholderRows(workbook);
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                consumer.accept(outputStream);
            }
            return workbook;
        }
        return withTemplateStream(context, (inputStream) -> {
            DefaultEasyExcelWriteHandler writeHandler = new DefaultEasyExcelWriteHandler(context);
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ExcelExportContext exportContext = new ExcelExportContext(EasyExcelHelper.generatorWriteBuilder(outputStream, inputStream, writeHandler).build(), context, exportTask);
                exportContext.setDataList(dataList);
                ExcelWorkbookDefinitionUtil.fillTemplate(exportContext);
                consumer.accept(outputStream);
            }
        });
    }

    private void doExport0ByCSV(ExcelExportTask exportTask, ExcelDefinitionContext context, List<Object> dataList, StreamConsumer<ByteArrayOutputStream> consumer) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            CSVExportContext exportContext = new CSVExportContext(outputStream, context, exportTask);
            exportContext.setDataList(dataList);
            CSVWorkbookHelper.fillCSV(exportContext);
            consumer.accept(outputStream);
        }
    }

    /**
     * 删除 workbook 中包含 EasyExcel 填充占位符（如 {data0.xxx}）的行
     */
    private static void removePlaceholderRows(Workbook workbook) {
        String placeholderPrefix = "{" + FileConstant.BLOCK_PREFIX;
        for (int sheetIdx = 0; sheetIdx < workbook.getNumberOfSheets(); sheetIdx++) {
            Sheet sheet = workbook.getSheetAt(sheetIdx);
            // 从最后一行往前遍历，避免删除后行号偏移
            for (int rowIdx = sheet.getLastRowNum(); rowIdx >= 0; rowIdx--) {
                Row row = sheet.getRow(rowIdx);
                if (row != null && isPlaceholderRow(row, placeholderPrefix)) {
                    sheet.removeRow(row);
                }
            }
        }
    }

    /**
     * 判断该行是否包含 EasyExcel 填充占位符（如 {data0.xxx}）
     */
    private static boolean isPlaceholderRow(Row row, String placeholderPrefix) {
        for (Cell cell : row) {
            if (cell == null || cell.getCellType() != CellType.STRING) {
                continue;
            }
            String value = cell.getStringCellValue();
            if (value != null && value.contains(placeholderPrefix)) {
                return true;
            }
        }
        return false;
    }
}
