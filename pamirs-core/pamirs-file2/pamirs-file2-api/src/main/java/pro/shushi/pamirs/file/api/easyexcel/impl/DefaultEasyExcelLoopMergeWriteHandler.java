package pro.shushi.pamirs.file.api.easyexcel.impl;

import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelLoopMergeDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.ExcelCellDefinition;
import pro.shushi.pamirs.file.api.model.ExcelCellRangeDefinition;
import pro.shushi.pamirs.file.api.model.ExcelRowDefinition;
import pro.shushi.pamirs.file.api.model.ExcelStyleDefinition;
import pro.shushi.pamirs.file.api.util.ExcelCellRangeHelper;
import pro.shushi.pamirs.file.api.util.ExcelDefinitionConverter;
import pro.shushi.pamirs.file.api.util.WorkbookHelper;

import java.util.*;

/**
 * <h>默认EasyExcel循环合并写入处理器</h>
 *
 * @author Adamancy Zhang at 18:02 on 2023-09-07
 */
public class DefaultEasyExcelLoopMergeWriteHandler extends AbstractEasyExcelBlockRangeWriteHandler implements RowWriteHandler, CellWriteHandler {

    private final Map<Integer, List<EasyExcelLoopMergeDefinition>> loopMergeRangeMap;

    private boolean isCloneRange = false;

    private int overflowSize = 0;

    private int lastedRowIndex = -1;

