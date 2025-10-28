package pro.shushi.pamirs.file.api.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import pro.shushi.pamirs.file.api.enmu.*;
import pro.shushi.pamirs.file.api.model.ExcelCellRangeDefinition;
import pro.shushi.pamirs.file.api.model.ExcelStyleDefinition;
import pro.shushi.pamirs.file.api.model.ExcelTypefaceDefinition;

import java.util.Optional;

public class ExcelDefinitionConverter {

    public static HorizontalAlignment convertHorizontalAlignment(ExcelHorizontalAlignmentEnum horizontalAlignment) {
        return convertHorizontalAlignment(horizontalAlignment, null);
    }

    public static HorizontalAlignment convertHorizontalAlignment(ExcelHorizontalAlignmentEnum horizontalAlignment, HorizontalAlignment defaultValue) {
        if (horizontalAlignment == null) {
            return defaultValue;
        }
        return horizontalAlignment.getPoi();
    }

    public static VerticalAlignment convertVerticalAlignment(ExcelVerticalAlignmentEnum verticalAlignment) {
        return convertVerticalAlignment(verticalAlignment, null);
    }

    public static VerticalAlignment convertVerticalAlignment(ExcelVerticalAlignmentEnum verticalAlignment, VerticalAlignment defaultValue) {
        if (verticalAlignment == null) {
            return defaultValue;
        }
        return verticalAlignment.getPoi();
    }

    public static BorderStyle convertBorderStyle(ExcelBorderStyleEnum borderStyle) {
        return convertBorderStyle(borderStyle, null);
    }

    public static BorderStyle convertBorderStyle(ExcelBorderStyleEnum borderStyle, BorderStyle defaultValue) {
        if (borderStyle == null) {
            return defaultValue;
        }
        return borderStyle.getPoi();
    }

    public static CellRangeAddress convertCellRangeAddress(ExcelCellRangeDefinition cellRangeDefinition) {
        return new CellRangeAddress(cellRangeDefinition.getBeginRowIndex(),
                cellRangeDefinition.getEndRowIndex(),
                cellRangeDefinition.getBeginColumnIndex(),
                cellRangeDefinition.getEndColumnIndex());
    }

