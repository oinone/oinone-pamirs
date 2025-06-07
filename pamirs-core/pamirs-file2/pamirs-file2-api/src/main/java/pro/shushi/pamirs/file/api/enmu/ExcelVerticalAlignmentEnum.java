package pro.shushi.pamirs.file.api.enmu;

import org.apache.poi.ss.usermodel.VerticalAlignment;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExcelVerticalAlignmentEnum.dictionary, displayName = "Excel垂直对齐")
public enum ExcelVerticalAlignmentEnum implements IEnum<String> {

    TOP("TOP", "顶部对齐", "顶部对齐", VerticalAlignment.TOP),
    CENTER("CENTER", "居中对齐", "居中对齐", VerticalAlignment.CENTER),
    BOTTOM("BOTTOM", "底部对齐", "底部对齐", VerticalAlignment.BOTTOM),
    JUSTIFY("JUSTIFY", "上下对齐", "上下对齐", VerticalAlignment.JUSTIFY),
    DISTRIBUTED("DISTRIBUTED", "分散对齐", "分散对齐", VerticalAlignment.DISTRIBUTED);

    public static final String dictionary = "file.ExcelVerticalAlignmentEnum";

    private String value;
    private String displayName;
    private String help;
    private VerticalAlignment poi;

    ExcelVerticalAlignmentEnum(String value, String displayName, String help, VerticalAlignment poi) {
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

    public VerticalAlignment getPoi() {
        return poi;
    }
}
