package pro.shushi.pamirs.file.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExcelTypefaceEnum.dictionary, displayName = "Excel字体")
public enum ExcelTypefaceEnum implements IEnum<String> {

    SONG("SONG", "宋体", "宋体"),
    REGULAR_SCRIPT("REGULAR_SCRIPT", "楷体", "楷体"),
    BOLDFACE("BOLDFACE", "黑体", "黑体"),
    YAHEI("YAHEI", "Microsoft YaHei", "微软雅黑");

    public static final String dictionary = "file.ExcelTypefaceEnum";

    private String value;
    private String displayName;
    private String help;

    ExcelTypefaceEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
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
}
