package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 组件类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.WidgetType", displayName = "组件类型")
public enum WidgetTypeEnum implements IEnum<String> {

    ATOMIC_WIDGET("atomicWidget", "原子组件", "原子组件"),
    COMPLEX_WIDGET("complexWidget", "复合组件", "复合组件"),
    ;

    private final String help;
    private final String value;
    private final String displayName;

    WidgetTypeEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
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