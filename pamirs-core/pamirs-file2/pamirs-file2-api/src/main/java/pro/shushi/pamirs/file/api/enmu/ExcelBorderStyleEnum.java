package pro.shushi.pamirs.file.api.enmu;

import org.apache.poi.ss.usermodel.BorderStyle;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExcelBorderStyleEnum.dictionary, displayName = "Excel边框样式")
public enum ExcelBorderStyleEnum implements IEnum<String> {

    NONE("NONE", "无边框", "无边框", BorderStyle.NONE),
    THIN("THIN", "细边框", "细边框", BorderStyle.THIN),
    MEDIUM("MEDIUM", "常规边框", "常规边框", BorderStyle.MEDIUM),
    THICK("THICK", "粗边框", "粗边框", BorderStyle.THICK),
    DASHED("DASHED", "虚线边框", "虚线边框", BorderStyle.DASHED),
    DOTTED("DOTTED", "点线边框", "点线边框", BorderStyle.DOTTED),
    DOUBLE("DOUBLE", "双线边框", "双线边框", BorderStyle.DOUBLE),
    HAIR("HAIR", "发线边框", "发线边框", BorderStyle.HAIR),
    MEDIUM_DASHED("MEDIUM_DASHED", "中虚线边框", "中虚线边框", BorderStyle.MEDIUM_DASHED),
    DASH_DOT("DASH_DOT", "点划线边框", "点划线边框", BorderStyle.DASH_DOT),
    MEDIUM_DASH_DOT("MEDIUM_DASH_DOT", "中划线点边框", "中划线点边框", BorderStyle.MEDIUM_DASH_DOT),
    DASH_DOT_DOT("DASH_DOT_DOT", "点-点-点边框", "点-点-点边框", BorderStyle.DASH_DOT_DOT),
    MEDIUM_DASH_DOT_DOT("MEDIUM_DASH_DOT_DOT", "中短划线-点-点边框", "中短划线-点-点边框", BorderStyle.MEDIUM_DASH_DOT_DOT),
    SLANTED_DASH_DOT("SLANTED_DASH_DOT", "斜线点边框", "斜线点边框", BorderStyle.SLANTED_DASH_DOT);

    public static final String dictionary = "file.ExcelBorderStyleEnum";

    private String value;
    private String displayName;
    private String help;
    private BorderStyle poi;

    ExcelBorderStyleEnum(String value, String displayName, String help, BorderStyle poi) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
        this.poi = poi;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHelp() {
        return help;
    }

    public BorderStyle getPoi() {
        return poi;
    }
}
