package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = TranslateDataSourcesEnum.DICTIONARY, displayName = "翻译来源", summary = "翻译来源")
public enum TranslateDataSourcesEnum implements IEnum<String> {

    PAGE_ADD("PAGE_ADD", "页面添加", "页面添加"),
    FILE_IMPORT_TRANSLATION("FILE_IMPORT_TRANSLATION", "文件导入", "文件导入"),
    BATCH_TRANSLATION("BATCH_TRANSLATION", "批量创建", "批量创建");

    public static final String DICTIONARY = "resource.TranslateDataSourcesEnum";

    private final String value;
    private final String displayName;
    private final String help;

    TranslateDataSourcesEnum(String value, String displayName, String help) {
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

