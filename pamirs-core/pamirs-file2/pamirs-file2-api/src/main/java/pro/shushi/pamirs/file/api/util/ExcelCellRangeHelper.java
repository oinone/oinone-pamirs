package pro.shushi.pamirs.file.api.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.util.CellReference;
import pro.shushi.pamirs.file.api.enmu.ExcelDirectionEnum;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelLoopMergeDefinition;
import pro.shushi.pamirs.file.api.model.ExcelCellRangeDefinition;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.List;

/**
 * Excel单元格范围帮助类
 *
 * @author Adamancy Zhang at 12:18 on 2021-08-13
 */
@Slf4j
public class ExcelCellRangeHelper {

    /**
     * 范围分割符
     */
    private static final String RANGE_CHAR = ":";

    /**
     * 绝对定位
     */
    private static final String FIXED_CHAR = "$";

    private static final int SINGLE_RANGE_COORDINATE = 1;

    private static final int STANDARD_RANGE_COORDINATE = 2;

    private ExcelCellRangeHelper() {
        //reject create object
    }

    /**
     * 根据排列方向选择行/列-启始/结束索引
     *
     * @param rangeDefinition 范围定义
     * @param direction       排列方向
     * @param consumer        行/列-起始/结束的索引
     */
    public static void switchBeginAndEndIndex(ExcelCellRangeDefinition rangeDefinition, ExcelDirectionEnum direction, BeginAndEndIndexConsumer consumer) {
        if (direction == null) {
            consumer.accept(rangeDefinition.getBeginRowIndex(), rangeDefinition.getEndRowIndex(), rangeDefinition.getBeginColumnIndex(), rangeDefinition.getEndColumnIndex());
            return;
        }
        switch (direction) {
            case HORIZONTAL:
                consumer.accept(rangeDefinition.getBeginRowIndex(), rangeDefinition.getEndRowIndex(), rangeDefinition.getBeginColumnIndex(), rangeDefinition.getEndColumnIndex());
                break;
            case VERTICAL:
                consumer.accept(rangeDefinition.getBeginColumnIndex(), rangeDefinition.getEndColumnIndex(), rangeDefinition.getBeginRowIndex(), rangeDefinition.getEndRowIndex());
                break;
            default:
                throw new IllegalArgumentException("Invalid excel block direction type. value=" + direction);
        }
    }

    /**
     * 起始/结束索引消费者
     */
    @FunctionalInterface
    public interface BeginAndEndIndexConsumer {

        /**
         * 消费逻辑
         *
         * @param rowBegin    起始行索引
         * @param rowEnd      结束行索引
         * @param columnBegin 起始列索引
         * @param columnEnd   结束列索引
         */
        void accept(int rowBegin, int rowEnd, int columnBegin, int columnEnd);
    }

    /**
     * 判断指定单元格是否在范围内
     *
     * @param rangeDefinition 范围
     * @param rowIndex        行索引
     * @param columnIndex     列索引
     * @return 是否在范围内
     */
    public static boolean isInRange(ExcelCellRangeDefinition rangeDefinition, int rowIndex, int columnIndex) {
        return rangeDefinition.getBeginRowIndex() <= rowIndex && rowIndex <= rangeDefinition.getEndRowIndex()
                && rangeDefinition.getBeginColumnIndex() <= columnIndex && columnIndex <= rangeDefinition.getEndColumnIndex();
    }

    /**
     * 判断指定范围是否包含在范围内
     *
     * @param originRangeDefinition 原规划范围
     * @param targetRangeDefinition 指定范围
     * @return 是否包含
     */
    public static boolean isContains(ExcelCellRangeDefinition originRangeDefinition, ExcelCellRangeDefinition targetRangeDefinition) {
        return originRangeDefinition.getBeginRowIndex() <= targetRangeDefinition.getBeginRowIndex() && originRangeDefinition.getEndRowIndex() >= targetRangeDefinition.getEndRowIndex()
                && originRangeDefinition.getBeginColumnIndex() <= targetRangeDefinition.getBeginColumnIndex() && originRangeDefinition.getEndColumnIndex() >= targetRangeDefinition.getEndColumnIndex();
    }

    /**
     * 判断指定范围是否与目标范围相交
     *
     * @param originRangeDefinition 指定范围
     * @param targetRangeDefinition 目标范围
     * @return 是否相交
     */
    public static boolean isIntersects(ExcelCellRangeDefinition originRangeDefinition, ExcelCellRangeDefinition targetRangeDefinition) {
        return originRangeDefinition.getBeginRowIndex() <= targetRangeDefinition.getEndRowIndex()
                && originRangeDefinition.getBeginColumnIndex() <= targetRangeDefinition.getEndColumnIndex()
                && targetRangeDefinition.getBeginRowIndex() <= originRangeDefinition.getEndRowIndex()
                && targetRangeDefinition.getBeginColumnIndex() <= originRangeDefinition.getEndColumnIndex();
    }