    public static ExcelStyleDefinition mergeCellStyleDefinition(ExcelStyleDefinition oldCellStyle, ExcelStyleDefinition newCellStyle, Boolean usingCascadingStyle) {
        if (newCellStyle == null) {
            return oldCellStyle;
        }
        if (oldCellStyle == null) {
            return newCellStyle;
        }
        if (usingCascadingStyle != null && usingCascadingStyle) {
            ExcelStyleDefinition mergedCellStyle = oldCellStyle.clone();
            Optional.ofNullable(newCellStyle.getHorizontalAlignment()).ifPresent(mergedCellStyle::setHorizontalAlignment);
            Optional.ofNullable(newCellStyle.getVerticalAlignment()).ifPresent(mergedCellStyle::setVerticalAlignment);
            Optional.ofNullable(newCellStyle.getFillBorderStyle()).ifPresent(mergedCellStyle::setFillBorderStyle);
            Optional.ofNullable(newCellStyle.getTopBorderStyle()).ifPresent(mergedCellStyle::setTopBorderStyle);
            Optional.ofNullable(newCellStyle.getRightBorderStyle()).ifPresent(mergedCellStyle::setRightBorderStyle);
            Optional.ofNullable(newCellStyle.getBottomBorderStyle()).ifPresent(mergedCellStyle::setBottomBorderStyle);
            Optional.ofNullable(newCellStyle.getLeftBorderStyle()).ifPresent(mergedCellStyle::setLeftBorderStyle);
            Optional.ofNullable(newCellStyle.getFillBorderColor()).ifPresent(mergedCellStyle::setFillBorderColor);
            Optional.ofNullable(newCellStyle.getTopBorderColor()).ifPresent(mergedCellStyle::setTopBorderColor);
            Optional.ofNullable(newCellStyle.getRightBorderColor()).ifPresent(mergedCellStyle::setRightBorderColor);
            Optional.ofNullable(newCellStyle.getBottomBorderColor()).ifPresent(mergedCellStyle::setBottomBorderColor);
            Optional.ofNullable(newCellStyle.getLeftBorderColor()).ifPresent(mergedCellStyle::setLeftBorderColor);
            Optional.ofNullable(newCellStyle.getFillPatternType()).ifPresent(mergedCellStyle::setFillPatternType);
            Optional.ofNullable(newCellStyle.getBackgroundColor()).ifPresent(mergedCellStyle::setBackgroundColor);
            Optional.ofNullable(newCellStyle.getForegroundColor()).ifPresent(mergedCellStyle::setForegroundColor);
            Optional.ofNullable(newCellStyle.getWrapText()).ifPresent(mergedCellStyle::setWrapText);
            Optional.ofNullable(newCellStyle.getShrinkToFit()).ifPresent(mergedCellStyle::setShrinkToFit);
            Optional.ofNullable(newCellStyle.getWidth()).ifPresent(mergedCellStyle::setWidth);
            Optional.ofNullable(newCellStyle.getHeight()).ifPresent(mergedCellStyle::setHeight);
            Optional.ofNullable(newCellStyle.getTypefaceDefinition()).ifPresent(v -> {
                Optional.ofNullable(v.getTypeface()).ifPresent(v::setTypeface);
                Optional.ofNullable(v.getTypefaceName()).ifPresent(v::setTypefaceName);
                Optional.ofNullable(v.getSize()).ifPresent(v::setSize);
                Optional.ofNullable(v.getItalic()).ifPresent(v::setItalic);
                Optional.ofNullable(v.getStrikeout()).ifPresent(v::setStrikeout);
                Optional.ofNullable(v.getColor()).ifPresent(v::setColor);
                Optional.ofNullable(v.getTypeOffset()).ifPresent(v::setTypeOffset);
                Optional.ofNullable(v.getUnderline()).ifPresent(v::setUnderline);
                Optional.ofNullable(v.getBold()).ifPresent(v::setBold);
            });
            return mergedCellStyle;
        }
        return newCellStyle;
    }

