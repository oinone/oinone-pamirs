package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = AuditStatusEnum.DICTIONARY, displayName = "审核状态", summary = "审核状态")
public enum AuditStatusEnum implements IEnum<String> {

    INIT_AUDIT("INIT_AUDIT", "待提交", "待提交"),
    PENDING_AUDIT("PENDING_AUDIT", "待审核", "待审核"),
    AUDITING("AUDITING", "审核中", "审核中"),
    SUCCESS("SUCCESS", "审核通过", "审核通过"),
    FAILURE("FAILURE", "审核不通过", "审核不通过"),
    PENDING_RESUBMIT("PENDING_RESUBMIT", "待重新提交", "待重新提交"),
    REDIRECT("REDIRECT", "已转交", "已转交"),
    CANCEL("CANCEL", "已撤销", "已撤销"),
    ERROR("ERROR", "审批异常", "审批异常");

    public static final String DICTIONARY = "resource.AuditStatusEnum";

    private final String value;
    private final String displayName;
    private final String help;

    AuditStatusEnum(String value, String displayName, String help) {
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
