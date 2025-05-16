package pro.shushi.pamirs.file.api.util.analysis;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelDirectionEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelCellDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelLoopMergeDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.*;
import pro.shushi.pamirs.file.api.util.DataConvertHelper;
import pro.shushi.pamirs.file.api.util.ExcelCellRangeHelper;
import pro.shushi.pamirs.file.api.util.ExcelDefinitionConverter;
import pro.shushi.pamirs.file.api.util.WorkbookHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Excel解析帮助类
 *
 * @author Adamancy Zhang at 15:17 on 2021-08-14
 */
public class ExcelAnalysisHelper {

    private ExcelAnalysisHelper() {
        //reject create object
    }

    /**
     * 解析配置行
     *
     * @param easyExcelBlockDefinition EasyExcel工作簿定义
     * @param blockDefinition          原始块定义
     */
    public static void analysisConfigHeader(EasyExcelBlockDefinition easyExcelBlockDefinition, ExcelBlockDefinition blockDefinition) {
        List<ExcelHeaderDefinition> headerList = blockDefinition.getHeaderList();
        if (CollectionUtils.isEmpty(headerList)) {
            throw new IllegalArgumentException("任何解析类型必须至少定义一行表头");
        }
        ExcelHeaderDefinition configHeader = null;
        List<ExcelHeaderDefinition> headerDefinitionList = new ArrayList<>();
        for (ExcelHeaderDefinition headerDefinition : headerList) {
            if (configHeader == null) {
                configHeader = headerDefinition;
            }
            if (headerDefinition.getIsConfig()) {
                configHeader = headerDefinition;
            } else {
                headerDefinitionList.add(headerDefinition);
            }
        }
        if (configHeader == null) {
            throw new IllegalArgumentException("任何解析类型必须添加配置行，否则模板定义无效");
        }
        easyExcelBlockDefinition.setConfigHeader(configHeader)
                .setHeaderDefinitionList(headerDefinitionList);
    }

    /**
     * 计算Excel工作表设计范围
     *
     * @param easyExcelSheetDefinition EasyExcel工作表定义
     * @param blockDefinition          原始块定义
     */
    public static void computeSheetDesignRange(EasyExcelSheetDefinition easyExcelSheetDefinition, ExcelBlockDefinition blockDefinition) {
        computeSheetRange(easyExcelSheetDefinition, blockDefinition, EasyExcelSheetDefinition::getDesignRange, EasyExcelSheetDefinition::setDesignRange, ExcelBlockDefinition::getDesignRange);
    }

    /**
     * 计算Excel工作表当前范围
     *
     * @param easyExcelSheetDefinition EasyExcel工作表定义
     * @param blockDefinition          原始块定义
     */
    public static void computeSheetCurrentRange(EasyExcelSheetDefinition easyExcelSheetDefinition, EasyExcelBlockDefinition blockDefinition) {
        computeSheetRange(easyExcelSheetDefinition, blockDefinition, EasyExcelSheetDefinition::getCurrentRange, EasyExcelSheetDefinition::setCurrentRange, EasyExcelBlockDefinition::getCurrentRange);
    }

    private static <T> void computeSheetRange(EasyExcelSheetDefinition easyExcelSheetDefinition, T blockDefinition,
                                              Function<EasyExcelSheetDefinition, ExcelCellRangeDefinition> sheetRangeGetter,
                                              BiConsumer<EasyExcelSheetDefinition, ExcelCellRangeDefinition> sheetRangeSetter,
                                              Function<T, ExcelCellRangeDefinition> blockRangeGetter) {
        ExcelCellRangeDefinition sheetRange = sheetRangeGetter.apply(easyExcelSheetDefinition);
        ExcelCellRangeDefinition blockRange = blockRangeGetter.apply(blockDefinition);
        if (sheetRange == null) {
            sheetRange = new ExcelCellRangeDefinition()
                    .setBeginRowIndex(blockRange.getBeginRowIndex())
                    .setEndRowIndex(blockRange.getEndRowIndex())
                    .setBeginColumnIndex(blockRange.getBeginColumnIndex())
                    .setEndColumnIndex(blockRange.getEndColumnIndex())
                    .setFixedBeginRowIndex(false)
                    .setFixedEndRowIndex(false)
                    .setFixedBeginColumnIndex(false)
                    .setFixedEndColumnIndex(false);
            sheetRangeSetter.accept(easyExcelSheetDefinition, sheetRange);
        } else {
            sheetRange.setBeginRowIndex(Math.min(sheetRange.getBeginRowIndex(), blockRange.getBeginRowIndex()))
                    .setEndRowIndex(Math.max(sheetRange.getEndRowIndex(), blockRange.getEndRowIndex()))
                    .setBeginColumnIndex(Math.min(sheetRange.getBeginColumnIndex(), blockRange.getBeginColumnIndex()))
                    .setEndColumnIndex(Math.max(sheetRange.getEndColumnIndex(), blockRange.getEndColumnIndex()));
        }
    }

