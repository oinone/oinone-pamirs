package pro.shushi.pamirs.boot.base.ux.enmu.layout;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 布局方式
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.LayoutType", displayName = "布局方式枚举")
public enum LayoutTypeEnum implements IEnum<String> {

    GRID("grid", "栅格布局", "栅格布局"),
    FLEX("flex", "流式布局", "流式布局");

    private final String displayName;

    private final String value;

    private final String help;

    LayoutTypeEnum(String value, String displayName, String help) {
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
