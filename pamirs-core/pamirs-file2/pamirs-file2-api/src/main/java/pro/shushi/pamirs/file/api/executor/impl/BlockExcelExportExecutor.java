package pro.shushi.pamirs.file.api.executor.impl;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelExportStrategyEnum;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelSheetDefinition;
import pro.shushi.pamirs.file.api.entity.ExcelExportFetchDataContext;
import pro.shushi.pamirs.file.api.executor.ExcelExportExecutor;
import pro.shushi.pamirs.file.api.extpoint.ExcelBlockFetchDataExtPoint;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.meta.api.Ext;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 区块Excel导出执行器
 *
 * @author Adamancy Zhang at 18:41 on 2024-03-28
 */
@Order
@Component
@SPI.Service(ExcelExportStrategyEnum.block)
public class BlockExcelExportExecutor extends StandardExcelExportExecutor implements ExcelExportExecutor {

    @Override
    public Workbook doExport(ExcelExportTask exportTask, ExcelDefinitionContext context, StreamConsumer<ByteArrayOutputStream> consumer) throws IOException {
        return super.doExport(exportTask, context, consumer);
    }

    @Override
    protected List<Object> fetchExportData(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        ExcelExportFetchDataContext fetchDataContext = null;
        List<EasyExcelSheetDefinition> sheetList = context.getSheetList();
        for (EasyExcelSheetDefinition sheetDefinition : sheetList) {
            List<EasyExcelBlockDefinition> blockDefinitions = sheetDefinition.getBlockDefinitions();
            for (EasyExcelBlockDefinition blockDefinition : blockDefinitions) {
                if (fetchDataContext == null) {
                    fetchDataContext = new ExcelExportFetchDataContext(exportTask, context, sheetDefinition, blockDefinition);
                } else {
                    fetchDataContext = fetchDataContext.exchange(sheetDefinition, blockDefinition);
                }
                fetchBlockData(fetchDataContext);
            }
        }
        if (fetchDataContext != null) {
            return fetchDataContext.getResults();
        }
        return null;
    }

    protected void fetchBlockData(ExcelExportFetchDataContext context) {
        Object result = Ext.run(ExcelBlockFetchDataExtPoint::fetchExportData, context);
        if (result == null) {
            result = generatorEmptyData(context);
        }
        context.getResults().add(result);
    }

    private Object generatorEmptyData(ExcelExportFetchDataContext context) {
        ExcelAnalysisTypeEnum analysisType = context.getBlockDefinition().getAnalysisType();
        switch (analysisType) {
            case FIXED_HEADER:
                return new HashMap<>();
            case FIXED_FORMAT:
                return new ArrayList<>();
            default:
                throw new IllegalArgumentException("Invalid block analysis type. type = " + analysisType);
        }
    }
}