    /**
     * 查找循环合并列表
     *
     * @param easyExcelBlockDefinition EasyExcel块定义
     */
    public static void lookupLoopMergeRangeList(EasyExcelBlockDefinition easyExcelBlockDefinition) {
        if (!ExcelAnalysisTypeEnum.FIXED_HEADER.equals(easyExcelBlockDefinition.getAnalysisType())) {
            return;
        }
        List<ExcelCellRangeDefinition> mergeRangeList = easyExcelBlockDefinition.getMergeRangeList();
        if (CollectionUtils.isEmpty(mergeRangeList)) {
            return;
        }
        ExcelCellRangeDefinition designRange = easyExcelBlockDefinition.getDesignRange();
        ExcelCellRangeDefinition lastedHeaderRange;
        ExcelDirectionEnum direction = easyExcelBlockDefinition.getDirection();
        switch (direction) {
            case HORIZONTAL:
                int endRowIndex = designRange.getEndRowIndex();
                lastedHeaderRange = new ExcelCellRangeDefinition(
                        endRowIndex,
                        endRowIndex,
                        designRange.getBeginColumnIndex(),
                        designRange.getEndColumnIndex()
                );
                break;
            case VERTICAL:
                int endColumnIndex = designRange.getEndColumnIndex();
                lastedHeaderRange = new ExcelCellRangeDefinition(
                        endColumnIndex,
                        endColumnIndex,
                        designRange.getBeginRowIndex(),
                        designRange.getEndRowIndex()
                );
                break;
            default:
                throw new IllegalArgumentException("Invalid excel block direction type. value=" + direction);
        }
        List<EasyExcelLoopMergeDefinition> loopMergeRanges = new ArrayList<>();
        for (ExcelCellRangeDefinition mergeRange : mergeRangeList) {
            if (ExcelCellRangeHelper.isContains(lastedHeaderRange, mergeRange)) {
                loopMergeRanges.add(new EasyExcelLoopMergeDefinition()
                        .setMergeRange(mergeRange.clone()));
            }
        }
        if (!loopMergeRanges.isEmpty()) {
            easyExcelBlockDefinition.setLoopMergeRangeList(loopMergeRanges);
        }
    }

    /**
     * 填充循环合并样式
     *
     * @param easyExcelBlockDefinition EasyExcel块定义
     */
    public static void fillLoopMergeRangeStyle(EasyExcelBlockDefinition easyExcelBlockDefinition) {
        List<EasyExcelLoopMergeDefinition> loopMergeRanges = easyExcelBlockDefinition.getLoopMergeRangeList();
        if (CollectionUtils.isEmpty(loopMergeRanges)) {
            return;
        }
        ExcelCellRangeDefinition designRange = easyExcelBlockDefinition.getDesignRange();
        ExcelDirectionEnum direction = easyExcelBlockDefinition.getDirection();
        int offsetIndex;
        switch (direction) {
            case HORIZONTAL:
                offsetIndex = designRange.getBeginColumnIndex();
                break;
            case VERTICAL:
                offsetIndex = designRange.getBeginRowIndex();
                break;
            default:
                throw new IllegalArgumentException("Invalid excel block direction type. value=" + direction);
        }
        List<ExcelCellDefinition> lastedCellList = easyExcelBlockDefinition.getConfigHeader().getCellList();
        for (EasyExcelLoopMergeDefinition loopMergeRange : loopMergeRanges) {
            int cellIndex;
            switch (direction) {
                case HORIZONTAL:
                    cellIndex = loopMergeRange.getMergeRange().getBeginColumnIndex() - offsetIndex;
                    break;
                case VERTICAL:
                    cellIndex = loopMergeRange.getMergeRange().getBeginRowIndex() - offsetIndex;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid excel block direction type. value=" + direction);
            }
            if (cellIndex > lastedCellList.size()) {
                continue;
            }
            ExcelCellDefinition cell = lastedCellList.get(cellIndex);
            loopMergeRange.setFirstStyle(cell.getStyle());
        }
    }

