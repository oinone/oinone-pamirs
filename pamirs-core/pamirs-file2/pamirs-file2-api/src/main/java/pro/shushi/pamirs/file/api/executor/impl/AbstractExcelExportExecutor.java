package pro.shushi.pamirs.file.api.executor.impl;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.executor.ExcelExportExecutor;
import pro.shushi.pamirs.file.api.util.ExcelWorkbookDefinitionUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 抽象Excel导出执行器
 *
 * @author Adamancy Zhang at 18:46 on 2024-03-28
 */
public abstract class AbstractExcelExportExecutor implements ExcelExportExecutor {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected Workbook withTemplateStream(ExcelDefinitionContext context, StreamConsumer<ByteArrayInputStream> consumer) throws IOException {
        Workbook workbook = ExcelWorkbookDefinitionUtil.createExportTemplate(context);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
                consumer.accept(inputStream);
                return workbook;
            }
        }
    }
}
