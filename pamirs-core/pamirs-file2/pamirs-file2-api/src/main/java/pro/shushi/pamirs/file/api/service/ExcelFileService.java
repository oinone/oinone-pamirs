package pro.shushi.pamirs.file.api.service;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import pro.shushi.pamirs.core.common.function.lambda.PamirsSupplier;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.function.ExcelReadCallback;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.meta.annotation.Fun;

import java.io.IOException;
import java.util.List;

@Fun(ExcelFileService.FUN_NAMESPACE)
public interface ExcelFileService {

    String FUN_NAMESPACE = "pro.shushi.pamirs.file.api.service.ExcelFileService";

    /**
     * 根据工作簿定义将数据集填充后，上传至OSS，并更新导出任务
     *
     * @param exportTask 导出任务
     * @param context    导出上下文
     */
    Workbook doExport(ExcelExportTask exportTask, ExcelDefinitionContext context);


    /**
     * 同步导出
     *
     * @param exportTask 导出任务
     * @param context    导出上下文
     */
    void doExportSync(ExcelExportTask exportTask, ExcelDefinitionContext context);

    /**
     * 根据工作簿定义将数据集填充后，上传至OSS，并更新导出任务
     *
     * @param exportTask 导出任务
     * @param context    导出上下文
     */
    void doExportAsync(ExcelExportTask exportTask, ExcelDefinitionContext context);

    /**
     * 根据工作簿定义将导入任务中的文件下载并读取
     *
     * @param importTask       导入任务
     * @param context          导入上下文
     * @param callbackSupplier 读取回调提供者
     */
    boolean doImport(ExcelImportTask importTask, ExcelDefinitionContext context, PamirsSupplier<ExcelReadCallback> callbackSupplier);

    /**
     * 根据工作簿定义将导入任务中的文件下载并读取（异步）
     *
     * @param importTask 导入任务
     * @param context    导入上下文
     */
    void doImportAsync(ExcelImportTask importTask, ExcelDefinitionContext context);

    /**
     * 根据文件url及模版创建导入任务并读取
     *
     * @param fileUrl          文件url
     * @param templateName     模版名称
     * @param callbackSupplier 读取回调提供者
     */
    ExcelImportTask doImportByUrl(String fileUrl, String templateName, PamirsSupplier<ExcelReadCallback> callbackSupplier);

    /**
     * 不生成导入记录
     */
    ExcelImportTask doImportByUrlTemporary(String fileUrl, ExcelWorkbookDefinition workbookDefinition, PamirsSupplier<ExcelReadCallback> callbackSupplier);

    /**
     * 刷新定义上下文
     *
     * @param data Excel模板定义
     * @return 定义上下文
     */
    ExcelDefinitionContext refreshDefinitionContext(ExcelWorkbookDefinition data);

    /**
     * 批量刷新定义上下文
     *
     * @param data Excel模板定义
     */
    Boolean refreshDefinitionContextBatch(List<ExcelWorkbookDefinition> data);

    /**
     * 下载导入模板定义
     *
     * @param data Excel模板定义
     */
    void downloadImportTemplate(ExcelWorkbookDefinition data, HttpServletResponse response) throws IOException;
}
