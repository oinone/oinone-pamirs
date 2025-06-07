package pro.shushi.pamirs.file.api.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.enmu.ExcelExportStrategyEnum;
import pro.shushi.pamirs.file.api.exception.ExcelTemplateException;
import pro.shushi.pamirs.file.api.executor.ExcelExportExecutor;
import pro.shushi.pamirs.file.api.executor.impl.StreamConsumer;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.service.ExcelExportService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Excel导出服务
 *
 * @author Adamancy Zhang at 18:21 on 2024-03-28
 */
@Service(ExcelExportServiceImpl.BEAN_NAME)
public class ExcelExportServiceImpl implements ExcelExportService {

    public static final String BEAN_NAME = "excelExportServiceImpl";

    @Override
    public Workbook doExport(ExcelExportTask exportTask, ExcelDefinitionContext context, StreamConsumer<ByteArrayOutputStream> consumer) throws IOException {
        return doExportStrategy(exportTask, context, consumer);
    }

    protected Workbook doExportStrategy(ExcelExportTask exportTask, ExcelDefinitionContext context, StreamConsumer<ByteArrayOutputStream> consumer) throws IOException {
        if (!doExportPrepare(exportTask, context)) {
            return null;
        }
        return Spider.getExtension(ExcelExportExecutor.class, Optional.ofNullable(context.getExportStrategy()).orElse(ExcelExportStrategyEnum.STANDARD).value())
                .doExport(exportTask, context, consumer);
    }

    protected boolean doExportPrepare(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        Long templateId = context.getTemplateId();
        if (templateId == null) {
            return true;
        }
        List<ExcelSheetDefinition> sheetList = Optional.of(Models.origin().queryOneByWrapper(Pops.<ExcelWorkbookDefinition>lambdaQuery()
                        .from(ExcelWorkbookDefinition.MODEL_MODEL)
                        .eq(ExcelWorkbookDefinition::getId, templateId)))
                .map(v -> {
                    v.analysisSheetDefinitions();
                    return v;
                })
                .map(ExcelWorkbookDefinition::getSheetList)
                .filter(CollectionUtils::isNotEmpty)
                .orElse(null);
        if (sheetList == null) {
            throw new ExcelTemplateException("The template does not have a sheet definition");
        }
        context.setOriginSheetList(sheetList);
        return true;
    }
}
