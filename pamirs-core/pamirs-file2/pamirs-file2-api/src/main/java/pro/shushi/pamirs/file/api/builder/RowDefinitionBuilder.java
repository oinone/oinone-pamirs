package pro.shushi.pamirs.file.api.builder;

import pro.shushi.pamirs.core.common.builder.BuilderHelper;
import pro.shushi.pamirs.core.common.builder.IBuilder;
import pro.shushi.pamirs.file.api.model.ExcelCellDefinition;
import pro.shushi.pamirs.file.api.model.ExcelRowDefinition;
import pro.shushi.pamirs.file.api.model.ExcelStyleDefinition;

import java.util.ArrayList;
import java.util.List;

public class RowDefinitionBuilder<T> extends AbstractBaseBuilder<T> implements IBuilder<ExcelRowDefinition> {

    private final List<IBuilder<ExcelCellDefinition>> cellBuilderList = new ArrayList<>();

    private IBuilder<ExcelStyleDefinition> styleBuilder;

    public RowDefinitionBuilder<T> setStyleBuilder(IBuilder<ExcelStyleDefinition> styleBuilder) {
        this.styleBuilder = styleBuilder;
        return this;
    }

    public CellDefinitionBuilder<RowDefinitionBuilder<T>> createCell() {
        CellDefinitionBuilder<RowDefinitionBuilder<T>> builder = new CellDefinitionBuilder<>(this);
        cellBuilderList.add(builder);
        return builder;
    }

    public StyleDefinitionBuilder<RowDefinitionBuilder<T>> createStyle() {
        StyleDefinitionBuilder<RowDefinitionBuilder<T>> builder = new StyleDefinitionBuilder<>(this);
        styleBuilder = builder;
        return builder;
    }

    public RowDefinitionBuilder(T builder) {
        super(builder);
    }

    @Override
    public ExcelRowDefinition build() {
        return new ExcelRowDefinition()
                .setCellList(BuilderHelper.batchBuild(cellBuilderList))
                .setStyle(BuilderHelper.build(styleBuilder));
    }
}