    /**
     * 填充单元格
     *
     * @param workbook               Excel工作簿 {@link Workbook}
     * @param sheet                  Excel工作表 {@link Sheet}
     * @param blockDefinition        EasyExcel块定义 {@link EasyExcelBlockDefinition}
     * @param currentFillRangeGetter 当前填充范围Getter方法
     * @param allRowDefinitionList   所有行定义
     * @param ignoredFormat          是否忽略格式
     */
    public static void fillCell(Workbook workbook, Sheet sheet, EasyExcelBlockDefinition blockDefinition, Function<EasyExcelBlockDefinition, ExcelCellRangeDefinition> currentFillRangeGetter, List<ExcelRowDefinition> allRowDefinitionList, boolean ignoredFormat) {
        ExcelCellRangeDefinition currentRange = currentFillRangeGetter.apply(blockDefinition);
        Integer beginRowIndex = currentRange.getBeginRowIndex(),
                beginColumnIndex = currentRange.getBeginColumnIndex();
        ExcelDirectionEnum direction = blockDefinition.getDirection();
        int i;
        switch (direction) {
            case HORIZONTAL:
                i = beginRowIndex;
                for (ExcelRowDefinition rowDefinition : allRowDefinitionList) {
                    Row row = WorkbookHelper.getOrCreateRow(sheet, i);
                    Optional.ofNullable(rowDefinition.getStyle()).map(ExcelStyleDefinition::getHeight).map(Integer::shortValue).ifPresent(row::setHeight);
                    int k = beginColumnIndex;
                    boolean isHeader = isHeaderRowDefinition(rowDefinition);
                    boolean isConfig = isConfigRowDefinition(rowDefinition);
                    for (ExcelCellDefinition cellDefinition : rowDefinition.getCellList()) {
                        Cell cell = WorkbookHelper.getOrCreateCell(row, k);
                        CellStyle cellStyle = cellDefinition.getOrCreateCellStyle(workbook, true);
                        DataConvertHelper.setCellValue(workbook, sheet, cellDefinition, cell, cellStyle, isConfig, isHeader, ignoredFormat);
                        cell.setCellStyle(cellStyle);
                        k++;
                    }
                    i++;
                }
                break;
            case VERTICAL:
                i = beginColumnIndex;
                for (ExcelRowDefinition rowDefinition : allRowDefinitionList) {
                    int k = beginRowIndex;
                    boolean isHeader = isHeaderRowDefinition(rowDefinition);
                    boolean isConfig = isConfigRowDefinition(rowDefinition);
                    for (ExcelCellDefinition cellDefinition : rowDefinition.getCellList()) {
                        Row row = WorkbookHelper.getOrCreateRow(sheet, k);
                        Optional.ofNullable(cellDefinition.getStyle()).map(ExcelStyleDefinition::getHeight).map(Integer::shortValue).ifPresent(row::setHeight);
                        Cell cell = WorkbookHelper.getOrCreateCell(row, i);
                        CellStyle cellStyle = cellDefinition.getOrCreateCellStyle(workbook, true);
                        DataConvertHelper.setCellValue(workbook, sheet, cellDefinition, cell, cellStyle, isConfig, isHeader, ignoredFormat);
                        cell.setCellStyle(cellStyle);
                        k++;
                    }
                    i++;
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid excel block direction type. value=" + direction);
        }
    }

    /**
     * <h>受影响块解析</h>
     * <p>
     * <B>通用解析规则:</B><br>
     * 1、仅对固定表头的块进行解析。<br>
     * 2、根据块编码忽略当前块。<br>
     * </p>
     * <p>
     * <B>受填充影响需要被平移的块列表的解析规则:</B><br>
     * 1、在水平排列-纵向填充{@link ExcelDirectionEnum#HORIZONTAL}时，受影响块在同一垂直区域内的下方。<br>
     * 2、在垂直排列-横向填充{@link ExcelDirectionEnum#VERTICAL}时，受影响块在同一水平区域内的右方。<br>
     * </p>
     * <p>
     * <B>受填充影响需要被复制填充的块列表的解析规则:<B><br>
     * 1、只有固定格式的块可以被影响。<br>
     * 2、在水平排列-纵向填充{@link ExcelDirectionEnum#HORIZONTAL}时，受影响块与填充行在同一行。<br>
     * 3、在垂直排列-横向填充{@link ExcelDirectionEnum#VERTICAL}时，受影响块与填充行在同一列。<br>
     * </p>
     *
     * @param easyExcelSheetDefinition EasyExcel工作簿定义
     */
    public static void influenceBlockAnalysis(EasyExcelSheetDefinition easyExcelSheetDefinition) {
        List<EasyExcelBlockDefinition> blockDefinitions = easyExcelSheetDefinition.getBlockDefinitions();
        for (EasyExcelBlockDefinition currentBlock : blockDefinitions) {
            if (!ExcelAnalysisTypeEnum.FIXED_HEADER.equals(currentBlock.getAnalysisType())) {
                continue;
            }
            int blockNumber = currentBlock.getBlockNumber();
            ExcelCellRangeDefinition currentDesignRange = currentBlock.getDesignRange();
            int beginRowIndex = currentDesignRange.getBeginRowIndex(),
                    endRowIndex = currentDesignRange.getEndRowIndex(),
                    beginColumnIndex = currentDesignRange.getBeginColumnIndex(),
                    endColumnIndex = currentDesignRange.getEndColumnIndex();
            ExcelDirectionEnum direction = currentBlock.getDirection();
            int initialCapacity = blockDefinitions.size() - 1;
            List<EasyExcelBlockDefinition> influenceTranslationBlockList = new ArrayList<>(initialCapacity);
            List<EasyExcelBlockDefinition> influenceFillBlockList = new ArrayList<>(initialCapacity);
            for (EasyExcelBlockDefinition influenceBlock : blockDefinitions) {
                if (blockNumber == influenceBlock.getBlockNumber()) {
                    continue;
                }
                boolean isInfluenceTranslation = false;
                boolean isInfluenceFill = false;
                ExcelAnalysisTypeEnum analysisType = influenceBlock.getAnalysisType();
                ExcelCellRangeDefinition influenceDesignRange = influenceBlock.getDesignRange();
                int influenceBeginRowIndex = influenceDesignRange.getBeginRowIndex(),
                        influenceEndRowIndex = influenceDesignRange.getEndRowIndex(),
                        influenceBeginColumnIndex = influenceDesignRange.getBeginColumnIndex(),
                        influenceEndColumnIndex = influenceDesignRange.getEndColumnIndex();
                switch (direction) {
                    case HORIZONTAL:
                        if (endRowIndex < influenceBeginRowIndex
                                && beginColumnIndex <= influenceEndColumnIndex
                                && influenceBeginColumnIndex <= endColumnIndex) {
                            isInfluenceTranslation = true;
                        }
                        if (ExcelAnalysisTypeEnum.FIXED_FORMAT.equals(analysisType)
                                && !influenceDesignRange.getFixedEndRowIndex()
                                && endRowIndex >= influenceBeginRowIndex
                                && endRowIndex <= influenceEndRowIndex) {
                            isInfluenceFill = true;
                        }
                        break;
                    case VERTICAL:
                        if (endColumnIndex < influenceBeginColumnIndex
                                && beginRowIndex <= influenceEndRowIndex
                                && influenceBeginRowIndex >= endRowIndex) {
                            isInfluenceTranslation = true;
                        }
                        if (ExcelAnalysisTypeEnum.FIXED_FORMAT.equals(analysisType)
                                && !influenceDesignRange.getFixedEndColumnIndex()
                                && endColumnIndex >= influenceBeginColumnIndex
                                && endColumnIndex <= influenceEndColumnIndex) {
                            isInfluenceFill = true;
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid excel block direction type. value=" + direction);
                }
                if (isInfluenceTranslation) {
                    influenceTranslationBlockList.add(influenceBlock);
                }
                if (isInfluenceFill) {
                    influenceFillBlockList.add(influenceBlock);
                }
            }
            if (!influenceTranslationBlockList.isEmpty()) {
                currentBlock.setInfluenceTranslationBlockList(influenceTranslationBlockList);
            }
            if (!influenceFillBlockList.isEmpty()) {
                currentBlock.setInfluenceFillBlockList(influenceFillBlockList);
            }
        }
    }

    /**
     * 单元格样式处理 样式: 列样式 < 行样式 < 单元格样式
     *
     * @param currentRange             当前区域
     * @param easyExcelSheetDefinition EasyExcel工作表定义
     * @param rowDefinition            行定义
     */
    public static void cellStyleProcess(ExcelCellRangeDefinition currentRange, EasyExcelSheetDefinition easyExcelSheetDefinition, EasyExcelBlockDefinition easyExcelBlockDefinition, ExcelRowDefinition rowDefinition, Boolean usingCascadingStyle) {
        ExcelCellRangeHelper.switchBeginAndEndIndex(currentRange, easyExcelBlockDefinition.getDirection(), (rowBegin, rowEnd, columnBegin, columnEnd) -> {
            Map<Integer, ExcelStyleDefinition> columnStyles = easyExcelSheetDefinition.getColumnStyles();
            boolean isHeaderRow = isHeaderRowDefinition(rowDefinition);
            List<ExcelCellDefinition> cellList = rowDefinition.getCellList();
            if (cellList == null) {
                cellList = new ArrayList<>();
                rowDefinition.setCellList(cellList);
            }
            ExcelStyleDefinition rowStyleDefinition = rowDefinition.getStyle();
            int cellSize = cellList.size();
            for (int i = columnBegin; i <= columnEnd; i++) {
                ExcelCellDefinition cell;
                int realColumnIndex = i - columnBegin;
                if (cellSize > realColumnIndex) {
                    cell = cellList.get(realColumnIndex);
                } else {
                    cell = new ExcelCellDefinition();
                    cellList.add(cell);
                }
                ExcelStyleDefinition styleDefinition = ExcelDefinitionConverter.mergeCellStyleDefinition(columnStyles.get(i), rowStyleDefinition, usingCascadingStyle);
                styleDefinition = ExcelDefinitionConverter.mergeCellStyleDefinition(styleDefinition, cell.getStyle(), usingCascadingStyle);
                if (styleDefinition != null) {
                    styleDefinition = styleDefinition.clone();
                    cell.setStyle(styleDefinition);
                }
                if (isHeaderRow) {
                    //忽略表头行使用属性单元格格式
                    continue;
                }
                EasyExcelCellDefinition fieldCellDefinition = easyExcelBlockDefinition.getColumnFieldCells().get(i);
                if (fieldCellDefinition != null) {
                    ExcelValueTypeEnum valueType = cell.getType();
                    if (valueType == null) {
                        valueType = fieldCellDefinition.getType();
                    }
                    String format = cell.getFormat();
                    if (StringUtils.isBlank(format)) {
                        format = fieldCellDefinition.getFormat();
                        if (StringUtils.isBlank(format) && valueType != null) {
                            format = valueType.getDefaultFormat();
                        }
                    }
                    cell.setType(valueType)
                            .setFormat(format);
                }
            }
        });
    }

    public static boolean isHeaderRowDefinition(ExcelRowDefinition rowDefinition) {
        return rowDefinition instanceof ExcelHeaderDefinition;
    }

    public static boolean isConfigRowDefinition(ExcelRowDefinition rowDefinition) {
        return isHeaderRowDefinition(rowDefinition) && ((ExcelHeaderDefinition) rowDefinition).getIsConfig();
    }
}
