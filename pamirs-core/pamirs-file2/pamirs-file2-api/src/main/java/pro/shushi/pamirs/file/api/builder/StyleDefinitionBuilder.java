package pro.shushi.pamirs.file.api.builder;

import pro.shushi.pamirs.core.common.builder.BuilderHelper;
import pro.shushi.pamirs.core.common.builder.IBuilder;
import pro.shushi.pamirs.file.api.enmu.ExcelBorderStyleEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelFillPatternTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelHorizontalAlignmentEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelVerticalAlignmentEnum;
import pro.shushi.pamirs.file.api.model.ExcelStyleDefinition;
import pro.shushi.pamirs.file.api.model.ExcelTypefaceDefinition;

public class StyleDefinitionBuilder<T> extends AbstractBaseBuilder<T> implements IBuilder<ExcelStyleDefinition> {

    private ExcelHorizontalAlignmentEnum horizontalAlignment = ExcelHorizontalAlignmentEnum.GENERAL;

    private ExcelVerticalAlignmentEnum verticalAlignment;

    private ExcelBorderStyleEnum fillBorderStyle;

    private ExcelBorderStyleEnum topBorderStyle;

    private ExcelBorderStyleEnum rightBorderStyle;

    private ExcelBorderStyleEnum bottomBorderStyle;

    private ExcelBorderStyleEnum leftBorderStyle;

    private Integer fillBorderColor;

    private Integer topBorderColor;

    private Integer rightBorderColor;

    private Integer bottomBorderColor;

    private Integer leftBorderColor;

    private ExcelFillPatternTypeEnum fillPatternType;

    private Integer backgroundColor;

    private Integer foregroundColor;

    private Boolean wrapText = false;

    private Boolean shrinkToFit = false;

    private Integer width;

    private Integer height;

    private IBuilder<ExcelTypefaceDefinition> typefaceDefinitionBuilder;

    public StyleDefinitionBuilder<T> setHorizontalAlignment(ExcelHorizontalAlignmentEnum horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    public StyleDefinitionBuilder<T> setVerticalAlignment(ExcelVerticalAlignmentEnum verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        return this;
    }

    public StyleDefinitionBuilder<T> setFillBorderStyle(ExcelBorderStyleEnum fillBorderStyle) {
        this.fillBorderStyle = fillBorderStyle;
        return this;
    }

    public StyleDefinitionBuilder<T> setTopBorderStyle(ExcelBorderStyleEnum topBorderStyle) {
        this.topBorderStyle = topBorderStyle;
        return this;
    }

    public StyleDefinitionBuilder<T> setRightBorderStyle(ExcelBorderStyleEnum rightBorderStyle) {
        this.rightBorderStyle = rightBorderStyle;
        return this;
    }

    public StyleDefinitionBuilder<T> setBottomBorderStyle(ExcelBorderStyleEnum bottomBorderStyle) {
        this.bottomBorderStyle = bottomBorderStyle;
        return this;
    }

    public StyleDefinitionBuilder<T> setLeftBorderStyle(ExcelBorderStyleEnum leftBorderStyle) {
        this.leftBorderStyle = leftBorderStyle;
        return this;
    }

    public StyleDefinitionBuilder<T> setFillBorderColor(Integer fillBorderColor) {
        this.fillBorderColor = fillBorderColor;
        return this;
    }

    public StyleDefinitionBuilder<T> setTopBorderColor(Integer topBorderColor) {
        this.topBorderColor = topBorderColor;
        return this;
    }

    public StyleDefinitionBuilder<T> setRightBorderColor(Integer rightBorderColor) {
        this.rightBorderColor = rightBorderColor;
        return this;
    }

    public StyleDefinitionBuilder<T> setBottomBorderColor(Integer bottomBorderColor) {
        this.bottomBorderColor = bottomBorderColor;
        return this;
    }

    public StyleDefinitionBuilder<T> setLeftBorderColor(Integer leftBorderColor) {
        this.leftBorderColor = leftBorderColor;
        return this;
    }

    public StyleDefinitionBuilder<T> setFillPatternType(ExcelFillPatternTypeEnum fillPatternType) {
        this.fillPatternType = fillPatternType;
        return this;
    }

    public StyleDefinitionBuilder<T> setBackgroundColor(Integer backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public StyleDefinitionBuilder<T> setForegroundColor(Integer foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }

    public StyleDefinitionBuilder<T> setWrapText(Boolean wrapText) {
        this.wrapText = wrapText;
        return this;
    }

    public StyleDefinitionBuilder<T> setShrinkToFit(Boolean shrinkToFit) {
        this.shrinkToFit = shrinkToFit;
        return this;
    }

    public StyleDefinitionBuilder<T> setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public StyleDefinitionBuilder<T> setHeight(Integer height) {
        this.height = height;
        return this;
    }

    public TypefaceDefinitionBuilder<StyleDefinitionBuilder<T>> createTypeface() {
        TypefaceDefinitionBuilder<StyleDefinitionBuilder<T>> builder = new TypefaceDefinitionBuilder<>(this);
        this.typefaceDefinitionBuilder = builder;
        return builder;
    }

    public StyleDefinitionBuilder(T builder) {
        super(builder);
    }

    @Override
    public ExcelStyleDefinition build() {
        return new ExcelStyleDefinition()
                .setHorizontalAlignment(horizontalAlignment)
                .setVerticalAlignment(verticalAlignment)
                .setFillBorderStyle(fillBorderStyle)
                .setTopBorderStyle(topBorderStyle)
                .setRightBorderStyle(rightBorderStyle)
                .setBottomBorderStyle(bottomBorderStyle)
                .setLeftBorderStyle(leftBorderStyle)
                .setFillBorderColor(fillBorderColor)
                .setTopBorderColor(topBorderColor)
                .setRightBorderColor(rightBorderColor)
                .setBottomBorderColor(bottomBorderColor)
                .setLeftBorderColor(leftBorderColor)
                .setFillPatternType(fillPatternType)
                .setBackgroundColor(backgroundColor)
                .setForegroundColor(foregroundColor)
                .setWrapText(wrapText)
                .setShrinkToFit(shrinkToFit)
                .setTypefaceDefinition(BuilderHelper.build(typefaceDefinitionBuilder))
                .setWidth(width)
                .setHeight(height);
    }
}
