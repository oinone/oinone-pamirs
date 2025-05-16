package pro.shushi.pamirs.file.api.util.analysis;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelDirectionEnum;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelLoopMergeDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.*;
import pro.shushi.pamirs.file.api.util.ExcelCellRangeHelper;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 固定表头{@link ExcelAnalysisTypeEnum#FIXED_HEADER}解析类型帮助类
 *
 * @author Adamancy Zhang at 15:27 on 2021-08-14
 */
public class ExcelFixedHeaderAnalysisHelper {

    private ExcelFixedHeaderAnalysisHelper() {
        //reject create object
    }

    /**
     * 预置行处理
     *
     * @param easyExcelSheetDefinition EasyExcel工作表定义
     * @param easyExcelBlockDefinition EasyExcel块定义
     * @param blockDefinition          原始块定义
     */
    public static void presetRowProcess(EasyExcelSheetDefinition easyExcelSheetDefinition, EasyExcelBlockDefinition easyExcelBlockDefinition, ExcelBlockDefinition blockDefinition) {
        Boolean usingCascadingStyle = easyExcelBlockDefinition.getUsingCascadingStyle();
        ExcelCellRangeDefinition currentRange = easyExcelBlockDefinition.getCurrentRange();
        int beginRowIndex = currentRange.getBeginRowIndex(),
                endRowIndex = currentRange.getEndRowIndex(),
                beginColumnIndex = currentRange.getBeginColumnIndex(),
                endColumnIndex = currentRange.getEndColumnIndex();
        ExcelDirectionEnum direction = easyExcelBlockDefinition.getDirection();
        List<ExcelRowDefinition> rowList = blockDefinition.getRowList();
        if (rowList == null) {
            rowList = new ArrayList<>();
        }
        int rowSize = rowList.size();
        int headerSize = easyExcelBlockDefinition.getHeaderDefinitionList().size();
        int rowIndex, emptyRowSize;
        switch (direction) {
            case HORIZONTAL:
                rowIndex = headerSize + rowSize + beginRowIndex;
                emptyRowSize = endRowIndex - beginRowIndex + 1 - headerSize - rowSize;
                break;
            case VERTICAL:
                rowIndex = headerSize + rowSize + beginColumnIndex;
                emptyRowSize = endColumnIndex - beginColumnIndex + 1 - headerSize - rowSize;
                break;
            default:
                throw new IllegalArgumentException("Invalid excel block direction type. value=" + direction);
        }
        Integer presetNumber = blockDefinition.getPresetNumber();
        if (presetNumber == null) {
            presetNumber = rowSize;
        }
        if (presetNumber <= 0) {
            presetNumber = 1;
        }
        //处理预置行 样式: 列样式 < 行样式 < 单元格样式
        for (int i = 0; i < presetNumber; i++) {
            ExcelRowDefinition rowDefinition;
            if (rowSize > i) {
                rowDefinition = rowList.get(i);
            } else {
                rowDefinition = new ExcelRowDefinition();
                rowList.add(rowDefinition);
                if (emptyRowSize > 0) {
                    emptyRowSize--;
                } else {
                    ExcelFixedHeaderAnalysisHelper.afterRowCreateInCurrentBlock(easyExcelSheetDefinition, easyExcelBlockDefinition, rowIndex);
                }
            }
            ExcelAnalysisHelper.cellStyleProcess(currentRange, easyExcelSheetDefinition, easyExcelBlockDefinition, rowDefinition, usingCascadingStyle);
            rowIndex++;
        }
        easyExcelBlockDefinition.setRowDefinitionList(rowList);
    }

    /**
     * 获取空行大小Map
     *
     * @param blockDefinitions EasyExcel块列表
     * @return 空行大小Map
     */
    public static Map<Integer, AtomicInteger> fetchEmptyRowSizeMap(List<EasyExcelBlockDefinition> blockDefinitions) {
        Map<Integer, AtomicInteger> emptyRowSizeMap = new HashMap<>(blockDefinitions.size());
        for (EasyExcelBlockDefinition blockDefinition : blockDefinitions) {
            if (!ExcelAnalysisTypeEnum.FIXED_HEADER.equals(blockDefinition.getAnalysisType())) {
                continue;
            }
            int blockNumber = blockDefinition.getBlockNumber();
            ExcelCellRangeDefinition currentRange = blockDefinition.getCurrentRange();
            int beginRowIndex = currentRange.getBeginRowIndex(),
                    endRowIndex = currentRange.getEndRowIndex(),
                    beginColumnIndex = currentRange.getBeginColumnIndex(),
                    endColumnIndex = currentRange.getEndColumnIndex();
            ExcelDirectionEnum direction = blockDefinition.getDirection();
            int headerSize = blockDefinition.getHeaderDefinitionList().size();
            int emptyRowSize;
            switch (direction) {
                case HORIZONTAL:
                    emptyRowSize = endRowIndex - beginRowIndex + 1 - headerSize;
                    break;
                case VERTICAL:
                    emptyRowSize = endColumnIndex - beginColumnIndex + 1 - headerSize;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid excel block direction type. value=" + direction);
            }
            emptyRowSizeMap.put(blockNumber, new AtomicInteger(emptyRowSize));
        }
        return emptyRowSizeMap;
    }

    /**
     * 在当前块中创建新行后的处理逻辑
     *
     * @param currentSheet 当前工作表
     * @param currentBlock 当前块
     * @param rowIndex     行索引
     */
    public static void afterRowCreateInCurrentBlock(EasyExcelSheetDefinition currentSheet, EasyExcelBlockDefinition currentBlock, int rowIndex) {
        blockLoopMergeProcess(currentSheet, currentBlock, rowIndex);
        scale(currentSheet, currentBlock, 1);
    }

    /**
     * 块循环合并处理
     *
     * @param currentSheet 当前EasyExcel工作表定义
     * @param currentBlock 当前EasyExcel块定义
     */
    private static void blockLoopMergeProcess(EasyExcelSheetDefinition currentSheet, EasyExcelBlockDefinition currentBlock, int rowIndex) {
        List<EasyExcelLoopMergeDefinition> loopMergeRanges = currentBlock.getLoopMergeRangeList();
        if (CollectionUtils.isNotEmpty(loopMergeRanges)) {
            List<EasyExcelLoopMergeDefinition> loopMergeRangeList = currentSheet.getLoopMergeRangeList();
            for (EasyExcelLoopMergeDefinition loopMergeRange : loopMergeRanges) {
                ExcelCellRangeDefinition mergeRangeDefinition = loopMergeRange.getMergeRange();
                loopMergeRangeList.add(new EasyExcelLoopMergeDefinition()
                        .setFirstStyle(loopMergeRange.getFirstStyle())
                        .setMergeRange(new ExcelCellRangeDefinition(
                                rowIndex,
                                rowIndex,
                                mergeRangeDefinition.getBeginColumnIndex(),
                                mergeRangeDefinition.getEndColumnIndex())
                        )
                );
            }
        }
    }

    /**
     * 调整当前块规模范围，并处理影响块列表
     *
     * @param currentSheet 当前EasyExcel工作表定义
     * @param currentBlock 当前EasyExcel块定义
     */
    public static void scale(EasyExcelSheetDefinition currentSheet, EasyExcelBlockDefinition currentBlock, int offset) {
        if (!ExcelAnalysisTypeEnum.FIXED_HEADER.equals(currentBlock.getAnalysisType())) {
            throw new IllegalArgumentException("Invalid analysis type. it must be ExcelAnalysisTypeEnum#FIXED_HEADER.");
        }
        if (offset == 0) {
            throw new IllegalArgumentException("Invalid offset value. it must not be zero.");
        }
        ExcelDirectionEnum direction = currentBlock.getDirection();
        switch (direction) {
            case HORIZONTAL:
                scaleByHorizontal(currentSheet, currentBlock, offset);
                break;
            case VERTICAL:
                scaleByVertical(currentSheet, currentBlock, offset);
                break;
            default:
                throw new IllegalArgumentException("Invalid excel block direction type. value=" + direction);
        }
    }

    private static void scaleByHorizontal(EasyExcelSheetDefinition currentSheet, EasyExcelBlockDefinition currentBlock, int offset) {
        ExcelCellRangeDefinition currentBlockRange = currentBlock.getCurrentRange();
        List<EasyExcelBlockDefinition> influenceTranslationBlockList = currentBlock.getInfluenceTranslationBlockList();
        List<EasyExcelBlockDefinition> influenceFillBlockList = currentBlock.getInfluenceFillBlockList();
        int currentEndRowIndex = currentBlockRange.getEndRowIndex(),
                lastedEndRowIndex = currentEndRowIndex + offset;
        Optional.ofNullable(currentSheet.getCurrentRange()).ifPresent(v -> v.setEndRowIndex(lastedEndRowIndex));
        currentBlockRange.setEndRowIndex(lastedEndRowIndex);
        if (CollectionUtils.isNotEmpty(influenceTranslationBlockList)) {
            for (EasyExcelBlockDefinition influenceBlock : influenceTranslationBlockList) {
                if (offset < 0) {
                    ExcelCellRangeHelper.translation(influenceBlock, offset, 0);
                } else {
                    if (influenceBlock.getCurrentRange().getBeginRowIndex() <= lastedEndRowIndex) {
                        ExcelCellRangeHelper.translation(influenceBlock, offset, 0);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(influenceFillBlockList) && offset > 0) {
            for (EasyExcelBlockDefinition influenceBlock : influenceFillBlockList) {
                List<ExcelRowDefinition> rowDefinitionList = influenceBlock.getRowDefinitionList();
                int originRowIndex = currentEndRowIndex - 1;
                if (rowDefinitionList.size() > originRowIndex) {
                    ExcelRowDefinition originRow = rowDefinitionList.get(originRowIndex);
                    for (int i = 0; i < offset; i++) {
                        ExcelRowDefinition copyRow = new ExcelRowDefinition();
                        List<ExcelCellDefinition> originCellList = originRow.getCellList();
                        if (CollectionUtils.isNotEmpty(originCellList)) {
                            List<ExcelCellDefinition> copyCellList = new ArrayList<>(originCellList.size());
                            for (ExcelCellDefinition originCell : originCellList) {
                                copyCellList.add(copyCellByScale(originCell));
                            }
                            copyRow.setCellList(copyCellList);
                        }
                        ExcelStyleDefinition originStyle = originRow.getStyle();
                        if (originStyle != null) {
                            copyRow.setStyle(originStyle.clone());
                        }
                        rowDefinitionList.add(originRowIndex, copyRow);
                    }
                    ExcelCellRangeDefinition currentInfluenceBlockRange = influenceBlock.getCurrentRange();
                    currentInfluenceBlockRange.setEndRowIndex(currentInfluenceBlockRange.getEndRowIndex() + offset);
                }
                List<ExcelCellRangeDefinition> mergeRangeList = influenceBlock.getMergeRangeList();
                if (CollectionUtils.isNotEmpty(mergeRangeList)) {
                    for (ExcelCellRangeDefinition mergeRange : mergeRangeList) {
                        if (!mergeRange.getFixedEndRowIndex()
                                && currentEndRowIndex >= mergeRange.getBeginRowIndex()
                                && currentEndRowIndex <= mergeRange.getEndRowIndex()) {
                            mergeRange.setEndRowIndex(mergeRange.getEndRowIndex() + offset);
                            mergeRange.disableReentry();
                        }
                    }
                }
            }
        }
    }

    private static void scaleByVertical(EasyExcelSheetDefinition currentSheet, EasyExcelBlockDefinition currentBlock, int offset) {
        ExcelCellRangeDefinition currentBlockRange = currentBlock.getCurrentRange();
        List<EasyExcelBlockDefinition> influenceTranslationBlockList = currentBlock.getInfluenceTranslationBlockList();
        List<EasyExcelBlockDefinition> influenceFillBlockList = currentBlock.getInfluenceFillBlockList();
        int currentEndColumnIndex = currentBlockRange.getEndColumnIndex(),
                lastedEndColumnIndex = currentEndColumnIndex + offset;
        Optional.ofNullable(currentSheet.getCurrentRange()).ifPresent(v -> v.setEndColumnIndex(lastedEndColumnIndex));
        currentBlockRange.setEndColumnIndex(lastedEndColumnIndex);
        if (CollectionUtils.isNotEmpty(influenceTranslationBlockList)) {
            for (EasyExcelBlockDefinition influenceBlock : influenceTranslationBlockList) {
                if (offset < 0) {
                    ExcelCellRangeHelper.translation(influenceBlock, 0, offset);
                } else {
                    if (influenceBlock.getCurrentRange().getBeginColumnIndex() <= lastedEndColumnIndex) {
                        ExcelCellRangeHelper.translation(influenceBlock, 0, offset);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(influenceFillBlockList) && offset > 0) {
            for (EasyExcelBlockDefinition influenceBlock : influenceFillBlockList) {
                List<ExcelRowDefinition> rowDefinitionList = influenceBlock.getRowDefinitionList();
                int originCellIndex = currentEndColumnIndex - 1;
                Map<Integer, ExcelCellDefinition> copyCellMap = new HashMap<>(offset);
                int i = 0;
                for (ExcelRowDefinition rowDefinition : rowDefinitionList) {
                    List<ExcelCellDefinition> cellList = rowDefinition.getCellList();
                    if (CollectionUtils.isNotEmpty(cellList)) {
                        if (cellList.size() > originCellIndex) {
                            copyCellMap.put(i, copyCellByScale(cellList.get(originCellIndex)));
                        }
                    }
                    i++;
                }
                if (!copyCellMap.isEmpty()) {
                    for (int k = 0; k < offset; k++) {
                        for (Map.Entry<Integer, ExcelCellDefinition> entry : copyCellMap.entrySet()) {
                            ExcelCellDefinition copyCell = entry.getValue();
                            if (k > 0) {
                                copyCell = copyCell.clone();
                            }
                            rowDefinitionList.get(entry.getKey()).getCellList().add(originCellIndex, copyCell);
                        }
                    }
                    ExcelCellRangeDefinition currentInfluenceBlockRange = influenceBlock.getCurrentRange();
                    currentInfluenceBlockRange.setEndColumnIndex(currentInfluenceBlockRange.getEndColumnIndex() + offset);
                }
                List<ExcelCellRangeDefinition> mergeRangeList = influenceBlock.getMergeRangeList();
                if (CollectionUtils.isNotEmpty(mergeRangeList)) {
                    for (ExcelCellRangeDefinition mergeRange : mergeRangeList) {
                        if (!mergeRange.getFixedEndColumnIndex()
                                && currentEndColumnIndex >= mergeRange.getBeginColumnIndex()
                                && currentEndColumnIndex <= mergeRange.getEndColumnIndex()) {
                            mergeRange.setEndColumnIndex(mergeRange.getEndColumnIndex() + offset);
                        }
                    }
                }
            }
        }
    }

    /**
     * 调整规模时复制单元格
     *
     * @param cell 原始单元格
     * @return 复制单元格
     */
    private static ExcelCellDefinition copyCellByScale(ExcelCellDefinition cell) {
        return cell.clone();
    }
}
