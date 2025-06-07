package pro.shushi.pamirs.file.api.enmu;


import org.apache.poi.ss.usermodel.FillPatternType;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExcelFillPatternTypeEnum.DICTIONARY, displayName = "Excel填充类型")
public enum ExcelFillPatternTypeEnum implements IEnum<String> {

    NO_FILL("NO_FILL", "不填充", "不填充", FillPatternType.NO_FILL),
    SOLID_FOREGROUND("SOLID_FOREGROUND", "纯色填充", "纯色填充", FillPatternType.SOLID_FOREGROUND),
    FINE_DOTS("FINE_DOTS", "细点填充", "细点填充", FillPatternType.FINE_DOTS),
    ALT_BARS("ALT_BARS", "交替条纹填充", "交替条纹填充", FillPatternType.ALT_BARS),
    SPARSE_DOTS("SPARSE_DOTS", "稀疏点填充", "稀疏点填充", FillPatternType.SPARSE_DOTS),
    THICK_HORZ_BANDS("THICK_HORZ_BANDS", "粗横向条纹填充", "粗横向条纹填充", FillPatternType.THICK_HORZ_BANDS),
    THICK_VERT_BANDS("THICK_VERT_BANDS", "粗纵向条纹填充", "粗纵向条纹填充", FillPatternType.THICK_VERT_BANDS),
    THICK_BACKWARD_DIAG("THICK_BACKWARD_DIAG", "粗斜线向后填充", "粗斜线向后填充", FillPatternType.THICK_BACKWARD_DIAG),
    THICK_FORWARD_DIAG("THICK_FORWARD_DIAG", "粗斜线向前填充", "粗斜线向前填充", FillPatternType.THICK_FORWARD_DIAG),
    BIG_SPOTS("BIG_SPOTS", "大点填充", "大点填充", FillPatternType.BIG_SPOTS),
    BRICKS("BRICKS", "砖块填充", "砖块填充", FillPatternType.BRICKS),
    THIN_HORZ_BANDS("THIN_HORZ_BANDS", "细横向条纹填充", "细横向条纹填充", FillPatternType.THIN_HORZ_BANDS),
    THIN_VERT_BANDS("THIN_VERT_BANDS", "细纵向条纹填充", "细纵向条纹填充", FillPatternType.THIN_VERT_BANDS),
    THIN_BACKWARD_DIAG("THIN_BACKWARD_DIAG", "细斜线向后填充", "细斜线向后填充", FillPatternType.THIN_BACKWARD_DIAG),
    THIN_FORWARD_DIAG("THIN_FORWARD_DIAG", "细斜线向前填充", "细斜线向前填充", FillPatternType.THIN_FORWARD_DIAG);

    public static final String DICTIONARY = "file.ExcelFillPatternTypeEnum";

    private final String value;
    private final String displayName;
    private final String help;
    private final FillPatternType poi;

    ExcelFillPatternTypeEnum(String value, String displayName, String help, FillPatternType poi) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
        this.poi = poi;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String help() {
        return help;
    }

    public FillPatternType getPoi() {
        return poi;
    }
}
