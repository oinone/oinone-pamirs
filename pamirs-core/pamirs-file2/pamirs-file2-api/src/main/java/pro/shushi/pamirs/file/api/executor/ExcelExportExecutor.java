package pro.shushi.pamirs.file.api.executor;

import org.apache.poi.ss.usermodel.Workbook;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.executor.impl.StreamConsumer;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Excel导出策略服务
 *
 * @author Adamancy Zhang at 18:37 on 2024-03-28
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ExcelExportExecutor {

    Workbook doExport(ExcelExportTask exportTask, ExcelDefinitionContext context, StreamConsumer<ByteArrayOutputStream> consumer) throws IOException;

}
