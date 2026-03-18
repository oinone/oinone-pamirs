package pro.shushi.pamirs.file.api.enmu;

import com.alibaba.fastjson.JSON;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.file.api.FileModule;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;

import java.util.LinkedHashMap;

@Base
@Dict(dictionary = ExcelValueTypeEnum.dictionary, displayName = "Excel值类型")
public enum ExcelValueTypeEnum implements IEnum<String> {

    STRING("STRING", "文本", "文本", null),
    INTEGER("INTEGER", "整数", "整数", "0"),
    NUMBER("NUMBER", "数字", "数字", "0.00"),
    DATETIME("DATETIME", "日期+时间", "日期+时间", DateFormatEnum.DATETIME.value()),
    FORMULA("FORMULA", "公式", "公式", null),
    BOOLEAN("BOOLEAN", "布尔", "布尔",
            JSON.toJSONString(MapHelper.newInstance(new LinkedHashMap<>(2))
                    .put("true", "是")
                    .put("false", "否")
                    .build()
            )),
    CALENDAR("CALENDAR", "日历", "日历", null),
    COMMENT("COMMENT", "备注", "备注", null),
    HYPER_LINK("HYPER_LINK", "超链接", "超链接", null),
    RICH_TEXT_STRING("RICH_TEXT_STRING", "富文本", "富文本", null),
    ENUMERATION("ENUMERATION", "枚举", "枚举", null),
    BIT("BIT", "二进制枚举", "二进制枚举", null),
    OBJECT("OBJECT", "对象", "对象", null),
    ;

    public static final String dictionary = "file.ExcelValueTypeEnum";

    private final String value;

    private final String displayName;

    private final String help;

    private final String defaultFormat;

    ExcelValueTypeEnum(String value, String displayName, String help, String defaultFormat) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
        this.defaultFormat = defaultFormat;
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public String displayName() {
        return this.displayName;
    }

    @Override
    public String help() {
        return this.help;
    }

    public String defaultFormat() {
        return I18nUtils.translateDataDictionaryItem(FileModule.MODULE_MODULE, ExcelValueTypeEnum.dictionary, name(), "defaultFormat", this.defaultFormat);
    }
}
