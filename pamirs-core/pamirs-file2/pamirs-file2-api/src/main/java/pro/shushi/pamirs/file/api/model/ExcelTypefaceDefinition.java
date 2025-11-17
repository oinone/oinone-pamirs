package pro.shushi.pamirs.file.api.model;

import pro.shushi.pamirs.file.api.enmu.ExcelTypeOffsetEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelTypefaceEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelUnderlineEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

@Base
@Model.model(ExcelTypefaceDefinition.MODEL_MODEL)
@Model(displayName = "Excel字体样式")
public class ExcelTypefaceDefinition extends TransientModel {

    private static final long serialVersionUID = -3546633362686224956L;

    public static final String MODEL_MODEL = "file.ExcelTypefaceDefinition";

    @Field.Enum
    @Field(displayName = "字体", defaultValue = "SONG")
    private ExcelTypefaceEnum typeface;

    @Field.String
    @Field(displayName = "自定义字体")
    private String typefaceName;

    @Field.Integer
    @Field(displayName = "大小", defaultValue = "11")
    private Integer size;

    @Field.Boolean
    @Field(displayName = "斜体", defaultValue = "false")
    private Boolean italic;

    @Field.Boolean
    @Field(displayName = "删除线", defaultValue = "false")
    private Boolean strikeout;

    @Field.Integer
    @Field(displayName = "颜色", defaultValue = "0xfff")
    private Integer color;

    @Field.Enum
    @Field(displayName = "偏移类型", defaultValue = "NORMAL")
    private ExcelTypeOffsetEnum typeOffset;

    @Field.Enum
    @Field(displayName = "下划线类型", defaultValue = "NONE")
    private ExcelUnderlineEnum underline;

    @Field.Boolean
    @Field(displayName = "是否加粗", defaultValue = "false")
    private Boolean bold;

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ExcelTypefaceDefinition clone() {
        return new ExcelTypefaceDefinition()
                .setTypeface(getTypeface())
                .setTypefaceName(getTypefaceName())
                .setSize(getSize())
                .setItalic(getItalic())
                .setStrikeout(getStrikeout())
                .setColor(getColor())
                .setTypeOffset(getTypeOffset())
                .setUnderline(getUnderline())
                .setBold(getBold());
    }
}
