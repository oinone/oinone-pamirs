package pro.shushi.pamirs.file.api.builder;

import pro.shushi.pamirs.core.common.builder.BuilderHelper;
import pro.shushi.pamirs.core.common.builder.IBuilder;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelDirectionEnum;
import pro.shushi.pamirs.file.api.model.*;
import pro.shushi.pamirs.file.api.util.ExcelCellRangeHelper;

import java.util.ArrayList;
import java.util.List;

public class BlockDefinitionBuilder extends AbstractBaseBuilder<SheetDefinitionBuilder> implements IBuilder<ExcelBlockDefinition> {

    private final ExcelCellRangeDefinition designRange;

    private ExcelAnalysisTypeEnum analysisType;

    private ExcelDirectionEnum direction;

    private String bindingModel;

    private boolean usingCascadingStyle = Boolean.FALSE;

    private int presetNumber = 1;

    private String fetchNamespace;

    private String fetchFun;

    private String domain;

    private final List<IBuilder<ExcelHeaderDefinition>> headerBuilderList = new ArrayList<>();

    private final List<IBuilder<ExcelRowDefinition>> rowBuilderList = new ArrayList<>();

    private final List<ExcelCellRangeDefinition> mergeRangeList = new ArrayList<>();

    private final List<IBuilder<ExcelUniqueDefinition>> uniqueBuilderList = new ArrayList<>();

    /**
     * @deprecated 2.3.0
     */
    @Deprecated
    public BlockDefinitionBuilder(SheetDefinitionBuilder builder, String bindingModel,
                                  ExcelAnalysisTypeEnum analysisType, ExcelDirectionEnum direction,
                                  int beginRowIndex, int endRowIndex, int beginColumnIndex, int endColumnIndex) {
        super(builder);
        this.bindingModel = bindingModel;
        this.analysisType = analysisType;
        this.direction = direction;
        this.designRange = new ExcelCellRangeDefinition(beginRowIndex, endRowIndex, beginColumnIndex, endColumnIndex);
    }

    public BlockDefinitionBuilder(SheetDefinitionBuilder builder, String bindingModel,
                                  ExcelAnalysisTypeEnum analysisType, ExcelDirectionEnum direction,
                                  String coordinate) {
        super(builder);
        this.bindingModel = bindingModel;
        this.analysisType = analysisType;
        this.direction = direction;
        this.designRange = new ExcelCellRangeDefinition(coordinate);
    }

    public BlockDefinitionBuilder modifyDesignRange(String coordinate) {
        ExcelCellRangeDefinition rangeDefinition = ExcelCellRangeHelper.analysis(coordinate);
        if (rangeDefinition == null) {
            throw new IllegalArgumentException("Modify design range error.");
        }
        designRange.setBeginRowIndex(rangeDefinition.getBeginRowIndex());
        designRange.setEndRowIndex(rangeDefinition.getEndRowIndex());
        designRange.setBeginColumnIndex(rangeDefinition.getBeginColumnIndex());
        designRange.setEndColumnIndex(rangeDefinition.getEndColumnIndex());
        return this;
    }

    public BlockDefinitionBuilder modifyDesignRange(Integer beginRowIndex, Integer endRowIndex, Integer beginColumnIndex, Integer endColumnIndex) {
        if (beginRowIndex != null) {
            designRange.setBeginRowIndex(beginRowIndex);
        }
        if (endRowIndex != null) {
            designRange.setEndRowIndex(endRowIndex);
        }
        if (beginColumnIndex != null) {
            designRange.setBeginColumnIndex(beginColumnIndex);
        }
        if (endColumnIndex != null) {
            designRange.setEndColumnIndex(endColumnIndex);
        }
        return this;
    }

    public BlockDefinitionBuilder setFixedDesignRange(Boolean fixedBeginRowIndex, Boolean fixedEndRowIndex, Boolean fixedBeginColumnIndex, Boolean fixedEndColumnIndex) {
        designRange.setFixedBeginRowIndex(fixedBeginRowIndex);
        designRange.setFixedEndRowIndex(fixedEndRowIndex);
        designRange.setFixedBeginColumnIndex(fixedBeginColumnIndex);
        designRange.setFixedEndColumnIndex(fixedEndColumnIndex);
        return this;
    }

