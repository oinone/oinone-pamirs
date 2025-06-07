package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 模板布局类型
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.TemplateLayoutType", displayName = "模板布局类型")
public enum TemplateLayoutTypeEnum implements IEnum<String> {

    MASK("mask", "母版布局", "母版布局"),
    VIEW("view", "视图布局", "视图布局"),
    ;

    private final String value;

    private final String displayName;

    private final String help;

    TemplateLayoutTypeEnum(String value, String displayName, String help) {
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