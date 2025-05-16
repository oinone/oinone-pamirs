package pro.shushi.pamirs.file.api.context;

import pro.shushi.pamirs.file.api.model.ExcelExportTask;

import java.io.OutputStream;

/**
 * @author Adamancy Zhang
 * @date 2021-01-21 19:33
 */
public class CSVExportContext extends AbstractExportContext {

    private final OutputStream output;

    private int currentRowIndex = 0;

    private int currentColumnIndex = 0;

    public CSVExportContext(OutputStream output, ExcelDefinitionContext definitionContext, ExcelExportTask exportTask) {
        super(definitionContext, exportTask);
        this.output = output;
    }

    public OutputStream getOutput() {
        return output;
    }

    public int getCurrentRowIndex() {
        return currentRowIndex;
    }

    public void setCurrentRowIndex(int currentRowIndex) {
        this.currentRowIndex = currentRowIndex;
    }

    public int getCurrentColumnIndex() {
        return currentColumnIndex;
    }

    public void setCurrentColumnIndex(int currentColumnIndex) {
        this.currentColumnIndex = currentColumnIndex;
    }
}