    public BlockDefinitionBuilder modifyFixedDesignRange(Boolean fixedBeginRowIndex, Boolean fixedEndRowIndex, Boolean fixedBeginColumnIndex, Boolean fixedEndColumnIndex) {
        if (fixedBeginRowIndex != null) {
            designRange.setFixedBeginRowIndex(fixedBeginRowIndex);
        }
        if (fixedEndRowIndex != null) {
            designRange.setFixedEndRowIndex(fixedEndRowIndex);
        }
        if (fixedBeginColumnIndex != null) {
            designRange.setFixedBeginColumnIndex(fixedBeginColumnIndex);
        }
        if (fixedEndColumnIndex != null) {
            designRange.setFixedEndColumnIndex(fixedEndColumnIndex);
        }
        return this;
    }

    public BlockDefinitionBuilder setAnalysisType(ExcelAnalysisTypeEnum analysisType) {
        this.analysisType = analysisType;
        return this;
    }

    public BlockDefinitionBuilder setDirection(ExcelDirectionEnum direction) {
        this.direction = direction;
        return this;
    }

    public BlockDefinitionBuilder setBindingModel(String bindingModel) {
        this.bindingModel = bindingModel;
        return this;
    }

    public BlockDefinitionBuilder setUsingCascadingStyle(boolean usingCascadingStyle) {
        this.usingCascadingStyle = usingCascadingStyle;
        return this;
    }

    public BlockDefinitionBuilder setPresetNumber(int presetNumber) {
        this.presetNumber = presetNumber;
        return this;
    }

    public BlockDefinitionBuilder setFetchNamespace(String fetchNamespace) {
        this.fetchNamespace = fetchNamespace;
        return this;
    }

    public BlockDefinitionBuilder setFetchFun(String fetchFun) {
        this.fetchFun = fetchFun;
        return this;
    }

    public BlockDefinitionBuilder setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public HeaderDefinitionBuilder createHeader() {
        HeaderDefinitionBuilder builder = new HeaderDefinitionBuilder(this);
        this.headerBuilderList.add(builder);
        return builder;
    }

    public RowDefinitionBuilder<BlockDefinitionBuilder> createRow() {
        RowDefinitionBuilder<BlockDefinitionBuilder> builder = new RowDefinitionBuilder<>(this);
        this.rowBuilderList.add(builder);
        return builder;
    }

    /**
     * @see BlockDefinitionBuilder#createMergeRange(String)
     * @deprecated 2.3.0
     */
    @Deprecated
    public BlockDefinitionBuilder createMergeRange(int beginRowIndex, int endRowIndex, int beginColumnIndex, int endColumnIndex) {
        this.mergeRangeList.add(new ExcelCellRangeDefinition(beginRowIndex, endRowIndex, beginColumnIndex, endColumnIndex));
        return this;
    }

    public BlockDefinitionBuilder createMergeRange(String coordinate) {
        this.mergeRangeList.add(new ExcelCellRangeDefinition(coordinate));
        return this;
    }

    public UniqueDefinitionBuilder<BlockDefinitionBuilder> createUnique(String model) {
        UniqueDefinitionBuilder<BlockDefinitionBuilder> builder = new UniqueDefinitionBuilder<>(this, model);
        this.uniqueBuilderList.add(builder);
        return builder;
    }

    @Override
    public ExcelBlockDefinition build() {
        return new ExcelBlockDefinition()
                .setBindingModel(bindingModel)
                .setAnalysisType(analysisType)
                .setDirection(direction)
                .setDesignRange(designRange)
                .setUsingCascadingStyle(usingCascadingStyle)
                .setPresetNumber(presetNumber)
                .setFetchNamespace(fetchNamespace)
                .setFetchFun(fetchFun)
                .setDomain(domain)
                .setHeaderList(BuilderHelper.batchBuild(headerBuilderList))
                .setRowList(BuilderHelper.batchBuild(rowBuilderList))
                .setMergeRangeList(mergeRangeList)
                .setUniqueDefinitions(BuilderHelper.batchBuild(uniqueBuilderList));
    }
}
