package pro.shushi.pamirs.translate.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = TranslationMethodEnum.DICTIONARY, displayName = "翻译方式", summary = "翻译方式")
public enum TranslationMethodEnum implements IEnum<String> {
    FRONT_END("FRONT_END","前端翻译","前端翻译"),
    BACK_END("FILE_IMPORT_TRANSLATION","文件导入翻译","文件导入翻译"),
    BACK_END_DATA("PAGE_TOOL_TRANSLATION","页面工具翻译","页面工具翻译");

    public static final String DICTIONARY = "translation.TranslationMethodEnum";


    private final String value;
    private final String displayName;
    private final String help;

    TranslationMethodEnum(String value, String displayName, String help) {
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