package pro.shushi.pamirs.core.common.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = DataStatusEnum.dictionary, displayName = "数据状态")
public enum DataStatusEnum implements IEnum<String> {

    DRAFT("DRAFT", "草稿", "草稿"),
    NOT_ENABLED("NOT_ENABLED", "未启用", "未启用"),
    ENABLED("ENABLED", "已启用", "已启用"),
    DISABLED("DISABLED", "已禁用", "已禁用");

    public static final String dictionary = "partner.DataStatusEnum";

    private String value;
    private String displayName;
    private String help;

    DataStatusEnum(String value, String displayName, String help) {
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