    public DefaultEasyExcelLoopMergeWriteHandler(EasyExcelSheetDefinition currentSheet) {
        super(currentSheet);
        this.loopMergeRangeMap = new HashMap<>(this.currentBlocks.size());
    }

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        if (this.isSingleFixedHeaderBlock) {
            processSingleFixedHeaderRange(context);
        }
        super.afterCellDispose(context);
    }

    private void processSingleFixedHeaderRange(CellWriteHandlerContext context) {
        if (this.currentRange == null) {
            return;
        }

        int rowIndex = context.getRowIndex();
        int fillRelativeRowIndex = context.getRelativeRowIndex();
        int relativeRowIndex = this.getRelativeRowIndex(rowIndex, fillRelativeRowIndex);
        if (rowIndex == relativeRowIndex) {
            return;
        }

        if (fillRelativeRowIndex == 0) {
            resetBlockRanges();
            return;
        }

        int columnIndex = context.getColumnIndex();
        int beginColumnIndex = this.currentRange.getBeginColumnIndex();
        int nextRelativeRowIndex = relativeRowIndex + 1;
        int currentFillRowSize = this.currentRange.getEndRowIndex() - this.currentRange.getBeginRowIndex() + 1 - this.headerSizeMap.get(this.currentRangeIndex);
        if (columnIndex == beginColumnIndex && relativeRowIndex == currentFillRowSize) {
            if (!ExcelCellRangeHelper.isInRange(this.currentRange, nextRelativeRowIndex, this.currentRange.getBeginColumnIndex())) {
                cloneBlockRanges();
                this.currentRange.setEndRowIndex(this.currentRange.getEndRowIndex() + 1);
            }
        }
    }

    @Override
    protected boolean refreshRange(CellWriteHandlerContext context) {
        int rowIndex = context.getRowIndex();
        int relativeRowIndex = this.getRelativeRowIndex(rowIndex, context.getRelativeRowIndex());
        int columnIndex = context.getColumnIndex();

        if (this.currentRange != null) {
            int fillRelativeRowIndex = context.getRelativeRowIndex();
            if (fillRelativeRowIndex == 0 && columnIndex == this.currentRange.getBeginColumnIndex()) {
                int fillRowSize = this.currentRange.getEndRowIndex() - this.currentRange.getBeginRowIndex() + 1 - this.headerSizeMap.get(this.currentRangeIndex);
                int currentFillRowSize = relativeRowIndex - this.currentRange.getBeginRowIndex() + 1 - this.headerSizeMap.get(this.currentRangeIndex);
                if (fillRowSize > currentFillRowSize && this.currentRangeIndex == this.blockRanges.size() - 1) {
                    this.lastedRowIndex = rowIndex;
                }
            }
        }
        return super.refreshRange(context);
    }

    @Override
    public void afterRowCreate(RowWriteHandlerContext context) {
        if (this.currentRange == null) {
            return;
        }

        if (this.isSingleFixedHeaderBlock) {
            return;
        }

        processFixedHeaderRange(context);
    }

    private void processFixedHeaderRange(RowWriteHandlerContext context) {
        int fillRelativeRowIndex = context.getRelativeRowIndex();
        if (fillRelativeRowIndex < 1) {
            resetBlockRanges();
            return;
        }

        EasyExcelBlockDefinition blockDefinition = this.currentBlocks.get(this.currentRangeIndex);

        if (!ExcelAnalysisTypeEnum.FIXED_HEADER.equals(blockDefinition.getAnalysisType())) {
            resetBlockRanges();
            return;
        }

        int rowIndex = context.getRowIndex();
        int relativeRowIndex = this.getRelativeRowIndex(rowIndex, fillRelativeRowIndex);
        if (rowIndex == relativeRowIndex) {
            resetBlockRanges();
            return;
        }

        ExcelCellRangeDefinition blockRange = blockDefinition.getCurrentRange();

        int loopMergeRangeListSize = Optional.ofNullable(this.currentSheet.getLoopMergeRangeList()).map(List::size).orElse(0);
        if (loopMergeRangeListSize == 0) {
            loopMergeRangeListSize = blockRange.getEndRowIndex() - blockRange.getBeginRowIndex() - 1;
        }
        int loopMergeRangeIndex = fillRelativeRowIndex - 1;
        int overflowSize = loopMergeRangeIndex - loopMergeRangeListSize + 1;
        if (overflowSize <= 0) {
            resetBlockRanges();
            return;
        }

        int nextRelativeRowIndex = blockRange.getEndRowIndex() - blockRange.getBeginRowIndex() + overflowSize + 1;
        if (!ExcelCellRangeHelper.isInRange(this.currentRange, nextRelativeRowIndex, this.currentRange.getEndColumnIndex())) {
            if (overflowSize == 1) {
                cloneBlockRanges();
            }
            this.currentRange.setEndRowIndex(this.currentRange.getEndRowIndex() + 1);
            this.overflowSize = overflowSize;
        }
    }

    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        if (this.currentRange == null) {
            return;
        }

        int rowIndex = context.getRowIndex();
        if (rowIndex < this.getBasicOffset()) {
            return;
        }

        EasyExcelBlockDefinition blockDefinition = this.currentBlocks.get(this.currentRangeIndex);

        switch (blockDefinition.getAnalysisType()) {
            case FIXED_HEADER:
                processFixedHeaderMergeRangeList(context, blockDefinition);
                break;
            case FIXED_FORMAT:
                processFixedFormatMergeRangeList(context, blockDefinition);
                break;
        }
    }

    private void processFixedFormatMergeRangeList(RowWriteHandlerContext context, EasyExcelBlockDefinition blockDefinition) {
        List<ExcelCellRangeDefinition> mergeRangeList = blockDefinition.getMergeRangeList();
        if (CollectionUtils.isEmpty(mergeRangeList)) {
            return;
        }

        ExcelCellRangeDefinition designRange = blockDefinition.getDesignRange();

        int rowIndex = context.getRowIndex();
        int mergeRangeRowOffsetIndex = rowIndex - (designRange.getEndRowIndex() - designRange.getBeginRowIndex());

        Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
        Sheet sheet = context.getWriteSheetHolder().getSheet();
        for (ExcelCellRangeDefinition mergeRange : mergeRangeList) {
            if (ExcelCellRangeHelper.isContains(designRange, mergeRange)) {
                ExcelStyleDefinition firstStyle = findMergedRangeStyle(blockDefinition, mergeRange);
                if (firstStyle != null) {
                    Row row = context.getRow();
                    for (int i = mergeRange.getBeginColumnIndex(); i <= mergeRange.getEndColumnIndex(); i++) {
                        Cell cell = WorkbookHelper.getOrCreateCell(row, i);
                        cell.setCellStyle(firstStyle.getOrCreateCellStyle(workbook));
                    }
                }
                ExcelCellRangeDefinition currentMergeRange = ExcelCellRangeHelper.translation(mergeRange.clone(), mergeRangeRowOffsetIndex, 0, true);
                sheet.addMergedRegion(ExcelDefinitionConverter.convertCellRangeAddress(currentMergeRange));
            }
        }
    }

    private ExcelStyleDefinition findMergedRangeStyle(EasyExcelBlockDefinition blockDefinition, ExcelCellRangeDefinition mergeRange) {
        return findMergedRangeStyle(blockDefinition, mergeRange, 0);
    }

    private ExcelStyleDefinition findMergedRangeStyle(EasyExcelBlockDefinition blockDefinition, ExcelCellRangeDefinition mergeRange, int rowOffsetIndex) {
        List<ExcelRowDefinition> rowDefinitionList = blockDefinition.getRowDefinitionList();
        if (CollectionUtils.isEmpty(rowDefinitionList)) {
            rowDefinitionList = FetchUtil.cast(blockDefinition.getHeaderDefinitionList());
            if (CollectionUtils.isEmpty(rowDefinitionList)) {
                return null;
            }
        }

        int rowIndex = mergeRange.getBeginRowIndex() - rowOffsetIndex;
        ExcelRowDefinition rowDefinition = null;
        if (0 <= rowIndex && rowIndex < rowDefinitionList.size()) {
            rowDefinition = rowDefinitionList.get(rowIndex);
        }
        if (rowDefinition == null) {
            return null;
        }

        List<ExcelCellDefinition> cellList = rowDefinition.getCellList();
        int columnIndex = mergeRange.getBeginColumnIndex();
        ExcelCellDefinition cell = null;
        if (0 <= columnIndex && columnIndex < cellList.size()) {
            cell = cellList.get(columnIndex);
        }
        if (cell == null) {
            return null;
        }

        return cell.getStyle();
    }

    private void processFixedHeaderMergeRangeList(RowWriteHandlerContext context, EasyExcelBlockDefinition blockDefinition) {
        List<EasyExcelLoopMergeDefinition> loopMergeRangeList = this.currentSheet.getLoopMergeRangeList();
        if (CollectionUtils.isEmpty(loopMergeRangeList)) {
            return;
        }

        int currentRelativeRowIndex = this.getRelativeRowIndex(context.getRowIndex(), context.getRelativeRowIndex());
        if (currentRelativeRowIndex == 0) {
            return;
        }
        int loopMergeRangeIndex = currentRelativeRowIndex - this.currentRange.getBeginRowIndex() - this.headerSizeMap.get(this.currentRangeIndex);

        if (loopMergeRangeIndex == 0) {
            processHeaderMergeRangeList(context, blockDefinition);
        }

        EasyExcelLoopMergeDefinition loopMergeRange;
        int rowIndex = context.getRowIndex();
        int overflowSize = loopMergeRangeIndex - loopMergeRangeList.size() + 1;
        if (overflowSize <= 0) {
            if (loopMergeRangeIndex < 0) {
                loopMergeRange = loopMergeRangeList.get(0);
            } else {
                loopMergeRange = loopMergeRangeList.get(loopMergeRangeIndex);
            }
        } else {
            EasyExcelLoopMergeDefinition lastedLoopMergeRange = loopMergeRangeList.get(loopMergeRangeList.size() - 1);
            List<EasyExcelLoopMergeDefinition> overflowMergeRangeList = this.loopMergeRangeMap.computeIfAbsent(this.currentRangeIndex, (k) -> new ArrayList<>());
            for (int i = overflowMergeRangeList.size() + 1; i <= overflowSize; i++) {
                overflowMergeRangeList.add(new EasyExcelLoopMergeDefinition()
                        .setFirstStyle(lastedLoopMergeRange.getFirstStyle())
                        .setMergeRange(ExcelCellRangeHelper.translation(lastedLoopMergeRange.getMergeRange().clone(), i, 0, true)));
            }
            loopMergeRange = overflowMergeRangeList.get(overflowSize - 1);
        }

        if (loopMergeRange == null) {
            return;
        }

        ExcelCellRangeDefinition blockRange = blockDefinition.getCurrentRange();
        int relativeRowIndex = blockRange.getEndRowIndex() + overflowSize;
        ExcelCellRangeDefinition mergeRange = loopMergeRange.getMergeRange();

        if (relativeRowIndex != mergeRange.getEndRowIndex()) {
            return;
        }

        Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
        Sheet sheet = context.getWriteSheetHolder().getSheet();
        int mergeRangeOffsetIndex = rowIndex - blockRange.getEndRowIndex() - overflowSize;

        ExcelStyleDefinition firstStyle = loopMergeRange.getFirstStyle();
        if (firstStyle != null) {
            Row row = context.getRow();
            for (int i = mergeRange.getBeginColumnIndex(); i <= mergeRange.getEndColumnIndex(); i++) {
                Cell cell = WorkbookHelper.getOrCreateCell(row, i);
                cell.setCellStyle(firstStyle.getOrCreateCellStyle(workbook));
            }
        }

        ExcelCellRangeDefinition currentMergeRange = ExcelCellRangeHelper.translation(mergeRange.clone(), mergeRangeOffsetIndex, 0, true);
        sheet.addMergedRegion(ExcelDefinitionConverter.convertCellRangeAddress(currentMergeRange));
    }

    private void processHeaderMergeRangeList(RowWriteHandlerContext context, EasyExcelBlockDefinition blockDefinition) {
        List<ExcelCellRangeDefinition> mergeRangeList = blockDefinition.getMergeRangeList();
        if (CollectionUtils.isEmpty(mergeRangeList)) {
            return;
        }

        ExcelCellRangeDefinition designRange = blockDefinition.getDesignRange().clone();
        if (!this.isSingleFixedHeaderBlock) {
            designRange.setEndRowIndex(designRange.getEndRowIndex() - 1);
        }

        int rowIndex = context.getRowIndex();
        int rowOffsetIndex = this.getRowOffsetIndex(rowIndex, context.getRelativeRowIndex());
        int mergeRangeRowOffsetIndex = rowOffsetIndex - 1 + designRange.getBeginRowIndex();

        Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
        Sheet sheet = context.getWriteSheetHolder().getSheet();
        for (ExcelCellRangeDefinition mergeRange : mergeRangeList) {
            if (ExcelCellRangeHelper.isContains(designRange, mergeRange)) {
                ExcelStyleDefinition firstStyle = findMergedRangeStyle(blockDefinition, mergeRange, designRange.getBeginRowIndex());
                if (firstStyle != null) {
                    Row row = context.getRow();
                    for (int i = mergeRange.getBeginColumnIndex(); i <= mergeRange.getEndColumnIndex(); i++) {
                        Cell cell = WorkbookHelper.getOrCreateCell(row, i);
                        cell.setCellStyle(firstStyle.getOrCreateCellStyle(workbook));
                    }
                }
                ExcelCellRangeDefinition currentMergeRange = ExcelCellRangeHelper.translation(mergeRange.clone(), mergeRangeRowOffsetIndex, 0, true);
                sheet.addMergedRegion(ExcelDefinitionConverter.convertCellRangeAddress(currentMergeRange));
            }
        }
    }

    @Override
    protected int getBasicOffset() {
        return super.getBasicOffset() + overflowSize;
    }

    @Override
    protected int getRelativeRowIndex(int rowIndex, int relativeRowIndex) {
        int value;
        if (this.currentRange == null) {
            if (rowIndex > 0) {
                value = rowIndex - 1;
            } else {
                value = rowIndex;
            }
            return value;
        }
        if (rowIndex < super.getBasicOffset()) {
            return rowIndex;
        }
        if (lastedRowIndex == -1) {
            lastedRowIndex = rowIndex;
            return 0;
        }
        int currentRelativeRowIndex = rowIndex - lastedRowIndex;
        if (relativeRowIndex == 0 && !ExcelCellRangeHelper.isInRange(this.currentRange, currentRelativeRowIndex, this.currentRange.getBeginColumnIndex()) && this.currentRangeIndex == this.blockRanges.size() - 1) {
            lastedRowIndex = rowIndex;
            return 0;
        }
        return currentRelativeRowIndex;
    }

    @Override
    protected int getRowOffsetIndex(int rowIndex, int relativeRowIndex) {
        if (rowIndex < super.getBasicOffset()) {
            return rowIndex;
        }
        return rowIndex - this.getRelativeRowIndex(rowIndex, relativeRowIndex);
    }

    protected void cloneBlockRanges() {
        if (this.isCloneRange) {
            return;
        }
        List<ExcelCellRangeDefinition> blockRanges = new ArrayList<>();
        for (EasyExcelBlockDefinition currentBlock : this.currentBlocks) {
            blockRanges.add(currentBlock.getCurrentRange().clone());
        }
        this.blockRanges = Collections.unmodifiableList(blockRanges);
        this.currentRange = this.blockRanges.get(this.currentRangeIndex);
        this.isCloneRange = true;
    }

    protected void resetBlockRanges() {
        if (!this.isCloneRange) {
            return;
        }
        overflowSize = 0;
        List<ExcelCellRangeDefinition> blockRanges = new ArrayList<>();
        for (EasyExcelBlockDefinition currentBlock : this.currentBlocks) {
            blockRanges.add(currentBlock.getCurrentRange());
        }
        this.blockRanges = Collections.unmodifiableList(blockRanges);
        this.currentRange = this.blockRanges.get(this.currentRangeIndex);
        this.isCloneRange = false;
    }
}
