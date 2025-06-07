package pro.shushi.pamirs.file.api.context;

import com.alibaba.excel.ExcelReader;
import pro.shushi.pamirs.file.api.easyexcel.ExcelAnalysisEventListener;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ExcelImportContext {

    private transient final ExcelReader reader;

    private final ExcelDefinitionContext definitionContext;

    private final ExcelImportTask importTask;

    private final List<Object> dataBufferList = new ArrayList<>();

    private final List<ExcelAnalysisEventListener> eventListenerList = new ArrayList<>();

    private final List<List<Map<Integer, String>>> errorDataList = new ArrayList<>();

    private ExcelAnalysisEventListener currentListener;

    private int currentSheetNumber = -1;

    private int currentBlockNumber = -1;

    private int currentRow = -1;

    public ExcelImportContext(ExcelReader reader, ExcelDefinitionContext definitionContext, ExcelImportTask importTask) {
        this.reader = reader;
        this.definitionContext = definitionContext;
        this.importTask = importTask;
    }

    public ExcelReader getReader() {
        return reader;
    }

    public ExcelDefinitionContext getDefinitionContext() {
        return definitionContext;
    }

    public ExcelImportTask getImportTask() {
        return importTask;
    }

    public List<Object> getDataBufferList() {
        return dataBufferList;
    }

    @SuppressWarnings("unchecked")
    public <R> R getDataBuffer(int index, Supplier<R> supplier) {
        while (dataBufferList.size() <= index) {
            dataBufferList.add(null);
        }
        R result = (R) dataBufferList.get(index);
        if (result == null) {
            result = supplier.get();
            dataBufferList.set(index, result);
        }
        return result;
    }

    public List<ExcelAnalysisEventListener> getEventListenerList() {
        return eventListenerList;
    }

    public List<List<Map<Integer, String>>> getErrorDataList() {
        return errorDataList;
    }

    public ExcelAnalysisEventListener getCurrentListener() {
        return currentListener;
    }

    public ExcelImportContext setCurrentListener(ExcelAnalysisEventListener currentListener) {
        this.currentListener = currentListener;
        return this;
    }

    public int getCurrentSheetNumber() {
        return currentSheetNumber;
    }

    public ExcelImportContext setCurrentSheetNumber(int currentSheetNumber) {
        this.currentSheetNumber = currentSheetNumber;
        return this;
    }

    public int getCurrentBlockNumber() {
        return currentBlockNumber;
    }

    public void setCurrentBlockNumber(int currentBlockNumber) {
        this.currentBlockNumber = currentBlockNumber;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }
}
