package pro.shushi.pamirs.file.api.builder;

import pro.shushi.pamirs.core.common.builder.BuilderHelper;
import pro.shushi.pamirs.core.common.builder.IBuilder;
import pro.shushi.pamirs.file.api.model.ExcelCellDefinition;
import pro.shushi.pamirs.file.api.model.ExcelHeaderDefinition;
import pro.shushi.pamirs.file.api.model.ExcelStyleDefinition;

import java.util.ArrayList;
import java.util.List;

public class HeaderDefinitionBuilder extends AbstractBaseBuilder<BlockDefinitionBuilder> implements IBuilder<ExcelHeaderDefinition> {

    private final List<IBuilder<ExcelCellDefinition>> cellBuilderList = new ArrayList<>();

    private IBuilder<ExcelStyleDefinition> styleBuilder;

    private boolean isConfig = false;

    private boolean isFrozen = false;

    public HeaderDefinitionBuilder setIsConfig(boolean isConfig) {
        this.isConfig = isConfig;
        return this;
    }

    public HeaderDefinitionBuilder setIsFrozen(boolean isFrozen) {
        this.isFrozen = isFrozen;
        return this;
    }

    public HeaderDefinitionBuilder setStyleBuilder(IBuilder<ExcelStyleDefinition> styleBuilder) {
        this.styleBuilder = styleBuilder;
        return this;
    }

    public CellDefinitionBuilder<HeaderDefinitionBuilder> createCell() {
        CellDefinitionBuilder<HeaderDefinitionBuilder> builder = new CellDefinitionBuilder<>(this);
        cellBuilderList.add(builder);
        return builder;
    }

    public StyleDefinitionBuilder<HeaderDefinitionBuilder> createStyle() {
        StyleDefinitionBuilder<HeaderDefinitionBuilder> builder = new StyleDefinitionBuilder<>(this);
        styleBuilder = builder;
        return builder;
    }

    public int cellSize(){
        return cellBuilderList.size();
    }

    public HeaderDefinitionBuilder(BlockDefinitionBuilder builder) {
        super(builder);
    }

    @Override
    public ExcelHeaderDefinition build() {
        return (ExcelHeaderDefinition) new ExcelHeaderDefinition()
                .setIsConfig(isConfig)
                .setIsFrozen(isFrozen)
                .setCellList(BuilderHelper.batchBuild(cellBuilderList))
                .setStyle(BuilderHelper.build(styleBuilder));
    }
}
