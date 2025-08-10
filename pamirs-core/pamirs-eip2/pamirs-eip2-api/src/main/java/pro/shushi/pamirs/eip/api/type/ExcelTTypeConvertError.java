package pro.shushi.pamirs.eip.api.type;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.Optional;

/**
 * @author Gesi at 10:35 on 2025/8/6
 */
@Data
public class ExcelTTypeConvertError {

    private String name;

    private String originType;

    private String format;

    private String targetType;

    private String value;

    private String sheetName;

    private Integer rowIndex;

    private Integer columnIndex;

    private ExcelTTypeConvertError() {
    }

    public static ExcelTTypeConvertError valueOf(String name, String originType, String format, String targetType, String value, String sheetName, Integer rowIndex, Integer columnIndex) {
        ExcelTTypeConvertError error = new ExcelTTypeConvertError();
        error.name = Optional.ofNullable(name).orElse("");
        error.originType = originType;
        error.format = Optional.ofNullable(format).orElse("");
        error.targetType = targetType;
        error.value = value;
        error.sheetName = Optional.ofNullable(sheetName).orElse("");
        error.rowIndex = rowIndex;
        error.columnIndex = columnIndex;
        return error;
    }

    public static ExcelTTypeConvertError valueOf(ExcelTTypeDescriptor descriptor) {
        ExcelTTypeConvertError error = new ExcelTTypeConvertError();
        error.name = Optional.ofNullable(descriptor.getName()).orElse("");
        error.originType = descriptor.getOriginType();
        error.format = Optional.ofNullable(descriptor.getFormat()).orElse(TtypeEnum.STRING.value());
        error.targetType = descriptor.getTargetType();
        error.value = descriptor.getValue();
        error.sheetName = Optional.ofNullable(descriptor.getSheetName()).orElse("");
        error.rowIndex = descriptor.getRowIndex();
        error.columnIndex = descriptor.getColumnIndex();
        return error;
    }
}
