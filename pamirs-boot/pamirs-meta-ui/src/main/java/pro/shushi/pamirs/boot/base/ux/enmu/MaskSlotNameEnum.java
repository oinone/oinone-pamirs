package pro.shushi.pamirs.boot.base.ux.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 母版模板插槽枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.MaskSlotName", displayName = "母版模板插槽枚举")
public enum MaskSlotNameEnum implements IEnum<String> {

    HEADER("header", "页头", "页头"),
    APP_BAR("appBar", "应用栏", "应用栏"),
    OPERATION_BAR("operationBar", "操作栏", "操作栏"),
    PROFILE("profile", "用户栏", "用户栏"),
    CONTENT("content", "内容区", "内容区"),
    SIDEBAR("sidebar", "边栏", "边栏"),
    MAIN("main", "主内容分发区", "主内容分发区"),
    FOOTER("footer", "页尾", "页尾");

    private final String help;
    private final String value;
    private final String displayName;

    MaskSlotNameEnum(String value, String displayName, String help) {
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