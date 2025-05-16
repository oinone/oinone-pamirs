package pro.shushi.pamirs.file.api.service;

import pro.shushi.pamirs.core.common.function.lambda.PamirsSupplier;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.function.ExcelReadCallback;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;

/**
 * @author Adamancy Zhang at 17:28 on 2024-03-28
 */
public interface ExcelImportService {

    /**
     * 根据工作簿定义将导入任务中的文件下载并读取
     *
     * @param importTask       导入任务
     * @param context          导入上下文
     * @param callbackSupplier 读取回调提供者
     */
    boolean doImport(ExcelImportTask importTask, ExcelDefinitionContext context, PamirsSupplier<ExcelReadCallback> callbackSupplier);

}
