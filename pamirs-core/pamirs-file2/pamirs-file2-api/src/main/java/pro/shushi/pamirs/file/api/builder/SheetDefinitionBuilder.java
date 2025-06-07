package pro.shushi.pamirs.file.api.builder;

import pro.shushi.pamirs.core.common.builder.BuilderHelper;
import pro.shushi.pamirs.core.common.builder.IBuilder;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelDirectionEnum;
import pro.shushi.pamirs.file.api.model.ExcelBlockDefinition;
import pro.shushi.pamirs.file.api.model.ExcelCellRangeDefinition;
import pro.shushi.pamirs.file.api.model.ExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.ExcelUniqueDefinition;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ExcelSheetDefinition}构建
 *
 * @author Adamancy Zhang at 15:42 on 2021-08-17
 */
@Slf4j
public class SheetDefinitionBuilder extends AbstractBaseBuilder<WorkbookDefinitionBuilder> implements IBuilder<ExcelSheetDefinition> {

    private String name;

    private boolean autoSizeColumn = true;

    private boolean onceFetchSheetData = true;

    private final List<IBuilder<ExcelBlockDefinition>> blockBuilderList = new ArrayList<>();

    private final List<ExcelCellRangeDefinition> mergeRangeList = new ArrayList<>();

    private final List<IBuilder<ExcelUniqueDefinition>> uniqueBuilderList = new ArrayList<>();

    public SheetDefinitionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public SheetDefinitionBuilder setAutoSizeColumn(boolean autoSizeColumn) {
        this.autoSizeColumn = autoSizeColumn;
        return this;
    }

    public SheetDefinitionBuilder setOnceFetchSheetData(boolean onceFetchSheetData) {
        this.onceFetchSheetData = onceFetchSheetData;
        return this;
    }

    public SheetDefinitionBuilder(WorkbookDefinitionBuilder builder) {
        super(builder);
    }

    public List<IBuilder<ExcelBlockDefinition>> getBlockBuilderList() {
        return blockBuilderList;
    }

    /**
     * @see SheetDefinitionBuilder#createBlock(String, ExcelAnalysisTypeEnum, ExcelDirectionEnum, String)
     * @deprecated 2.3.0
     */
    @Deprecated
    public BlockDefinitionBuilder createBlock(String bindingModel, ExcelAnalysisTypeEnum analysisType, ExcelDirectionEnum direction,
                                              Integer beginRowIndex, Integer endRowIndex, Integer beginColumnIndex, Integer endColumnIndex) {
        BlockDefinitionBuilder builder = new BlockDefinitionBuilder(this, bindingModel, analysisType, direction, beginRowIndex, endRowIndex, beginColumnIndex, endColumnIndex);
        blockBuilderList.add(builder);
        return builder;
    }

    public BlockDefinitionBuilder createBlock(String bindingModel, ExcelAnalysisTypeEnum analysisType, ExcelDirectionEnum direction, String coordinate) {
        BlockDefinitionBuilder builder = new BlockDefinitionBuilder(this, bindingModel, analysisType, direction, coordinate);
        blockBuilderList.add(builder);
        return builder;
    }

    /**
     * @see SheetDefinitionBuilder#createMergeRange(String)
     * @deprecated 2.3.0
     */
    @Deprecated
    public SheetDefinitionBuilder createMergeRange(int beginRowIndex, int endRowIndex, int beginColumnIndex, int endColumnIndex) {
        mergeRangeList.add(new ExcelCellRangeDefinition(beginRowIndex, endRowIndex, beginColumnIndex, endColumnIndex));
        return this;
    }

    public SheetDefinitionBuilder createMergeRange(String coordinate) {
        this.mergeRangeList.add(new ExcelCellRangeDefinition(coordinate));
        return this;
    }

    public UniqueDefinitionBuilder<SheetDefinitionBuilder> createUnique(String model) {
        UniqueDefinitionBuilder<SheetDefinitionBuilder> builder = new UniqueDefinitionBuilder<>(this, model);
        uniqueBuilderList.add(builder);
        return builder;
    }

    @Override
    public ExcelSheetDefinition build() {
        List<ExcelBlockDefinition> blockDefinitionList = BuilderHelper.batchBuild(blockBuilderList);
        if (blockDefinitionList == null) {
            throw new NullPointerException();
        }
        if (onceFetchSheetData) {
            String model = null;
            boolean isContainsFixedFormat = false, isSameModel = true;
            for (ExcelBlockDefinition blockDefinition : blockDefinitionList) {
                if (ExcelAnalysisTypeEnum.FIXED_FORMAT.equals(blockDefinition.getAnalysisType())) {
                    isContainsFixedFormat = true;
                }
                if (model == null) {
                    model = blockDefinition.getBindingModel();
                } else {
                    if (!model.equals(blockDefinition.getBindingModel())) {
                        isSameModel = false;
                    }
                }
            }
            if (isContainsFixedFormat) {
                if (!isSameModel) {
                    throw new IllegalArgumentException("Invalid sheet definition. all block analysis type must be the same.");
                }
            } else {
                onceFetchSheetData = false;
                log.debug("Invalid onceFetchSheetData. cause: the block list does not contain a block of analysis type ExcelAnalysisTypeEnum#FIXED_FORMAT.");
            }
        }
        return new ExcelSheetDefinition()
                .setName(name)
                .setAutoSizeColumn(autoSizeColumn)
                .setOnceFetchData(onceFetchSheetData)
                .setBlockDefinitionList(blockDefinitionList)
                .setMergeRangeList(mergeRangeList)
                .setUniqueDefinitions(BuilderHelper.batchBuild(uniqueBuilderList));
    }
}
