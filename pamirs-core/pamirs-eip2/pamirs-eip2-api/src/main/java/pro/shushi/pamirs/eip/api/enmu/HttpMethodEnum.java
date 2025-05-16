package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = HttpMethodEnum.dictionary, displayName = "http请求方法", summary = "http请求方法")
public enum HttpMethodEnum implements IEnum<String> {

    POST("POST", "POST", "POST"),
    GET("GET", "GET", "GET"),
    ;

    public static final String dictionary = "pamirs.eip.HttpMethodEnum";

    private String value;

    private String displayName;

    private String help;

    HttpMethodEnum(String value, String displayName, String help) {
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