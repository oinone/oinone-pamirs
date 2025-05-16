package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 组件类型
 *
 * @author Adamancy Zhang at 11:06 on 2021-08-10
 */
@Base
@Dict(dictionary = ComponentTypeEnum.dictionary, displayName = "EIP组件类型", summary = "EIP组件类型")
public enum ComponentTypeEnum implements IEnum<String> {

    NORMAL("NORMAL", "普通组件", "普通组件"),// 2021年07月29日15:14:55 单指eip接口
    FILTER("FILTER", "条件组件", "条件组件"),
    FUNCTION("FUNCTION", "函数组件", "函数组件"),
    PARAM_PROCESSOR("PARAM_PROCESSOR", "参数转换组件", "参数转换组件"),
    ;

    public static final String dictionary = "pamirs.eip.ComponentTypeEnum";

    private final String value;

    private final String displayName;

    private final String help;

    ComponentTypeEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHelp() {
        return help;
    }
}
