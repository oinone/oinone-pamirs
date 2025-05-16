package pro.shushi.pamirs.file.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExcelTaskStateEnum.dictionary, displayName = "Excel任务状态")
public enum ExcelTaskStateEnum implements IEnum<String> {

    PROCESSING("PROCESSING", "处理中", "处理中"),
    SUCCESS("SUCCESS", "成功", "成功"),
    FAILURE("FAILURE", "失败", "失败");

    public static final String dictionary = "file.ExcelTaskStateEnum";

    private String value;

    private String displayName;

    private String help;

    ExcelTaskStateEnum(String value, String displayName, String help) {
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
