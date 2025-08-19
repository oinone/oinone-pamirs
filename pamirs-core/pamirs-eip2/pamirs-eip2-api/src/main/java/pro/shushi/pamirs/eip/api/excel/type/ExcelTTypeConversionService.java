package pro.shushi.pamirs.eip.api.excel.type;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.excel.type.converter.*;

import java.util.List;

/**
 * @author Gesi at 15:30 on 2025/7/18
 */
@Component
public class ExcelTTypeConversionService {

    private final List<ExcelTTypeConverter> converters;

    @Autowired
    public ExcelTTypeConversionService(List<ExcelTTypeConverter> converters) {
        this.converters = converters;
    }

    public String convert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        if (excelTTypeDescriptor.getOriginType() == null) {
            excelTTypeDescriptor.setOriginType("");
        }
        if (excelTTypeDescriptor.getTargetType() == null) {
            excelTTypeDescriptor.setTargetType("");
        }
        if (CollectionUtils.isNotEmpty(this.converters)) {
            for (ExcelTTypeConverter converter : this.converters) {
                if (converter.canConvert(excelTTypeDescriptor)) {
                    return converter.convert(excelTTypeDescriptor);
                }
            }
        }
        return null;
    }

    public static ExcelTTypeConversionService loadDefaultConversionService() {
        return new ExcelTTypeConversionService(Lists.newArrayList(
                new ExcelTTypeBoolConverter(),
                new ExcelTTypeDateConverter(),
                new ExcelTTypeDateTimeConverter(),
                new ExcelTTypeIntegerConverter(),
                new ExcelTTypeMoneyConverter(),
                new ExcelTTypeStringConverter(),
                new ExcelTTypeTimeConverter(),
                new ExcelTTypeYearConverter()
        ));
    }

}
