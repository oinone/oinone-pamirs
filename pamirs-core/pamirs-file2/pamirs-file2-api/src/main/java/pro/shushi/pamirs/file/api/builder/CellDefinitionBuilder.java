package pro.shushi.pamirs.file.api.builder;

import pro.shushi.pamirs.core.common.builder.BuilderHelper;
import pro.shushi.pamirs.core.common.builder.IBuilder;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;
import pro.shushi.pamirs.file.api.model.ExcelCellDefinition;
import pro.shushi.pamirs.file.api.model.ExcelStyleDefinition;

import java.util.function.Consumer;

public class CellDefinitionBuilder<T> extends AbstractBaseBuilder<T> implements IBuilder<ExcelCellDefinition> {

    private String field;

    private String value;

    private ExcelValueTypeEnum type;

    private String format;

    private Boolean translate;

    private boolean isStatic = false;

    private boolean autoSizeColumn = false;

    private IBuilder<ExcelStyleDefinition> styleBuilder;

    public CellDefinitionBuilder<T> setField(String field) {
        this.field = field;
        return this;
    }

    public CellDefinitionBuilder<T> setValue(String value) {
        this.value = value;
        return this;
    }

    public CellDefinitionBuilder<T> setType(ExcelValueTypeEnum type) {
        this.type = type;
        return this;
    }

    public CellDefinitionBuilder<T> setFormat(String format) {
        this.format = format;
        return this;
    }

    public CellDefinitionBuilder<T> setTranslate(Boolean translate) {
        this.translate = translate;
        return this;
    }

    public CellDefinitionBuilder<T> setIsStatic(Boolean isStatic) {
        this.isStatic = Boolean.TRUE.equals(isStatic);
        return this;
    }

    public CellDefinitionBuilder<T> setAutoSizeColumn(Boolean autoSizeColumn) {
        this.autoSizeColumn = Boolean.TRUE.equals(autoSizeColumn);
        return this;
    }

    public CellDefinitionBuilder<T> setStyleBuilder(IBuilder<ExcelStyleDefinition> styleBuilder) {
        this.styleBuilder = styleBuilder;
        return this;
    }

    public StyleDefinitionBuilder<CellDefinitionBuilder<T>> createStyle() {
        StyleDefinitionBuilder<CellDefinitionBuilder<T>> builder = new StyleDefinitionBuilder<>(this);
        this.styleBuilder = builder;
        return builder;
    }

    public CellDefinitionBuilder<T> apply(Consumer<CellDefinitionBuilder<T>> consumer) {
        consumer.accept(this);
        return this;
    }

    public CellDefinitionBuilder(T builder) {
        super(builder);
    }

    @Override
    public ExcelCellDefinition build() {
        return new ExcelCellDefinition()
                .setField(field)
                .setValue(value)
                .setType(type)
                .setFormat(format)
                .setTranslate(translate)
                .setIsStatic(isStatic)
                .setAutoSizeColumn(autoSizeColumn)
                .setStyle(BuilderHelper.build(styleBuilder));
    }
}
