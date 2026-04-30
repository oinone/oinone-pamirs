package pro.shushi.pamirs.file.api.executor.impl;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.config.FileProperties;
import pro.shushi.pamirs.file.api.context.CSVExportContext;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.context.ExcelExportContext;
import pro.shushi.pamirs.file.api.easyexcel.impl.DefaultEasyExcelWriteHandler;
import pro.shushi.pamirs.file.api.enmu.ExcelExportStrategyEnum;
import pro.shushi.pamirs.file.api.enmu.TaskMessageLevelEnum;
import pro.shushi.pamirs.file.api.executor.ExcelExportExecutor;
import pro.shushi.pamirs.file.api.extpoint.ExcelExportFetchDataExtPoint;
import pro.shushi.pamirs.file.api.model.ExcelBlockDefinition;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.util.CSVWorkbookHelper;
import pro.shushi.pamirs.file.api.util.EasyExcelHelper;
import pro.shushi.pamirs.file.api.util.ExcelWorkbookDefinitionUtil;
import pro.shushi.pamirs.meta.api.Ext;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 标准Excel导出执行器
 *
 * @author Adamancy Zhang at 18:39 on 2024-03-28
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
        if (dataList == null || dataList.isEmpty()) {
            dataList = new ArrayList<>();
            List<ExcelSheetDefinition> originSheetList = context.getOriginSheetList();
            for (ExcelSheetDefinition sheetDefinition : originSheetList) {
                List<ExcelBlockDefinition> blockDefinitionList = sheetDefinition.getBlockDefinitionList();
                for (ExcelBlockDefinition blockDefinition : blockDefinitionList) {
                    switch (blockDefinition.getAnalysisType()) {
                        case FIXED_HEADER:
                            List<Object> blockData = new ArrayList<>();
                            blockData.add(new HashMap<>());
                            dataList.add(blockData);
                            break;
                        case FIXED_FORMAT:
                            dataList.add(new HashMap<>());
                            break;
                        default:
                            break;
                    }
                }
            }
        }

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
}
