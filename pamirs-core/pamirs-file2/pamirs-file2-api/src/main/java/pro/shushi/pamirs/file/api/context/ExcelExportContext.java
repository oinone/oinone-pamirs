package pro.shushi.pamirs.file.api.context;

import com.alibaba.excel.ExcelWriter;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;

/**
 * @author Adamancy Zhang
 * @date 2021-01-21 19:37
 */
public class ExcelExportContext extends AbstractExportContext {

    private final ExcelWriter writer;

    public ExcelExportContext(ExcelWriter writer, ExcelDefinitionContext definitionContext, ExcelExportTask exportTask) {
        super(definitionContext, exportTask);
        this.writer = writer;
    }

    public ExcelWriter getWriter() {
        return writer;
    }
}
