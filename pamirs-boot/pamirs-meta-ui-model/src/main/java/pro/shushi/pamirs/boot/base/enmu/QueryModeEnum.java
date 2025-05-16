package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 查询方式枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.QueryMode", displayName = "查询方式")
public enum QueryModeEnum implements IEnum<String> {

    DOMAIN("domain", "条件入参", "条件入参"),
    ENTITY("entity", "单行或多行实体入参", "单行或多行实体入参");

    private final String displayName;

    private final String value;

    private final String help;

    QueryModeEnum(String value, String displayName, String help) {
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
