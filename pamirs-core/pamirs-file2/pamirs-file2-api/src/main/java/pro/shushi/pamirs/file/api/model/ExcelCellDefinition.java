package pro.shushi.pamirs.file.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.Optional;

@Base
@Model.model(ExcelCellDefinition.MODEL_MODEL)
@Model(displayName = "Excel单元格")
public class ExcelCellDefinition extends TransientModel {

    private static final long serialVersionUID = -3722958464925188297L;

    public static final String MODEL_MODEL = "file.ExcelCellDefinition";

    /**
     * <h>单元格属性定义</h>
     * <p>
     * 固定表头:<br>
     * - 仅在配置行中生效<br>
     * 固定格式:<br>
     * - 在任意单元格中生效
     * </p>
     */
    @Field(displayName = "属性")
    private String field;

    @Field(displayName = "值", summary = "单元格的值")
    private String value;

    @Field(displayName = "值类型")
    private ExcelValueTypeEnum type;

    @Field(displayName = "格式化", summary = "格式化方式请参考用户手册")
    private String format;

    @Field(displayName = "是否翻译", summary = "默认情况下，静态值、枚举和布尔字段会自动翻译，其他情况根据指定值进行处理")
    private Boolean translate;

    @Field(displayName = "是否是静态值", defaultValue = "false", summary = "静态值在数据解析时将使用配置值，不读取单元格的值")
    private Boolean isStatic;

    @Field(displayName = "是否是属性值", summary = "标记该单元格的内容为属性值")
    private Boolean isFieldValue;

    @Field(displayName = "自动列宽", defaultValue = "true", summary = "自动列宽")
    private Boolean autoSizeColumn;

    @Field(displayName = "单元格样式")
    private ExcelStyleDefinition style;

    @JSONField(serialize = false)
    private transient CellStyle styleCache;

    public CellStyle getStyleCache() {
        return this.styleCache;
    }

    public void setStyleCache(CellStyle styleCache) {
        this.styleCache = styleCache;
    }

    public CellStyle getOrCreateCellStyle(Workbook workbook) {
        return this.getOrCreateCellStyle(workbook, false);
    }

    public CellStyle getOrCreateCellStyle(Workbook workbook, boolean isNotNull) {
        if (styleCache != null) {
            return styleCache;
        }
        ExcelStyleDefinition style = this.getStyle();
        if (style == null) {
            if (isNotNull) {
                styleCache = workbook.createCellStyle();
            }
        } else {
            styleCache = style.getOrCreateCellStyle(workbook);
        }
        return styleCache;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ExcelCellDefinition clone() {
        return new ExcelCellDefinition()
                .setField(getField())
                .setValue(getValue())
                .setType(getType())
                .setFormat(getFormat())
                .setIsStatic(getIsStatic())
                .setIsFieldValue(getIsFieldValue())
                .setAutoSizeColumn(getAutoSizeColumn())
                .setStyle(Optional.ofNullable(getStyle()).map(ExcelStyleDefinition::clone).orElse(null));
    }
}
