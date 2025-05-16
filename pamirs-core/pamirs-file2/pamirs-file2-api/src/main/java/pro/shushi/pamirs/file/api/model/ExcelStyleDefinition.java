package pro.shushi.pamirs.file.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import pro.shushi.pamirs.file.api.enmu.ExcelBorderStyleEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelFillPatternTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelHorizontalAlignmentEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelVerticalAlignmentEnum;
import pro.shushi.pamirs.file.api.util.ExcelDefinitionConverter;
import pro.shushi.pamirs.file.api.util.WorkbookHelper;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.Optional;

@Base
@Model.model(ExcelStyleDefinition.MODEL_MODEL)
@Model(displayName = "Excel单元格样式")
public class ExcelStyleDefinition extends TransientModel {

    public static final String MODEL_MODEL = "file.ExcelCellStyleDefinition";

    @Field(displayName = "水平对齐", defaultValue = "GENERAL")
    private ExcelHorizontalAlignmentEnum horizontalAlignment;

    @Field(displayName = "垂直对齐")
    private ExcelVerticalAlignmentEnum verticalAlignment;

    @Field(displayName = "全边框样式")
    private ExcelBorderStyleEnum fillBorderStyle;

    @Field(displayName = "上边框样式")
    private ExcelBorderStyleEnum topBorderStyle;

    @Field(displayName = "右边框样式")
    private ExcelBorderStyleEnum rightBorderStyle;

    @Field(displayName = "下边框样式")
    private ExcelBorderStyleEnum bottomBorderStyle;

    @Field(displayName = "左边框样式")
    private ExcelBorderStyleEnum leftBorderStyle;

    @Field(displayName = "全边框色")
    private Integer fillBorderColor;

    @Field(displayName = "上边框色")
    private Integer topBorderColor;

    @Field(displayName = "右边框色")
    private Integer rightBorderColor;

    @Field(displayName = "下边框色")
    private Integer bottomBorderColor;

    @Field(displayName = "左边框色")
    private Integer leftBorderColor;

    @Field(displayName = "背景色/前景色填充类型")
    private ExcelFillPatternTypeEnum fillPatternType;

    @Field(displayName = "背景色")
    private Integer backgroundColor;

    @Field(displayName = "前景色")
    private Integer foregroundColor;

    @Field(displayName = "是否自动换行", defaultValue = "false")
    private Boolean wrapText;

    @Field(displayName = "是否自动收缩", defaultValue = "false")
    private Boolean shrinkToFit;

    @Field(displayName = "宽", summary = "仅首行有效")
    private Integer width;

    @Field(displayName = "高", summary = "仅首列有效")
    private Integer height;

    @Field(displayName = "字体")
    private ExcelTypefaceDefinition typefaceDefinition;

    @JSONField(serialize = false)
    private CellStyle styleCache;

    public CellStyle getStyleCache() {
        return this.styleCache;
    }

    public void setStyleCache(CellStyle styleCache) {
        this.styleCache = styleCache;
    }

    public CellStyle getOrCreateCellStyle(Workbook workbook) {
        if (styleCache != null && WorkbookHelper.verifyBelongToStyleSource(workbook, styleCache)) {
            return styleCache;
        }
        styleCache = ExcelDefinitionConverter.convertCellStyle(workbook, this);
        return styleCache;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ExcelStyleDefinition clone() {
        return new ExcelStyleDefinition()
                .setHorizontalAlignment(getHorizontalAlignment())
                .setVerticalAlignment(getVerticalAlignment())
                .setFillBorderStyle(getFillBorderStyle())
                .setTopBorderStyle(getTopBorderStyle())
                .setRightBorderStyle(getRightBorderStyle())
                .setBottomBorderStyle(getBottomBorderStyle())
                .setLeftBorderStyle(getLeftBorderStyle())
                .setFillBorderColor(getFillBorderColor())
                .setTopBorderColor(getTopBorderColor())
                .setRightBorderColor(getRightBorderColor())
                .setBottomBorderColor(getBottomBorderColor())
                .setLeftBorderColor(getLeftBorderColor())
                .setFillPatternType(getFillPatternType())
                .setBackgroundColor(getBackgroundColor())
                .setForegroundColor(getForegroundColor())
                .setWrapText(getWrapText())
                .setShrinkToFit(getShrinkToFit())
                .setWidth(getWidth())
                .setHeight(getHeight())
                .setTypefaceDefinition(Optional.ofNullable(getTypefaceDefinition())
                        .map(ExcelTypefaceDefinition::clone)
                        .orElse(null));
    }
}
