package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = TranslationApplicationScopeEnum.DICTIONARY, displayName = "翻译应用范围", summary = "翻译应用范围")
public enum TranslationApplicationScopeEnum implements IEnum<String> {

    MODULE("MODULE", "源术语所在应用", "源术语所在应用"),
    GLOBAL("GLOBAL", "全局", "全局");

    public static final String DICTIONARY = "resource.TranslationApplicationScopeEnum";
    private final String value;
    private final String displayName;
    private final String help;

    TranslationApplicationScopeEnum(String value, String displayName, String help) {
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
