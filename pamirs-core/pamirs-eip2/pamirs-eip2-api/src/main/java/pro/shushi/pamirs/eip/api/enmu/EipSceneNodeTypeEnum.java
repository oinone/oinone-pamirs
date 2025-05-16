package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author drome
 * @date 2021/7/302:12 下午
 */
@Base
@Dict(dictionary = EipSceneNodeTypeEnum.dictionary, displayName = "场景节点类型")
public enum EipSceneNodeTypeEnum implements IEnum<String> {

    // TODO: 2021/8/6 function 现在不建议这么做.建议包装成eip接口(function协议而不是http协议)
    @Deprecated
    FUNCTION("FUNCTION", "函数", "函数"),

    INTEGRATION_INTERFACE("INTEGRATION_INTERFACE", "集成接口", "集成接口"),
    ROUTE_DEFINITION("ROUTE_DEFINITION", "组合接口", "组合接口"),
    ;

    public static final String dictionary = "pamirs.eip.EipSceneNodeTypeEnum";

    private String value;

    private String displayName;

    private String help;

    EipSceneNodeTypeEnum(String value, String displayName, String help) {
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

