package pro.shushi.pamirs.file.api.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import pro.shushi.pamirs.file.api.enmu.OfficeVersionEnum;

import java.util.Collection;

public class WorkbookHelper {

    /**
     * 创建工作簿
     *
     * @return 新工作簿
     */
    public static Workbook createWorkbook(OfficeVersionEnum version) {
        switch (version) {
            case OLD:
                return new HSSFWorkbook();
            case AUTO:
            case NEW:
                return new XSSFWorkbook();
            default:
                throw new IllegalArgumentException("Invalid version");
        }
    }

    /**
     * 在指定工作簿中创建工作表
     *
     * @param workbook 指定工作簿
     * @param name     指定工作表名称
     * @return 新工作表
     */
    public static Sheet createSheet(Workbook workbook, String name) {
        return workbook.createSheet(name);
    }

    public static OfficeVersionEnum getVersion(Workbook workbook) {
        SpreadsheetVersion version = workbook.getSpreadsheetVersion();
        switch (version) {
            case EXCEL97:
                return OfficeVersionEnum.OLD;
            case EXCEL2007:
                return OfficeVersionEnum.NEW;
            default:
                throw new IllegalArgumentException("Invalid version");
        }
    }

    public static RichTextString createRichTextString(Workbook workbook, String text) {
        SpreadsheetVersion version = workbook.getSpreadsheetVersion();
        switch (version) {
            case EXCEL97:
                return new HSSFRichTextString(text);
            case EXCEL2007:
                return new XSSFRichTextString(text);
            default:
                throw new IllegalArgumentException("Invalid version");
        }
    }

    public static DataValidationHelper createDataValidationHelper(Workbook workbook, Sheet sheet) {
        SpreadsheetVersion version = workbook.getSpreadsheetVersion();
        switch (version) {
            case EXCEL97:
                return new HSSFDataValidationHelper((HSSFSheet) sheet);
            case EXCEL2007:
                return new XSSFDataValidationHelper((XSSFSheet) sheet);
            default:
                throw new IllegalArgumentException("Invalid version");
        }
    }

    public static DataValidation createDataValidation(Workbook workbook, Sheet sheet, CellRangeAddressList regions, Collection<String> values) {
        SpreadsheetVersion version = workbook.getSpreadsheetVersion();
        DataValidationHelper dataValidationHelper = createDataValidationHelper(workbook, sheet);
        DataValidationConstraint dataValidationConstraint = dataValidationHelper.createExplicitListConstraint(values.toArray(new String[0]));
        switch (version) {
            case EXCEL97:
                return new HSSFDataValidation(regions, dataValidationConstraint);
            case EXCEL2007:
                return dataValidationHelper.createValidation(dataValidationConstraint, regions);
            default:
                throw new IllegalArgumentException("Invalid version");
        }
    }

    /**
     * 获取工作簿
     *
     * @param workbook   指定工作簿
     * @param sheetIndex 指定工作表索引[0,{@link Workbook#getNumberOfSheets()})
     * @return 工作表
     */
    public static Sheet getSheet(Workbook workbook, Integer sheetIndex) {
        if (sheetIndex >= 0 && sheetIndex < workbook.getNumberOfSheets()) {
            return workbook.getSheetAt(sheetIndex);
        }
        return null;
    }

    /**
     * 获取工作簿
     *
     * @param workbook  指定工作簿
     * @param sheetName 指定工作表名称
     * @return 工作表
     */
    public static Sheet getSheet(Workbook workbook, String sheetName) {
        return workbook.getSheet(sheetName);
    }

    public static Row getOrCreateRow(Sheet sheet, Integer index) {
        Row row = sheet.getRow(index);
        if (row == null) {
            row = sheet.createRow(index);
        }
        return row;
    }

    public static Cell getOrCreateCell(Row row, Integer index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            cell = row.createCell(index);
        }
        return cell;
    }

    /**
     * 获取默认单元格样式
     *
     * @param workbook 指定工作簿
     * @return 单元格样式，全边框线、上下居中，左右居中，宋体，五号字
     */
    public static CellStyle createStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setWrapText(false);
        Font font = workbook.createFont();
        font.setFontName("宋体");
        style.setFont(font);
        return style;
    }

    public static boolean verifyBelongToStyleSource(Workbook workbook, CellStyle style) {
        SpreadsheetVersion version = workbook.getSpreadsheetVersion();
        switch (version) {
            case EXCEL97:
                if (!(style instanceof HSSFCellStyle)) {
                    return false;
                }
                HSSFCellStyle hssfCellStyle = (HSSFCellStyle) style;
                HSSFWorkbook hssfWorkbook = (HSSFWorkbook) workbook;
                try {
                    hssfCellStyle.verifyBelongsToWorkbook(hssfWorkbook);
                } catch (IllegalArgumentException e) {
                    return false;
                }
                break;
            case EXCEL2007:
                if (!(style instanceof XSSFCellStyle)) {
                    return false;
                }
                XSSFCellStyle xssfCellStyle = (XSSFCellStyle) style;
                StylesTable stylesTable;
                if (workbook instanceof SXSSFWorkbook) {
                    SXSSFWorkbook sxssfWorkbook = (SXSSFWorkbook) workbook;
                    stylesTable = sxssfWorkbook.getXSSFWorkbook().getStylesSource();
                } else if (workbook instanceof XSSFWorkbook) {
                    XSSFWorkbook xssfWorkbook = (XSSFWorkbook) workbook;
                    stylesTable = xssfWorkbook.getStylesSource();
                } else {
                    throw new IllegalArgumentException("Invalid workbook");
                }
                try {
                    xssfCellStyle.verifyBelongsToStylesSource(stylesTable);
                } catch (IllegalArgumentException e) {
                    return false;
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid version");
        }
        return true;
    }
}
