package pro.shushi.pamirs.translate.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = TranslationStatusEnum.DICTIONARY, displayName = "翻译状态", summary = "翻译状态")
public enum TranslationStatusEnum implements IEnum<String> {
    ALL("ALL", "全部", "全部"),
    TRANSLATED("TRANSLATED", "已翻译", "已翻译"),
    NOT_TRANSLATED("NOT_TRANSLATED", "未翻译", "未翻译");

    public static final String DICTIONARY = "translation.TranslationStatusEnum";


    private final String value;
    private final String displayName;
    private final String help;

    TranslationStatusEnum(String value, String displayName, String help) {
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