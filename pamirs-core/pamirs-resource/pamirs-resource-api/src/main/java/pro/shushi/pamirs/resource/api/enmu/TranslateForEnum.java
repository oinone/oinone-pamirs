package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * TranslateForEnum
 *
 * @author yakir on 2020/11/04 23:07.
 */
@Dict(dictionary = TranslateForEnum.DICTIONARY, displayName = "翻译类型", summary = "翻译类型")
public enum TranslateForEnum implements IEnum<String> {

    FRONT_END("FRONT_END", "前端翻译", "前端翻译"),
    BACK_END("BACK_END", "后端元数据翻译", "后端元数据翻译"),
    BACK_END_DATA("BACK_END_DATA", "后端数据翻译", "后端数据翻译"),
    ;

    public static final String DICTIONARY = "resource.TranslateForEnum";

    private final String value;
    private final String displayName;
    private final String help;

    TranslateForEnum(String value, String displayName, String help) {
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