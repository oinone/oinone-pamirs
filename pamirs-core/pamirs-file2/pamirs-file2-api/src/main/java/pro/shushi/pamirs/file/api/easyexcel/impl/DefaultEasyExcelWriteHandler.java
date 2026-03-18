package pro.shushi.pamirs.file.api.easyexcel.impl;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import com.alibaba.excel.write.handler.context.SheetWriteHandlerContext;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.easyexcel.ExcelWriteHandlerExtendApi;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelDirectionEnum;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.ExcelCellRangeDefinition;
import pro.shushi.pamirs.file.api.util.analysis.ExcelFixedHeaderAnalysisHelper;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;

import java.util.ArrayList;
import java.util.List;

/**
 * <h>默认EasyExcel写入处理器</h>
 * <p>
 * 1、获取当前正在处理的workbook及sheet。{@link DefaultEasyExcelWriteHandler#afterCellCreate(WriteSheetHolder, WriteTableHolder, Cell, Head, Integer, Boolean)}
 * 2、获取当前行所在的块列表。{@link DefaultEasyExcelWriteHandler#beforeRowCreate(WriteSheetHolder, WriteTableHolder, Integer, Integer, Boolean)}
 * </p>
 *
 * @author Adamancy Zhang at 10:29 on 2021-08-16
 */
public class DefaultEasyExcelWriteHandler implements SheetWriteHandler, RowWriteHandler, CellWriteHandler {

    private final ExcelDefinitionContext context;

    private EasyExcelSheetDefinition currentSheet;

    private List<EasyExcelBlockDefinition> currentBlocks;

    private ExcelWriteHandlerExtendApi extendApi;

    public DefaultEasyExcelWriteHandler(ExcelDefinitionContext context) {
        this.context = context;
        for (ExcelWriteHandlerExtendApi extendApi : BeanDefinitionUtils.getBeansOfTypeByOrdered(ExcelWriteHandlerExtendApi.class)) {
            if (extendApi.match(context)) {
                this.extendApi = extendApi;
                break;
            }
        }
    }

    @Override
    public void beforeSheetCreate(SheetWriteHandlerContext context) {
        if (extendApi != null) {
            extendApi.beforeSheetCreate(context);
        }
    }

    @Override
    public void afterSheetCreate(SheetWriteHandlerContext context) {
        afterSheetCreate(context.getWriteWorkbookHolder(), context.getWriteSheetHolder());
        if (extendApi != null) {
            extendApi.afterSheetCreate(context);
        }
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Integer sheetNo = writeSheetHolder.getSheetNo();
        List<EasyExcelSheetDefinition> sheetList = context.getSheetList();
        if (sheetNo != null && sheetNo <= sheetList.size() - 1) {
            currentSheet = context.getSheetList().get(sheetNo);
        }
        if (currentSheet == null) {
            String sheetName = writeSheetHolder.getSheetName();
            if (StringUtils.isNotBlank(sheetName)) {
                for (EasyExcelSheetDefinition sheet : sheetList) {
                    if (sheetName.equals(sheet.getName())) {
                        currentSheet = sheet;
                        break;
                    }
                }
            }
        }
        //todo
        if (currentSheet == null) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(I18nUtils.getMessage("pamirs.file.excel.sheet.notFound")).errThrow();
        }
        currentSheet.setWorkbook(writeWorkbookHolder.getCachedWorkbook());
        currentSheet.setSheet(writeSheetHolder.getCachedSheet());
    }

    @Override
    public void beforeRowCreate(RowWriteHandlerContext context) {
        beforeRowCreate(context.getWriteSheetHolder(), context.getWriteTableHolder(), context.getRowIndex(),
                context.getRelativeRowIndex(), context.getHead());
        if (extendApi != null) {
            extendApi.beforeRowCreate(context);
        }
    }

    @Override
    public void beforeRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Integer rowIndex, Integer relativeRowIndex, Boolean isHead) {
        // TODO: zbh 20210816 暂不支持垂直排列-横向填充的块偏移
        List<EasyExcelBlockDefinition> blockDefinitions = currentSheet.getBlockDefinitions();
        int initialCapacity = blockDefinitions.size();
        this.currentBlocks = new ArrayList<>(initialCapacity);
        for (EasyExcelBlockDefinition blockDefinition : blockDefinitions) {
            if (ExcelAnalysisTypeEnum.FIXED_HEADER.equals(blockDefinition.getAnalysisType()) && ExcelDirectionEnum.HORIZONTAL.equals(blockDefinition.getDirection())) {
                ExcelCellRangeDefinition currentRange = blockDefinition.getCurrentRange();
                int currentNextRowIndex = currentRange.getEndRowIndex() + 1;
                if (rowIndex.equals(currentNextRowIndex)) {
                    //添加到当前块列表中
                    this.currentBlocks.add(blockDefinition);
                }
            }
        }
    }

    @Override
    public void afterRowCreate(RowWriteHandlerContext context) {
        afterRowCreate(context.getWriteSheetHolder(), context.getWriteTableHolder(), context.getRow(),
                context.getRelativeRowIndex(), context.getHead());
        if (extendApi != null) {
            extendApi.afterRowCreate(context);
        }
    }

    @Override
    public void afterRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        // TODO: zbh 20210816 暂不支持垂直排列-横向填充的块循环合并
        int rowIndex = row.getRowNum();
        for (EasyExcelBlockDefinition currentBlock : this.currentBlocks) {
            ExcelFixedHeaderAnalysisHelper.afterRowCreateInCurrentBlock(currentSheet, currentBlock, rowIndex);
        }
    }

    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        if (extendApi != null) {
            extendApi.afterRowDispose(context);
        }
    }

    @Override
    public void beforeCellCreate(CellWriteHandlerContext context) {
        if (extendApi != null) {
            extendApi.beforeCellCreate(context);
        }
    }

    @Override
    public void afterCellCreate(CellWriteHandlerContext context) {
        if (extendApi != null) {
            extendApi.afterCellCreate(context);
        }
    }

    @Override
    public void afterCellDataConverted(CellWriteHandlerContext context) {
        if (extendApi != null) {
            extendApi.afterCellDataConverted(context);
        }
    }

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        if (extendApi != null) {
            extendApi.afterCellDispose(context);
        }
    }

    public void extendBuilder(ExcelWriterBuilder builder) {
        if (extendApi != null) {
            extendApi.extendBuilder(builder);
        }
    }
}
