package pro.shushi.pamirs.eip.api.type.converter;

import pro.shushi.pamirs.eip.api.type.ExcelTTypeDescriptor;

/**
 * @author Gesi at 15:30 on 2025/7/18
 */
public interface ExcelTTypeConverter {

    boolean canConvert(ExcelTTypeDescriptor excelTTypeDescriptor);

    String convert(ExcelTTypeDescriptor excelTTypeDescriptor);

    default String defaultValue(ExcelTTypeDescriptor excelTTypeDescriptor) {
        if (excelTTypeDescriptor.getErrorMessageHub() != null && excelTTypeDescriptor.getRowIndex() != null && excelTTypeDescriptor.getColumnIndex() != null) {
            String sheetName = excelTTypeDescriptor.getSheetName();
            Integer rowIndex = excelTTypeDescriptor.getRowIndex();
            Integer columnIndex = excelTTypeDescriptor.getColumnIndex();
            excelTTypeDescriptor.getErrorMessageHub().add(String.format("sheet：%s第%s行第%s列值：%s解析成%s失败", sheetName, rowIndex + 1, columnIndex + 1, excelTTypeDescriptor.getValue(), excelTTypeDescriptor.getTargetType()));
        }
        return null;
    }

}
