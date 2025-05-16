package pro.shushi.pamirs.business.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * JoinStateEnum
 *
 * @author yakir on 2022/09/16 18:28.
 */
@Dict(dictionary = JoinStateEnum.dict, displayName = "团队管理加入状态")
public enum JoinStateEnum implements IEnum<String> {

    WAIT_AUDIT("WAIT_AUDIT", "等待加入申请审核中...", "等待加入申请审核中..."),
    INIT_ENT("INIT_ENT", "正在初始化企业...", "正在初始化企业..."),

    ;

    public static final String dict = "business.JoinStateEnum";

    private final String value;
    private final String displayName;
    private final String help;

    JoinStateEnum(String value, String displayName, String help) {
        this.value       = value;
        this.displayName = displayName;
        this.help        = help;
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
