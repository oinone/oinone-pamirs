package pro.shushi.pamirs.eip.api.enmu.connector;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * ApiSchema
 *
 * @author yakir on 2023/03/30 14:13.
 */
@Base
@Dict(dictionary = ApiMethod.dictionary, displayName = "连接器API请求方法", summary = "连接器API请求方法")
public enum ApiMethod implements IEnum<String> {

    GET("GET", "GET", "GET"),
    POST("POST", "POST", "POST"),
    PUT("PUT", "PUT", "PUT"),
    DELETE("DELETE", "DELETE", "DELETE"),

    ;

    public static final String dictionary = "designer.ApiMethod";

    private final String value;
    private final String displayName;
    private final String help;

    ApiMethod(String value, String displayName, String help) {
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
