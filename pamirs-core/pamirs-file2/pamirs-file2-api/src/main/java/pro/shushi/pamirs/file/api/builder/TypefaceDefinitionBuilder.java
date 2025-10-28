package pro.shushi.pamirs.file.api.builder;

import pro.shushi.pamirs.core.common.builder.IBuilder;
import pro.shushi.pamirs.file.api.enmu.ExcelTypeOffsetEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelTypefaceEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelUnderlineEnum;
import pro.shushi.pamirs.file.api.model.ExcelTypefaceDefinition;

public class TypefaceDefinitionBuilder<T> extends AbstractBaseBuilder<T> implements IBuilder<ExcelTypefaceDefinition> {

    private ExcelTypefaceEnum typeface = ExcelTypefaceEnum.SONG;

    private String typefaceName;

    private Integer size;

    private Boolean italic = Boolean.FALSE;

    private Boolean strikeout = Boolean.FALSE;

    private Integer color = 0x7fff;

    private ExcelTypeOffsetEnum typeOffset = ExcelTypeOffsetEnum.NORMAL;

    private ExcelUnderlineEnum underline = ExcelUnderlineEnum.NONE;

    private Boolean bold = Boolean.FALSE;

    public TypefaceDefinitionBuilder(T builder) {
        super(builder);
    }

    public static TypefaceDefinitionBuilder<?> newInstance() {
        return new TypefaceDefinitionBuilder<>(null);
    }

    public TypefaceDefinitionBuilder<T> setTypeface(ExcelTypefaceEnum typeface) {
        this.typeface = typeface;
        return this;
    }

    public TypefaceDefinitionBuilder<T> setTypefaceName(String typefaceName) {
        this.typefaceName = typefaceName;
        return this;
    }

    public TypefaceDefinitionBuilder<T> setSize(Integer size) {
        this.size = size;
        return this;
    }

    public TypefaceDefinitionBuilder<T> setItalic(Boolean italic) {
        this.italic = italic;
        return this;
    }

    public TypefaceDefinitionBuilder<T> setStrikeout(Boolean strikeout) {
        this.strikeout = strikeout;
        return this;
    }

    public TypefaceDefinitionBuilder<T> setColor(Integer color) {
        this.color = color;
        return this;
    }

    public TypefaceDefinitionBuilder<T> setTypeOffset(ExcelTypeOffsetEnum typeOffset) {
        this.typeOffset = typeOffset;
        return this;
    }

    public TypefaceDefinitionBuilder<T> setUnderline(ExcelUnderlineEnum underline) {
        this.underline = underline;
        return this;
    }

    public TypefaceDefinitionBuilder<T> setBold(Boolean bold) {
        this.bold = bold;
        return this;
    }

    @Override
    public ExcelTypefaceDefinition build() {
        return new ExcelTypefaceDefinition()
                .setTypeface(typeface)
                .setTypefaceName(typefaceName)
                .setSize(size)
                .setItalic(italic)
                .setStrikeout(strikeout)
                .setColor(color)
                .setTypeOffset(typeOffset)
                .setUnderline(underline)
                .setBold(bold);
    }
}
