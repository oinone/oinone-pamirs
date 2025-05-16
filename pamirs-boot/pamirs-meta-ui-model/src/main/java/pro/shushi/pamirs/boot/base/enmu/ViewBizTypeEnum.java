package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 视图业务类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.ViewBizType", displayName = "视图业务类型")
public enum ViewBizTypeEnum implements IEnum<String> {

    OPERATIONS_MANAGEMENT("OPERATIONS_MANAGEMENT", "运营管理", "运营管理"),
    PORTAL("PORTAL", "官网门户", "官网门户"),
    MALL("MALL", "商城", "商城"),
    COMMON("COMMON", "公共", "公共"),
    ;

    private final String value;

    private final String displayName;

    private final String help;

    ViewBizTypeEnum(String value, String displayName, String help) {
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