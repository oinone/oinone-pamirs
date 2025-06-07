package pro.shushi.pamirs.file.api.util.analysis;

import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.ExcelCellDefinition;
import pro.shushi.pamirs.file.api.model.ExcelCellRangeDefinition;
import pro.shushi.pamirs.file.api.model.ExcelHeaderDefinition;
import pro.shushi.pamirs.file.api.model.ExcelStyleDefinition;
import pro.shushi.pamirs.file.api.util.ExcelDefinitionConverter;

import java.util.List;
import java.util.Map;

/**
 * 固定格式{@link ExcelAnalysisTypeEnum#FIXED_FORMAT}解析类型帮助类
 *
 * @author Adamancy Zhang at 15:29 on 2021-08-14
 */
public class ExcelFixedFormatAnalysisHelper {

    private ExcelFixedFormatAnalysisHelper() {
        //reject create object
    }

    /**
     * 配置行样式处理
     *
     * @param easyExcelSheetDefinition EasyExcel工作表定义
     * @param easyExcelBlockDefinition EasyExcel块定义
     */
    public static void configHeaderStyleProcess(EasyExcelSheetDefinition easyExcelSheetDefinition, EasyExcelBlockDefinition easyExcelBlockDefinition) {
        ExcelCellRangeDefinition designRange = easyExcelBlockDefinition.getDesignRange();
        int beginColumnIndex = designRange.getBeginColumnIndex(),
                endColumnIndex = designRange.getEndColumnIndex();
        ExcelHeaderDefinition configHeader = easyExcelBlockDefinition.getConfigHeader();
        List<ExcelCellDefinition> cellList = configHeader.getCellList();
        ExcelStyleDefinition headerStyle = configHeader.getStyle();
        Boolean usingCascadingStyle = easyExcelBlockDefinition.getUsingCascadingStyle();
        Map<Integer, ExcelStyleDefinition> columnStyles = easyExcelSheetDefinition.getColumnStyles();
        ExcelStyleDefinition styleDefinition;
        int cellSize = cellList.size();
        int k = 0;
        for (int i = beginColumnIndex; i <= endColumnIndex; i++) {
            styleDefinition = ExcelDefinitionConverter.mergeCellStyleDefinition(columnStyles.get(i), headerStyle, usingCascadingStyle);
            if (k < cellSize) {
                ExcelCellDefinition cellDefinition = cellList.get(k);
                styleDefinition = ExcelDefinitionConverter.mergeCellStyleDefinition(styleDefinition, cellDefinition.getStyle(), usingCascadingStyle);
                if (styleDefinition != null) {
                    styleDefinition = styleDefinition.clone();
                    cellDefinition.setStyle(styleDefinition);
                }
            } else {
                styleDefinition = styleDefinition.clone();
            }
            columnStyles.putIfAbsent(i, styleDefinition);
            k++;
        }
    }
}
