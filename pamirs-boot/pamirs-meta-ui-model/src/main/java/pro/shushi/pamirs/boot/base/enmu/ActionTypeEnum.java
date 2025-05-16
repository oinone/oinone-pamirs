package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = "base.ActionType", displayName = "动作类型")
public enum ActionTypeEnum implements IEnum<String> {

    SERVER("SERVER", "服务器动作", "服务器动作"),
    URL("URL", "URL动作", "URL动作"),
    VIEW("VIEW", "窗口动作", "窗口动作"),
    CLIENT("CLIENT", "客户端动作", "客户端动作"),
    COMPOSITION("COMPOSITION", "组合动作", "组合动作"),

    ;

    private final String value;

    private final String displayName;

    private final String help;

    ActionTypeEnum(String value, String displayName, String help) {
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
