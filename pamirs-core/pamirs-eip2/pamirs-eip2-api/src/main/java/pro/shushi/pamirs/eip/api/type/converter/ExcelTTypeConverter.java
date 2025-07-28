package pro.shushi.pamirs.eip.api.type.converter;

import pro.shushi.pamirs.eip.api.type.ExcelTTypeDescriptor;

/**
 * @author Gesi at 15:30 on 2025/7/18
 */
public interface ExcelTTypeConverter {

    boolean canConvert(ExcelTTypeDescriptor excelTTypeDescriptor);

    String convert(ExcelTTypeDescriptor excelTTypeDescriptor);

    String defaultValue();

}
