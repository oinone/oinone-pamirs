package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "blockslayoutenum", displayName = "")
public enum BlocksLayoutEnum implements IEnum<String> {
    RIGHT("right", "右侧", "右侧"),
    LEFT("left", "左侧", "左侧"),
    ;

    private String help;

    private String value;

    private String displayName;

    BlocksLayoutEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


}
