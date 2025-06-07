package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BitEnum;

/**
 * 组件适用范围
 *
 * @author wangxian@shushi.pro
 * date 2023/12/14
 */
@Base
@Dict(dictionary = WidgetScopeEnum.DICTIONARY, displayName = "组件适用范围")
public enum WidgetScopeEnum implements BitEnum {

    UI_WIDGET(1, "组件设计", "组件设计"),
    UI_PAGE(2 << 0, "页面设计", "页面设计"),
    ;

    public static final String DICTIONARY = "base.WidgetScopeEnum";

    private final Long value;
    private final String displayName;
    private final String help;

    WidgetScopeEnum(long value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public Long value() {
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