    /**
     * 范围平移
     *
     * @param rangeDefinition   范围定义
     * @param rowOffsetIndex    行偏移量
     * @param columnOffsetIndex 列偏移量
     * @param isForce           是否强制平移
     */
    public static ExcelCellRangeDefinition translation(ExcelCellRangeDefinition rangeDefinition, int rowOffsetIndex, int columnOffsetIndex, boolean isForce) {
        if (isForce) {
            rangeDefinition.setBeginRowIndex(rangeDefinition.getBeginRowIndex() + rowOffsetIndex)
                    .setEndRowIndex(rangeDefinition.getEndRowIndex() + rowOffsetIndex)
                    .setBeginColumnIndex(rangeDefinition.getBeginColumnIndex() + columnOffsetIndex)
                    .setEndColumnIndex(rangeDefinition.getEndColumnIndex() + columnOffsetIndex);
        } else {
            if (!rangeDefinition.getFixedBeginRowIndex()) {
                rangeDefinition.setBeginRowIndex(rangeDefinition.getBeginRowIndex() + rowOffsetIndex);
            }
            if (!rangeDefinition.getFixedEndRowIndex()) {
                rangeDefinition.setEndRowIndex(rangeDefinition.getEndRowIndex() + rowOffsetIndex);
            }
            if (!rangeDefinition.getFixedBeginColumnIndex()) {
                rangeDefinition.setBeginColumnIndex(rangeDefinition.getBeginColumnIndex() + columnOffsetIndex);
            }
            if (!rangeDefinition.getFixedEndColumnIndex()) {
                rangeDefinition.setEndColumnIndex(rangeDefinition.getEndColumnIndex() + columnOffsetIndex);
            }
        }
        return rangeDefinition;
    }

    /**
     * 块平移
     *
     * @param blockDefinition   EasyExcel块定义
     * @param rowOffsetIndex    行偏移量
     * @param columnOffsetIndex 列偏移量
     */
    public static void translation(EasyExcelBlockDefinition blockDefinition, int rowOffsetIndex, int columnOffsetIndex) {
        ExcelCellRangeHelper.translation(blockDefinition.getCurrentRange(), rowOffsetIndex, columnOffsetIndex, true);
        List<ExcelCellRangeDefinition> mergeRangeList = blockDefinition.getMergeRangeList();
        if (CollectionUtils.isNotEmpty(mergeRangeList)) {
            for (ExcelCellRangeDefinition mergeRange : mergeRangeList) {
                ExcelCellRangeHelper.translation(mergeRange, rowOffsetIndex, columnOffsetIndex, true);
            }
        }
        List<EasyExcelLoopMergeDefinition> loopMergeRanges = blockDefinition.getLoopMergeRangeList();
        if (CollectionUtils.isNotEmpty(loopMergeRanges)) {
            for (EasyExcelLoopMergeDefinition loopMergeRange : loopMergeRanges) {
                ExcelCellRangeHelper.translation(loopMergeRange.getMergeRange(), rowOffsetIndex, columnOffsetIndex, true);
            }
        }
    }

    public static ExcelCellRangeDefinition analysis(String coordinate) {
        String[] coordinates = coordinate.split(RANGE_CHAR);
        if (coordinates.length == SINGLE_RANGE_COORDINATE) {
            return analysis(coordinates[0], coordinates[0]);
        }
        if (coordinates.length == STANDARD_RANGE_COORDINATE) {
            return analysis(coordinates[0], coordinates[1]);
        }
        return null;
    }

    private static ExcelCellRangeDefinition analysis(String beginCoordinate, String endCoordinate) {
        ExcelCellRangeDefinition rangeDefinition = new ExcelCellRangeDefinition();
        boolean isBeginAnalysisSuccess = analysisCoordinate(beginCoordinate, (isBeginFixed, begin, isEndFixed, end) ->
                rangeDefinition.setBeginRowIndex(begin)
                        .setBeginColumnIndex(end)
                        .setFixedBeginRowIndex(isBeginFixed)
                        .setFixedBeginColumnIndex(isEndFixed)),
                isEndAnalysisSuccess = analysisCoordinate(endCoordinate, (isBeginFixed, begin, isEndFixed, end) ->
                        rangeDefinition.setEndRowIndex(begin)
                                .setEndColumnIndex(end)
                                .setFixedEndRowIndex(isBeginFixed)
                                .setFixedEndColumnIndex(isEndFixed));
        if (isBeginAnalysisSuccess && isEndAnalysisSuccess) {
            return rangeDefinition;
        }
        return null;
    }

    private static boolean analysisCoordinate(String coordinate, CoordinateConsumer consumer) {
        try {
            CellReference ref = new CellReference(coordinate);
            consumer.accept(ref.isRowAbsolute(), ref.getRow(), ref.isColAbsolute(), ref.getCol());
        } catch (Exception e) {
            log.error("Invalid cell reference value. value: {}", coordinate, e);
            return false;
        }
        return true;
    }

    /**
     * 坐标消费者
     */
    private interface CoordinateConsumer {

        /**
         * 消费逻辑
         *
         * @param isFixedRowIndex    行坐标是否为绝对坐标
         * @param rowIndex           行坐标
         * @param isFixedColumnIndex 列坐标是否为绝对坐标
         * @param columnIndex        列坐标
         */
        void accept(boolean isFixedRowIndex, int rowIndex, boolean isFixedColumnIndex, int columnIndex);
    }
}
