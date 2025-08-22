package pro.shushi.pamirs.eip.api.excel.type;

import pro.shushi.pamirs.eip.api.tmodel.EipExcelTypeTransform;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.List;

/**
 * @author Gesi at 14:48 on 2025/7/18
 */
@Data
public class ExcelTTypeDescriptor {

    private List<ExcelTTypeConvertError> errorMessageHub;

    private String name;

    private String originType;

    private String format;

    private String targetType;

    private String value;

    private String sheetName;

    private Integer rowIndex;

    private Integer columnIndex;

    public static ExcelTTypeDescriptor valueOf(String value, EipExcelTypeTransform typeTransform) {
        ExcelTTypeDescriptor excelTTypeDescriptor = new ExcelTTypeDescriptor();
        excelTTypeDescriptor.setName(typeTransform.getName());
        excelTTypeDescriptor.setOriginType(typeTransform.getOriginType());
        excelTTypeDescriptor.setFormat(typeTransform.getFormat());
        excelTTypeDescriptor.setTargetType(typeTransform.getTargetType());
        excelTTypeDescriptor.setValue(value);
        return excelTTypeDescriptor;
    }

    public static ExcelTTypeDescriptor valueOf(String value, String name, String originType, String targetType, String format) {
        ExcelTTypeDescriptor excelTTypeDescriptor = new ExcelTTypeDescriptor();
        excelTTypeDescriptor.setOriginType(originType);
        excelTTypeDescriptor.setFormat(format);
        excelTTypeDescriptor.setTargetType(targetType);
        excelTTypeDescriptor.setValue(value);
        excelTTypeDescriptor.setName(name);
        return excelTTypeDescriptor;
    }

    public static ExcelTTypeDescriptor valueOf(String value, String originType, String targetType) {
        return valueOf(value, null, originType, targetType, null);
    }

}
