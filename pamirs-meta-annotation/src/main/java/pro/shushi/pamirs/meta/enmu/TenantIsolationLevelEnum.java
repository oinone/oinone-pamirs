package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 租户隔离级别
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.TenantIsolationLevel", displayName = "租户隔离级别")
public enum TenantIsolationLevelEnum implements IEnum<String> {

    SINGLE("SINGLE", "单租户", "单租户"),
    DATA("DATA", "数据隔离", "数据隔离"),
    LOGICAL("LOGICAL", "逻辑隔离", "逻辑隔离"),
    PHYSICAL("PHYSICAL", "物理隔离", "物理隔离"),
    ;

    private final String value;

    private final String displayName;

    private final String help;

    TenantIsolationLevelEnum(String value, String displayName, String help) {
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
