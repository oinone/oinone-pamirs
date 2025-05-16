package pro.shushi.pamirs.boot.base.ux.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 容器类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.ContainerType", displayName = "容器类型")
public enum ContainerTypeEnum implements IEnum<String> {

    CONTAINER("container", "容器", "容器"),
    BLOCK("block", "区块", "区块"),
    GROUP("group", "分组", "分组"),
    TABS("tabs", "选项卡", "选项卡"),
    TAB("tab", "选项卡页", "选项卡页");

    private final String displayName;

    private final String value;

    private final String help;

    ContainerTypeEnum(String value, String displayName, String help) {
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
