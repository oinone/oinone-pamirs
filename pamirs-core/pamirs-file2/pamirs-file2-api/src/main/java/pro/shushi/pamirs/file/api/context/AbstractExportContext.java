package pro.shushi.pamirs.file.api.context;

import pro.shushi.pamirs.file.api.model.ExcelExportTask;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2021-01-21 19:34
 */
public abstract class AbstractExportContext {

    private final ExcelDefinitionContext definitionContext;

    private final ExcelExportTask exportTask;

    private List<Object> dataList;

    protected AbstractExportContext(ExcelDefinitionContext definitionContext, ExcelExportTask exportTask) {
        this.definitionContext = definitionContext;
        this.exportTask = exportTask;
    }

    public ExcelDefinitionContext getDefinitionContext() {
        return definitionContext;
    }

    public ExcelExportTask getExportTask() {
        return exportTask;
    }

    public List<Object> getDataList() {
        return dataList;
    }

    public void setDataList(List<Object> dataList) {
        this.dataList = dataList;
    }

    public AbstractExportContext addData(Object data) {
        if (this.dataList == null) {
            this.dataList = new ArrayList<>();
        }
        dataList.add(data);
        return this;
    }
}
