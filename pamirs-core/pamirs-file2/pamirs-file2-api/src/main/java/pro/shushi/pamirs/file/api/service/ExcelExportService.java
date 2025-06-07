package pro.shushi.pamirs.file.api.service;

import org.apache.poi.ss.usermodel.Workbook;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.executor.impl.StreamConsumer;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Excel导出服务
 *
 * @author Adamancy Zhang at 17:28 on 2024-03-28
 */
public interface ExcelExportService {

    /**
     * 根据工作簿定义将数据集填充到工作簿
     *
     * @param exportTask 导出任务
     * @param context    导出上下文
     * @return Excel工作簿
     */
    Workbook doExport(ExcelExportTask exportTask, ExcelDefinitionContext context, StreamConsumer<ByteArrayOutputStream> consumer) throws IOException;

}
