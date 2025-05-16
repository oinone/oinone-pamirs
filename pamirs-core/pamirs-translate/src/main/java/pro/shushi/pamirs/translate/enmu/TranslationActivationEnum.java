package pro.shushi.pamirs.translate.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = TranslationActivationEnum.DICTIONARY, displayName = "是否激活", summary = "是否激活")
public enum TranslationActivationEnum implements IEnum<String> {
    ALL(null, "全部", "全部"),
    ACTIVATION(true, "激活", "激活"),
    INACTIVATED(false, "未激活", "未激活");

    public static final String DICTIONARY = "translation.TranslationActivationEnum";


    private final Boolean value;
    private final String displayName;
    private final String help;

    TranslationActivationEnum(Boolean value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    public Boolean getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHelp() {
        return help;
    }
}