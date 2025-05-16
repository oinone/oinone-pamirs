package pro.shushi.pamirs.eip.designer.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * ApiParamOrigin
 *
 * @author yakir on 2023/03/30 14:13.
 */
@Base
@Dict(dictionary = ApiParamOrigin.dictionary, displayName = "响应参数来源", summary = "响应参数来源")
public enum ApiParamOrigin implements IEnum<String> {

    Header("Header", "Header", "Header"),
    Body("Body", "Body", "Body"),

    ;

    public static final String dictionary = "designer.ApiParamOrigin";

    private final String value;
    private final String displayName;
    private final String help;

    ApiParamOrigin(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String help() {
        return help;
    }
}
