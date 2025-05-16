package pro.shushi.pamirs.file.api.easyexcel.impl;

import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.ExcelCellRangeDefinition;
import pro.shushi.pamirs.file.api.util.ExcelCellRangeHelper;

import java.util.*;

/**
 * @author Adamancy Zhang at 18:05 on 2023-09-07
 */
public abstract class AbstractEasyExcelBlockRangeWriteHandler implements RowWriteHandler, CellWriteHandler {

    protected static final int READY_FIND_RANGE = -1;

    protected static final int ERROR_FIND_RANGE = -2;

    protected final EasyExcelSheetDefinition currentSheet;

    protected final List<EasyExcelBlockDefinition> currentBlocks;

    protected final Map<Integer, Integer> headerSizeMap;

    protected List<ExcelCellRangeDefinition> blockRanges;

    protected final boolean isSingleFixedHeaderBlock;

    protected final boolean isSingleFixedFormatBlock;

    protected ExcelCellRangeDefinition currentRange;

    protected int currentRangeIndex = READY_FIND_RANGE;

    public AbstractEasyExcelBlockRangeWriteHandler(EasyExcelSheetDefinition currentSheet) {
        this.currentSheet = currentSheet;
        this.currentBlocks = Collections.unmodifiableList(currentSheet.getBlockDefinitions());

        Map<Integer, Integer> headerSizeMap = new HashMap<>();
        List<ExcelCellRangeDefinition> blockRanges = new ArrayList<>();
        for (int i = 0; i < this.currentBlocks.size(); i++) {
            EasyExcelBlockDefinition currentBlock = this.currentBlocks.get(i);
            blockRanges.add(currentBlock.getCurrentRange());
            if (ExcelAnalysisTypeEnum.FIXED_HEADER.equals(currentBlock.getAnalysisType())) {
                headerSizeMap.put(i, Optional.ofNullable(currentBlock.getHeaderDefinitionList()).map(List::size).orElse(0));
            } else {
                headerSizeMap.put(i, 0);
            }
        }
        this.headerSizeMap = Collections.unmodifiableMap(headerSizeMap);
        this.blockRanges = Collections.unmodifiableList(blockRanges);
        boolean isSingleFixedHeaderBlock = false;
        boolean isSingleFixedFormatBlock = false;
        if (this.currentBlocks.size() == 1) {
            switch (this.currentBlocks.get(0).getAnalysisType()) {
                case FIXED_HEADER:
                    isSingleFixedHeaderBlock = true;
                    isSingleFixedFormatBlock = false;
                    break;
                case FIXED_FORMAT:
                    isSingleFixedHeaderBlock = false;
                    isSingleFixedFormatBlock = true;
                    break;
                default:
                    isSingleFixedHeaderBlock = false;
                    isSingleFixedFormatBlock = false;
                    break;
            }
        }
        this.isSingleFixedHeaderBlock = isSingleFixedHeaderBlock;
        this.isSingleFixedFormatBlock = isSingleFixedFormatBlock;
    }

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        this.refreshRange(context);
    }

    protected boolean refreshRange(CellWriteHandlerContext context) {
        int columnIndex = context.getColumnIndex();
        int relativeRowIndex = this.getRelativeRowIndex(context.getRowIndex(), context.getRelativeRowIndex());

        if (this.currentRange != null && !ExcelCellRangeHelper.isInRange(this.currentRange, relativeRowIndex, columnIndex)) {
            this.currentRange = null;
            if (this.currentRangeIndex == this.blockRanges.size() - 1) {
                this.currentRangeIndex = READY_FIND_RANGE;
            }
        }

        if (this.currentRange == null) {
            return this.findRange(relativeRowIndex, columnIndex);
        }
        return true;
    }

    protected int getSheetBasicOffset() {
        ExcelCellRangeDefinition sheetDesignRange = this.currentSheet.getCurrentRange();
        return sheetDesignRange.getEndRowIndex() - sheetDesignRange.getBeginRowIndex() + 1;
    }

    protected int getBasicOffset() {
        return this.getSheetBasicOffset();
    }

    protected int getRelativeRowIndex(int rowIndex, int relativeRowIndex) {
        return rowIndex % this.getBasicOffset();
    }

    protected int getRowOffsetIndex(int rowIndex, int relativeRowIndex) {
        int basicOffset = this.getBasicOffset();
        return rowIndex / basicOffset * basicOffset;
    }

    private boolean findRange(int relativeRowIndex, int columnIndex) {
        if (this.currentRangeIndex == ERROR_FIND_RANGE) {
            return false;
        }
        for (int i = this.currentRangeIndex + 1; i < this.blockRanges.size(); i++) {
            ExcelCellRangeDefinition range = this.blockRanges.get(i);
            if (ExcelCellRangeHelper.isInRange(range, relativeRowIndex, columnIndex)) {
                this.currentRange = range;
                this.currentRangeIndex = i;
                return true;
            }
        }
        this.currentRange = null;
        this.currentRangeIndex = ERROR_FIND_RANGE;
        return false;
    }

    protected boolean isSingleBlock() {
        return this.isSingleFixedHeaderBlock || this.isSingleFixedFormatBlock;
    }
}
