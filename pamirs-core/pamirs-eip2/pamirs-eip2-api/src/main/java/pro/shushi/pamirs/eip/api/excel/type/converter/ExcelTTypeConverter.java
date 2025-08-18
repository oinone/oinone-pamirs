package pro.shushi.pamirs.eip.api.excel.type.converter;

import pro.shushi.pamirs.eip.api.excel.type.ExcelTTypeConvertError;
import pro.shushi.pamirs.eip.api.excel.type.ExcelTTypeDescriptor;

/**
 * @author Gesi at 15:30 on 2025/7/18
 */
public interface ExcelTTypeConverter {

    boolean canConvert(ExcelTTypeDescriptor excelTTypeDescriptor);

    String convert(ExcelTTypeDescriptor excelTTypeDescriptor);

    default String defaultValue(ExcelTTypeDescriptor excelTTypeDescriptor) {
        if (excelTTypeDescriptor.getErrorMessageHub() != null && excelTTypeDescriptor.getRowIndex() != null && excelTTypeDescriptor.getColumnIndex() != null) {
            excelTTypeDescriptor.getErrorMessageHub().add(ExcelTTypeConvertError.valueOf(excelTTypeDescriptor));
        }
        return null;
    }

}
