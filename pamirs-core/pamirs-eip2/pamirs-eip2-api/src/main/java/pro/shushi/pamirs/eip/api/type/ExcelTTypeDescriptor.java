package pro.shushi.pamirs.eip.api.type;

import pro.shushi.pamirs.eip.api.tmodel.EipExcelTypeTransform;
import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * @author Gesi at 14:48 on 2025/7/18
 */
@Data
public class ExcelTTypeDescriptor {

    private String name;

    private String originType;

    private String format;

    private String targetType;

    private String value;

    public static ExcelTTypeDescriptor valueOf(String value, EipExcelTypeTransform typeTransform) {
        ExcelTTypeDescriptor excelTTypeDescriptor = new ExcelTTypeDescriptor();
        excelTTypeDescriptor.setName(typeTransform.getName());
        excelTTypeDescriptor.setOriginType(typeTransform.getOriginType());
        excelTTypeDescriptor.setFormat(typeTransform.getFormat());
        excelTTypeDescriptor.setTargetType(typeTransform.getTargetType());
        excelTTypeDescriptor.setValue(value);
        return excelTTypeDescriptor;
    }

    public static ExcelTTypeDescriptor valueOf(String value, String originType, String targetType, String format) {
        ExcelTTypeDescriptor excelTTypeDescriptor = new ExcelTTypeDescriptor();
        excelTTypeDescriptor.setOriginType(originType);
        excelTTypeDescriptor.setFormat(format);
        excelTTypeDescriptor.setTargetType(targetType);
        excelTTypeDescriptor.setValue(value);
        return excelTTypeDescriptor;
    }

    public static ExcelTTypeDescriptor valueOf(String value, String originType, String targetType) {
        return valueOf(value, originType, targetType, null);
    }

}
