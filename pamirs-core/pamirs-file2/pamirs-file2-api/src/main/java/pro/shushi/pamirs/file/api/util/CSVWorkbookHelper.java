package pro.shushi.pamirs.file.api.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.file.api.config.FileConstant;
import pro.shushi.pamirs.file.api.config.FileProperties;
import pro.shushi.pamirs.file.api.context.CSVExportContext;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.enmu.TaskMessageLevelEnum;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelCellDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.ExcelCellDefinition;
import pro.shushi.pamirs.file.api.model.ExcelCellRangeDefinition;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelHeaderDefinition;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.resource.api.model.ResourceMajorConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Adamancy Zhang
 * @date 2021-01-21 19:31
 */
public class CSVWorkbookHelper {

    private CSVWorkbookHelper() {
        //reject create object
    }

    /**
     * 第一阶段仅实现【固定表头-横向排列】导出功能
     *
     * @param exportContext 导出上下文
     * @throws IOException I/O exception
     */
    public static void fillCSV(CSVExportContext exportContext) throws IOException {
        List<Object> objects = exportContext.getDataList();
        int maxBlockNumber = objects.size();
        if (maxBlockNumber == 0) {
            return;
        }
        ExcelDefinitionContext definitionContext = exportContext.getDefinitionContext();
        int blockNumber = 0;
        List<List<String>> rowList = new ArrayList<>();
        // CSV格式不支持分sheet导出，只取第一个sheet定义
        EasyExcelSheetDefinition sheetDefinition = definitionContext.getSheetList().get(0);
        for (EasyExcelBlockDefinition blockDefinition : sheetDefinition.getBlockDefinitions()) {
            if (blockNumber >= maxBlockNumber) {
                break;
            }
            Object object = objects.get(blockNumber);
            if (object == null) {
                continue;
            }
            fillCSV0(exportContext, blockDefinition, rowList, object);
            blockNumber++;
        }
        writeData(exportContext.getOutput(), rowList, Charset.forName(Optional.ofNullable(new ResourceMajorConfig().singletonModel())
                .map(ResourceMajorConfig::getDefaultCsvCharset)
                .filter(StringUtils::isNotBlank)
                .orElse(FileConstant.CSV_EXPORT_CHARSET)));
    }

    private static void fillCSV0(CSVExportContext exportContext, EasyExcelBlockDefinition blockDefinition, List<List<String>> rowList, Object data) {
        ExcelDefinitionContext definitionContext = exportContext.getDefinitionContext();
        List<Map<String, Object>> targets;
        if (data instanceof List) {
            //noinspection unchecked
            targets = DataConvertHelper.convertDataByList(definitionContext, blockDefinition, (List<Object>) data);
        } else {
            if (data == null) {
                return;
            }
            targets = DataConvertHelper.convertDataByObject(definitionContext, blockDefinition, data);
        }
        ExcelExportTask exportTask = exportContext.getExportTask();
        Integer csvMaxSupportLength = exportTask.getWorkbookDefinition().getCsvMaxSupportLength();
        if (csvMaxSupportLength == null || csvMaxSupportLength <= 0) {
            csvMaxSupportLength = BeanDefinitionUtils.getBean(FileProperties.class).getExportProperty().getCsvMaxSupportLength();
        }
        if (targets.size() > csvMaxSupportLength) {
            exportTask.addTaskMessage(TaskMessageLevelEnum.ERROR, I18nUtils.getMessage("ExcelExportHelper.csv_max_support_length", csvMaxSupportLength));
            return;
        }
        fillHeader(exportContext, blockDefinition, rowList);
        fillBody(exportContext, blockDefinition, rowList, targets);
    }

