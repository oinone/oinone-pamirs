package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "directionenum", displayName = "")
public enum DirectionEnum implements IEnum<String> {

    LTR("LTR", "从左向右阅读", "从左向右阅读"),
    RTL("RTL", "从右向左阅读", "从右向左阅读");

    private String help;

    private String value;

    private String displayName;

    DirectionEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


}
