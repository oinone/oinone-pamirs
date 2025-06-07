package pro.shushi.pamirs.file.api.executor.impl;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.enmu.ExcelExportStrategyEnum;
import pro.shushi.pamirs.file.api.executor.ExcelExportExecutor;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 流式Excel导出执行器
 *
 * @author Adamancy Zhang at 18:42 on 2024-03-28
 */
@Order
@Component
@SPI.Service(ExcelExportStrategyEnum.stream)
public class StreamExcelExportExecutor extends AbstractExcelExportExecutor implements ExcelExportExecutor {

    @Override
    public Workbook doExport(ExcelExportTask exportTask, ExcelDefinitionContext context, StreamConsumer<ByteArrayOutputStream> consumer) throws IOException {
        return Spider.getExtension(ExcelExportExecutor.class, ExcelExportStrategyEnum.standard).doExport(exportTask, context, consumer);
    }

}
