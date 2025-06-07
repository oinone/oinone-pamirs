package pro.shushi.pamirs.file.api.easyexcel.impl;

import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.ExcelCellDefinition;
import pro.shushi.pamirs.file.api.model.ExcelRowDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <h>默认EasyExcel模板写入处理器</h>
 *
 * @author Adamancy Zhang at 11:53 on 2023-09-07
 */
public class DefaultEasyExcelTemplateWriteHandler extends AbstractEasyExcelBlockRangeWriteHandler implements RowWriteHandler, CellWriteHandler {

    private final List<List<List<CellStyle>>> currentStyles;

    private boolean init = false;

    public DefaultEasyExcelTemplateWriteHandler(EasyExcelSheetDefinition currentSheet) {
        super(currentSheet);
        this.currentStyles = new ArrayList<>();
    }

    @Override
    public void beforeRowCreate(RowWriteHandlerContext context) {
        if (!init) {
            init = true;
            this.init(context.getWriteWorkbookHolder().getWorkbook());
        }
    }


    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        int columnIndex = context.getColumnIndex();
        int relativeRowIndex = this.getRelativeRowIndex(context.getRowIndex(), context.getRelativeRowIndex());

        List<List<CellStyle>> blockCellStyles = null;
        if (this.refreshRange(context)) {
            blockCellStyles = this.currentStyles.get(this.currentRangeIndex);
        }

        if (blockCellStyles == null) {
            return;
        }

        List<CellStyle> rowCellStyles = null;
        int rowCellStyleIndex = relativeRowIndex - this.currentRange.getBeginRowIndex();
        if (0 <= rowCellStyleIndex && rowCellStyleIndex < blockCellStyles.size()) {
            rowCellStyles = blockCellStyles.get(rowCellStyleIndex);
        }

        if (rowCellStyles == null) {
            return;
        }

        CellStyle cellStyle = null;
        int cellDefinitionIndex = columnIndex - this.currentRange.getBeginColumnIndex();
        if (0 <= cellDefinitionIndex && cellDefinitionIndex < rowCellStyles.size()) {
            cellStyle = rowCellStyles.get(cellDefinitionIndex);
        }

        if (cellStyle == null) {
            return;
        }

        context.getCell().setCellStyle(cellStyle);
    }

    private void init(Workbook workbook) {
        for (EasyExcelBlockDefinition currentBlock : this.currentBlocks) {
            List<ExcelRowDefinition> allRowDefinitionList = new ArrayList<>();
            Optional.ofNullable(currentBlock.getHeaderDefinitionList()).ifPresent(allRowDefinitionList::addAll);
            if (ExcelAnalysisTypeEnum.FIXED_HEADER.equals(currentBlock.getAnalysisType())) {
                allRowDefinitionList.add(currentBlock.getConfigHeader());
            }
            Optional.ofNullable(currentBlock.getRowDefinitionList()).ifPresent(allRowDefinitionList::addAll);

            List<List<CellStyle>> allStyles = new ArrayList<>();
            for (ExcelRowDefinition rowDefinition : allRowDefinitionList) {
                List<CellStyle> columnStyles = new ArrayList<>();
                List<ExcelCellDefinition> cellList = rowDefinition.getCellList();
                if (CollectionUtils.isNotEmpty(cellList)) {
                    for (ExcelCellDefinition cell : cellList) {
                        columnStyles.add(cell.getOrCreateCellStyle(workbook));
                    }
                }
                allStyles.add(columnStyles);
            }
            this.currentStyles.add(allStyles);
        }
    }
}
