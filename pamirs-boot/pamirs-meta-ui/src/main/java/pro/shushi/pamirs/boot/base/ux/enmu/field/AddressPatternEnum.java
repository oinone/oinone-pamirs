package pro.shushi.pamirs.boot.base.ux.enmu.field;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 地址格式
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.AddressPattern", displayName = "地址格式")
public enum AddressPatternEnum implements IEnum<String> {

    PROVINCE4("province_l4", "省/市/区（县）/街道（镇）", "省/市/区（县）/街道（镇）"),
    PROVINCE3("province_l3", "省/市/区（县）", "省/市/区（县）"),
    PROVINCE2("province_l2", "省/市", "省/市"),
    PROVINCE1("province_l1", "省", "省"),
    CITY2("city_l2", "市/区（县）", "市/区（县）"),
    CITY1("city_l1", "市", "市")
    ;

    private final String displayName;

    private final String value;

    private final String help;

    AddressPatternEnum(String value, String displayName, String help) {
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