    public static CellStyle convertCellStyle(Workbook workbook, ExcelStyleDefinition cellStyleDefinition) {
        if (workbook == null) {
            return null;
        }
        CellStyle cellStyle = workbook.createCellStyle();
        if (cellStyleDefinition == null) {
            return cellStyle;
        }
        Optional.ofNullable(cellStyleDefinition.getHorizontalAlignment()).map(ExcelHorizontalAlignmentEnum::name).map(HorizontalAlignment::valueOf).ifPresent(cellStyle::setAlignment);
        Optional.ofNullable(cellStyleDefinition.getVerticalAlignment()).map(ExcelVerticalAlignmentEnum::name).map(VerticalAlignment::valueOf).ifPresent(cellStyle::setVerticalAlignment);
        Optional.ofNullable(cellStyleDefinition.getFillBorderStyle()).map(ExcelBorderStyleEnum::name).map(BorderStyle::valueOf).ifPresent(borderStyle -> {
            cellStyle.setBorderTop(borderStyle);
            cellStyle.setBorderRight(borderStyle);
            cellStyle.setBorderBottom(borderStyle);
            cellStyle.setBorderLeft(borderStyle);
        });
        Optional.ofNullable(cellStyleDefinition.getTopBorderStyle()).map(ExcelBorderStyleEnum::name).map(BorderStyle::valueOf).ifPresent(cellStyle::setBorderTop);
        Optional.ofNullable(cellStyleDefinition.getRightBorderStyle()).map(ExcelBorderStyleEnum::name).map(BorderStyle::valueOf).ifPresent(cellStyle::setBorderRight);
        Optional.ofNullable(cellStyleDefinition.getBottomBorderStyle()).map(ExcelBorderStyleEnum::name).map(BorderStyle::valueOf).ifPresent(cellStyle::setBorderBottom);
        Optional.ofNullable(cellStyleDefinition.getLeftBorderStyle()).map(ExcelBorderStyleEnum::name).map(BorderStyle::valueOf).ifPresent(cellStyle::setBorderLeft);
        Optional.ofNullable(cellStyleDefinition.getFillBorderColor()).ifPresent(borderColor -> {
            cellStyle.setTopBorderColor(borderColor.shortValue());
            cellStyle.setRightBorderColor(borderColor.shortValue());
            cellStyle.setBottomBorderColor(borderColor.shortValue());
            cellStyle.setLeftBorderColor(borderColor.shortValue());
        });
        Optional.ofNullable(cellStyleDefinition.getTopBorderColor()).map(Integer::shortValue).ifPresent(cellStyle::setTopBorderColor);
        Optional.ofNullable(cellStyleDefinition.getRightBorderColor()).map(Integer::shortValue).ifPresent(cellStyle::setRightBorderColor);
        Optional.ofNullable(cellStyleDefinition.getBottomBorderColor()).map(Integer::shortValue).ifPresent(cellStyle::setBottomBorderColor);
        Optional.ofNullable(cellStyleDefinition.getLeftBorderColor()).map(Integer::shortValue).ifPresent(cellStyle::setLeftBorderColor);
        Optional.ofNullable(cellStyleDefinition.getFillPatternType()).map(ExcelFillPatternTypeEnum::getPoi).ifPresent(cellStyle::setFillPattern);
        Optional.ofNullable(cellStyleDefinition.getBackgroundColor()).map(Integer::shortValue).ifPresent(cellStyle::setFillBackgroundColor);
        Optional.ofNullable(cellStyleDefinition.getForegroundColor()).map(Integer::shortValue).ifPresent(v -> {
            if (FillPatternType.NO_FILL.equals(cellStyle.getFillPattern())) {
                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            cellStyle.setFillForegroundColor(v);
        });
        Optional.ofNullable(cellStyleDefinition.getWrapText()).ifPresent(cellStyle::setWrapText);
        Optional.ofNullable(cellStyleDefinition.getShrinkToFit()).ifPresent(cellStyle::setShrinkToFit);
        Optional.ofNullable(cellStyleDefinition.getTypefaceDefinition()).ifPresent(typefaceDefinition -> cellStyle.setFont(convertTypeface(workbook, typefaceDefinition)));
        return cellStyle;
    }

    public static Font convertTypeface(Workbook workbook, ExcelTypefaceDefinition typefaceDefinition) {
        if (workbook == null || typefaceDefinition == null) {
            return null;
        }
        Font font = workbook.createFont();
        String typefaceName = typefaceDefinition.getTypefaceName();
        if (StringUtils.isBlank(typefaceName)) {
            typefaceName = Optional.ofNullable(typefaceDefinition.getTypeface()).map(ExcelTypefaceEnum::getDisplayName).orElse(null);
        }
        if (StringUtils.isNotBlank(typefaceName)) {
            font.setFontName(typefaceName);
        }
        Optional.ofNullable(typefaceDefinition.getSize()).map(Integer::shortValue).ifPresent(font::setFontHeightInPoints);
        Optional.ofNullable(typefaceDefinition.getItalic()).ifPresent(font::setItalic);
        Optional.ofNullable(typefaceDefinition.getStrikeout()).ifPresent(font::setStrikeout);
        Optional.ofNullable(typefaceDefinition.getColor()).map(Integer::shortValue).ifPresent(font::setColor);
        Optional.ofNullable(typefaceDefinition.getTypeOffset()).map(ExcelTypeOffsetEnum::getPoi).ifPresent(font::setTypeOffset);
        Optional.ofNullable(typefaceDefinition.getUnderline()).map(ExcelUnderlineEnum::getPoi).ifPresent(font::setUnderline);
        Optional.ofNullable(typefaceDefinition.getBold()).ifPresent(font::setBold);
        return font;
    }
}
