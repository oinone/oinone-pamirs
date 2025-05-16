package pro.shushi.pamirs.file.api.enmu;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExcelHorizontalAlignmentEnum.dictionary, displayName = "Excel水平对齐")
public enum ExcelHorizontalAlignmentEnum implements IEnum<String> {

    GENERAL("GENERAL", "默认", "文本居左；数字、日期和时间居右；布尔居中；", HorizontalAlignment.GENERAL),
    LEFT("LEFT", "左对齐", "左对齐", HorizontalAlignment.LEFT),
    CENTER("CENTER", "居中对齐", "居中对齐", HorizontalAlignment.CENTER),
    RIGHT("RIGHT", "右对齐", "右对齐", HorizontalAlignment.RIGHT),
    FILL("FILL", "填充对齐", "填充对齐", HorizontalAlignment.FILL),
    JUSTIFY("JUSTIFY", "左右对齐", "左右对齐", HorizontalAlignment.JUSTIFY),
    CENTER_SELECTION("CENTER_SELECTION", "居中选择对齐", "居中选择对齐", HorizontalAlignment.CENTER_SELECTION),
    DISTRIBUTED("DISTRIBUTED", "分散对齐", "分散对齐", HorizontalAlignment.DISTRIBUTED);

    public static final String dictionary = "file.ExcelHorizontalAlignmentEnum";

    private String value;
    private String displayName;
    private String help;
    private HorizontalAlignment poi;

    ExcelHorizontalAlignmentEnum(String value, String displayName, String help, HorizontalAlignment poi) {
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

    public HorizontalAlignment getPoi() {
        return poi;
    }
}
