package pro.shushi.pamirs.file.api.entity;

import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel导出获取数据上下文
 *
 * @author Adamancy Zhang on 2021-03-31 10:19
 */
public class ExcelExportFetchDataContext {

    /**
     * 当前导出任务
     */
    private final ExcelExportTask exportTask;

    /**
     * 当前导出任务上下文
     */
    private final ExcelDefinitionContext context;

    /**
     * 当前工作表
     */
    private final EasyExcelSheetDefinition sheetDefinition;

    /**
     * 当前区块
     */
    private final EasyExcelBlockDefinition blockDefinition;

    /**
     * 导出数据集
     */
    private final List<Object> results;

    public ExcelExportFetchDataContext(ExcelExportTask exportTask,
                                       ExcelDefinitionContext context,
                                       EasyExcelSheetDefinition sheetDefinition,
                                       EasyExcelBlockDefinition blockDefinition) {
        this(exportTask, context, sheetDefinition, blockDefinition, new ArrayList<>(4));
    }

    private ExcelExportFetchDataContext(ExcelExportTask exportTask,
                                        ExcelDefinitionContext context,
                                        EasyExcelSheetDefinition sheetDefinition,
                                        EasyExcelBlockDefinition blockDefinition,
                                        List<Object> results) {
        this.exportTask = exportTask;
        this.context = context;
        this.sheetDefinition = sheetDefinition;
        this.blockDefinition = blockDefinition;
        this.results = results;
    }

    public ExcelExportTask getExportTask() {
        return exportTask;
    }

    public ExcelDefinitionContext getContext() {
        return context;
    }

    public EasyExcelSheetDefinition getSheetDefinition() {
        return sheetDefinition;
    }

    public EasyExcelBlockDefinition getBlockDefinition() {
        return blockDefinition;
    }

    public List<Object> getResults() {
        return results;
    }

    public ExcelExportFetchDataContext exchange(EasyExcelSheetDefinition sheetDefinition, EasyExcelBlockDefinition blockDefinition) {
        return new ExcelExportFetchDataContext(this.exportTask, this.context, sheetDefinition, blockDefinition, this.results);
    }
}
