package pro.shushi.pamirs.file.api.easyexcel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import pro.shushi.pamirs.core.common.StringHelper;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class EasyExcelTimestampConverter implements Converter<Timestamp> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return Timestamp.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Timestamp convertToJavaData(ReadCellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return null;
    }

    @Override
    public WriteCellData<String> convertToExcelData(Timestamp value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        WriteCellData<String> cellData = new WriteCellData<>();
        cellData.setType(CellDataTypeEnum.STRING);
        String data = StringHelper.valueOf(value);
        cellData.setData(data);
        cellData.setStringValue(data);
        cellData.setNumberValue(new BigDecimal(value.getTime()));
        return cellData;
    }
}