    private static void fillHeader(CSVExportContext exportContext, EasyExcelBlockDefinition blockDefinition, List<List<String>> rowList) {
        ExcelCellRangeDefinition designRange = blockDefinition.getDesignRange();
        int beginColumnIndex = designRange.getBeginColumnIndex(),
                endColumnIndex = designRange.getEndColumnIndex(),
                beginRowIndex = designRange.getBeginRowIndex(),
                endRowIndex = designRange.getEndRowIndex();
        exportContext.setCurrentRowIndex(beginRowIndex);
        exportContext.setCurrentColumnIndex(beginColumnIndex);
        int currentRowIndex = beginRowIndex;
        List<ExcelHeaderDefinition> headerDefinitionList = blockDefinition.getHeaderDefinitionList();
        if (CollectionUtils.isNotEmpty(headerDefinitionList)) {
            for (ExcelHeaderDefinition headerDefinition : headerDefinitionList) {
                if (currentRowIndex >= endRowIndex) {
                    break;
                }
                if (headerDefinition.getIsConfig()) {
                    continue;
                }
                List<String> columnList = CollectionHelper.getAndAddNewInstance(rowList, currentRowIndex, ArrayList::new);
                List<ExcelCellDefinition> cellList = headerDefinition.getCellList();
                for (int currentColumnIndex = beginColumnIndex; currentColumnIndex <= endColumnIndex; currentColumnIndex++) {
                    while (columnList.size() <= currentColumnIndex) {
                        columnList.add(null);
                    }
                    ExcelCellDefinition cellDefinition = cellList.get(currentColumnIndex);
                    if (cellDefinition == null) {
                        continue;
                    }
                    exportContext.setCurrentColumnIndex(currentColumnIndex);
                    columnList.set(currentColumnIndex, cellDefinition.getValue());
                }
                currentRowIndex++;
                exportContext.setCurrentRowIndex(currentRowIndex);
            }
        }
    }

    private static void fillBody(CSVExportContext exportContext, EasyExcelBlockDefinition blockDefinition, List<List<String>> rowList, List<Map<String, Object>> targets) {
        ExcelCellRangeDefinition designRange = blockDefinition.getDesignRange();
        int beginColumnIndex = designRange.getBeginColumnIndex(),
                endColumnIndex = designRange.getEndColumnIndex(),
                beginRowIndex = designRange.getBeginRowIndex(),
                endRowIndex = designRange.getEndRowIndex();
        int currentRowIndex = exportContext.getCurrentRowIndex(), maxRowIndex = endRowIndex + targets.size();
        Map<Integer, EasyExcelCellDefinition> columnFieldCells = blockDefinition.getColumnFieldCells();
        for (Map<String, Object> target : targets) {
            if (currentRowIndex >= maxRowIndex) {
                break;
            }
            List<String> columnList = CollectionHelper.getAndAddNewInstance(rowList, currentRowIndex, ArrayList::new);
            for (int currentColumnIndex = beginColumnIndex; currentColumnIndex <= endColumnIndex; currentColumnIndex++) {
                while (columnList.size() <= currentColumnIndex) {
                    columnList.add(null);
                }
                EasyExcelCellDefinition columnFieldCell = columnFieldCells.get(currentColumnIndex);
                if (columnFieldCell == null) {
                    continue;
                }
                exportContext.setCurrentColumnIndex(currentColumnIndex);
                columnList.set(currentColumnIndex, getFieldValue(target, exportContext, blockDefinition, columnFieldCell));
            }
            currentRowIndex++;
            exportContext.setCurrentColumnIndex(currentRowIndex);
        }
    }

    private static String getFieldValue(Map<String, Object> target, CSVExportContext exportContext, EasyExcelBlockDefinition blockDefinition, EasyExcelCellDefinition columnFieldCell) {
        return "\"" + StringHelper.valueOf(target.get(columnFieldCell.getKey())) + "\"";
    }

    private static void writeData(OutputStream output, List<List<String>> rowList, Charset charset) throws IOException {
        try (OutputStreamWriter outputWriter = new OutputStreamWriter(output, charset)) {
            int i = 0;
            for (List<String> rowData : rowList) {
                outputWriter.write(StringHelper.join(",", rowData) + "\r\n");
                outputWriter.flush();
                rowList.set(i++, null);
            }
            rowList.clear();
        }
    }
}
