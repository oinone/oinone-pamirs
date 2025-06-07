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
@Dict(dictionary = ApiSchema.dictionary, displayName = "连接器API协议", summary = "连接器API协议")
public enum ApiSchema implements IEnum<String> {

    // restful
    HTTP("HTTP", "HTTP", "HTTP"),
    HTTPS("HTTPS", "HTTPS", "HTTPS"),

    ;

    public static final String dictionary = "designer.ApiSchema";

    private final String value;
    private final String displayName;
    private final String help;

    ApiSchema(String value, String displayName, String help) {
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

    public boolean isHttp() {
        return this.equals(HTTP) || this.equals(HTTPS);

    }
}
