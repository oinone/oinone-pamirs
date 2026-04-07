package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author yeshenyue on 2026/4/3 17:01.
 */
@Base
@Dict(dictionary = EipRetryStatusEnum.dictionary, displayName = "日志状态")
public enum EipRetryStatusEnum implements IEnum<String> {

    RETRYING("RETRYING", "进行中", "进行中"),
    SUCCESS("SUCCESS", "成功", "成功"),
    FAILURE("FAILURE", "失败", "失败");

    public static final String dictionary = "eip.EipRetryStatusEnum";

    private final String value;

    private final String displayName;

    private final String help;

    EipRetryStatusEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
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
}
