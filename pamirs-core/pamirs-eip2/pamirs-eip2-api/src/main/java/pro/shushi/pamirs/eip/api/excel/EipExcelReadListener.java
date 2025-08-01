package pro.shushi.pamirs.eip.api.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.data.DataFormatData;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.eip.api.type.converter.ExcelTTypeBoolConverter;
import pro.shushi.pamirs.eip.api.type.converter.ExcelTTypeDateTimeConverter;
import pro.shushi.pamirs.eip.api.type.converter.ExcelTTypeMoneyConverter;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * EipExcelReadListener
 *
 * @author yakir on 2024/10/31 18:12.
 */
@Slf4j
public class EipExcelReadListener extends AnalysisEventListener<Map<Integer, String>> {

    private final EipExcel excel;
    private final int limit;
    private final Map<String, AtomicInteger> sheetLimtMap = new HashMap<>();

    public EipExcelReadListener() {
        this(-1);
    }

    public EipExcelReadListener(int limit) {
        this.excel = new EipExcel();
        this.limit = limit;
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {

        ReadSheetHolder sheet = context.readSheetHolder();
        // int sheetIdx = sheet.getSheetNo();
        // excel sheet名称不能重复 故未使用sheet索引
        String sheetName = sheet.getSheetName();

        excel.addTotal(sheet.getApproximateTotalRowNumber());

        if (StringUtils.isBlank(sheetName)) {
            sheetName = "sheet";
        }

        for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
            ReadCellData<?> cellData = entry.getValue();
            int cellIndex = cellData.getColumnIndex();
            EipExcelHead head = new EipExcelHead();
            head.setIndex(cellIndex);
            head.setType(TtypeEnum.STRING.value());
            head.setName(cellData.getStringValue());

            excel.computeIfAbsentSheet(sheetName)
                    .addHead(head);
        }
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {

        ReadSheetHolder sheet = context.readSheetHolder();
        ReadRowHolder row = context.readRowHolder();
        String sheetName = sheet.getSheetName();
        if (StringUtils.isBlank(sheetName)) {
            sheetName = "sheet";
        }

        int idx = row.getRowIndex();
        EipExcelEntry excelEntry = new EipExcelEntry();
        excelEntry.setIndex(idx);

        List<String> rowData = new ArrayList<>(data.size());

        for (Map.Entry<Integer, String> entry : data.entrySet()) {
            rowData.add(entry.getValue());
        }

        excelEntry.setData(rowData);

        boolean headReady = excel.getSheet(sheetName).headReady();
        if (!headReady) {
            Map<Integer, Cell> headMap = row.getCellMap();
            for (Map.Entry<Integer, Cell> entry : headMap.entrySet()) {
                ReadCellData<?> cellData = (ReadCellData<?>) entry.getValue();
                int cellIndex = cellData.getColumnIndex();
                CellDataTypeEnum cellType = cellData.getType();
                String ttype = ttype(cellData.getStringValue(), cellType);
                EipExcelHead excelHead = excel.getSheet(sheetName).getHead(cellIndex);
                if (null != excelHead) {
                    excelHead.setType(ttype);
                    DataFormatData fmtData = cellData.getDataFormatData();
                    if (null == fmtData) {
                        continue;
                    }
                    String format = fmtData.getFormat();
                    excelHead.setFormat(format);
                }
            }
            excel.getSheet(sheetName).setHeadReady();
        }

        excel.computeIfAbsentSheet(sheetName)
                .addEntry(excelEntry);
    }

    /**
     * 代码来自 easy excel
     *
     * @see com.alibaba.excel.constant.BuiltinFormats
     */
    private final Set<String> sets = Sets.newHashSet(

            "yyyy/m/d",
            // 15
            "d-mmm-yy",
            // 16
            "d-mmm",
            // 17
            "mmm-yy",
            // 18
            "h:mm AM/PM",
            // 19
            "h:mm:ss AM/PM",
            // 20
            "h:mm",
            // 21
            "h:mm:ss",
            // 22
            // The official documentation shows "m/d/yy h:mm", but the actual test is "yyyy-m-d h:mm".
            "yyyy-m-d h:mm",
            // 23-26 No specific correspondence found in the official documentation.
            // 23
            //null,
            // 24
            //null,
            // 25
            //null,
            // 26
            //null,
            // 27
            "yyyy\"年\"m\"月\"",
            // 28
            "m\"月\"d\"日\"",
            // 29
            "m\"月\"d\"日\"",
            // 30
            "m-d-yy",
            // 31
            "yyyy\"年\"m\"月\"d\"日\"",
            // 32
            "h\"时\"mm\"分\"",
            // 33
            "h\"时\"mm\"分\"ss\"秒\"",
            // 34
            "上午/下午h\"时\"mm\"分\"",
            // 35
            "上午/下午h\"时\"mm\"分\"ss\"秒\"",
            // 36
            "yyyy\"年\"m\"月\"",
            "mm:ss",
            // 46
            "[h]:mm:ss",
            // 47
            "mm:ss.0",
            // 48
            "##0.0E+0",
            // 49
            "@",
            // 50
            "yyyy\"年\"m\"月\"",
            // 51
            "m\"月\"d\"日\"",
            // 52
            "yyyy\"年\"m\"月\"",
            // 53
            "m\"月\"d\"日\"",
            // 54
            "m\"月\"d\"日\"",
            // 55
            "上午/下午h\"时\"mm\"分\"",
            // 56
            "上午/下午h\"时\"mm\"分\"ss\"秒\"",
            // 57
            "yyyy\"年\"m\"月\"",
            // 58
            "m\"月\"d\"日\""
    );

    @Override
    public boolean hasNext(AnalysisContext context) {
        if (limit <= -1) {
            return true;
        }

        ReadSheetHolder sheetHolder = context.readSheetHolder();
        String sheetName = sheetHolder.getSheetName();
        int count = sheetLimtMap.computeIfAbsent(sheetName, v -> new AtomicInteger())
                .getAndIncrement();

        return count != limit;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

        // do nothing ...

    }

    private String ttype(String value, CellDataTypeEnum cellType) {
        if (StringUtils.isBlank(value)) {
            return ttype(cellType);
        }
        if (ExcelTTypeBoolConverter.originIsBool(value)) {
            return TtypeEnum.BOOLEAN.value();
        }
        if (ExcelTTypeMoneyConverter.originIsNumber(value)) {
            return TtypeEnum.MONEY.value();
        }
        if (ExcelTTypeDateTimeConverter.originIsDate(value)) {
            return TtypeEnum.DATETIME.value();
        }
        return ttype(cellType);
    }

    private String ttype(CellDataTypeEnum cellType) {
        switch (cellType) {
            case STRING:
            case DIRECT_STRING:
            case EMPTY:
            case ERROR:
                return TtypeEnum.STRING.value();
            case NUMBER:
                return TtypeEnum.MONEY.value();
            case DATE:
                return TtypeEnum.DATETIME.value();
            case BOOLEAN:
                return TtypeEnum.BOOLEAN.value();
            case RICH_TEXT_STRING:
                return TtypeEnum.HTML.value();
            default:
                log.warn("未匹配CellType:{}", cellType);
                return TtypeEnum.STRING.value();
        }
    }

    public EipExcel getExcel() {
        return excel;
    }
}
