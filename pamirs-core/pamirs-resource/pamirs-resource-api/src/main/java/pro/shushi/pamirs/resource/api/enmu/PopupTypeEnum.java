package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "poupTypeEnum", displayName = "")
public enum PopupTypeEnum implements IEnum<String> {
    POPUP("popup", "弹窗", "弹窗"),
    RIGHT_SUSPENSIO("rightSuspensio", "右侧悬浮", "右侧悬浮"),
    ;

    private String help;

    private String value;

    private String displayName;

    PopupTypeEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


}
