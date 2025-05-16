package pro.shushi.pamirs.eip.api.enmu.connector;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * ConnAuthType
 *
 * @author yakir on 2023/03/29 15:25.
 */
@Base
@Dict(dictionary = ConnAuthType.dictionary, displayName = "认证方法", summary = "认证方式")
public enum ConnAuthType implements IEnum<String> {

    NO_AUTH("NO_AUTH", "无需认证", "无需认证"),
    BASIC("BASIC", "Basic Auth", "Basic Auth"),
    OAUTH2("OAUTH2", "OAuth 2.0", "OAuth 2.0"),
    CAK("CAK", "Common API Key", "Common API Key"),
//    TOKEN("TOKEN", "TOKEN", "TOKEN"),
    CUSTOM("CUSTOM", "自定义认证", "自定义认证"),
    ;

    public static final String dictionary = "designer.ConnAuthType";

    private final String value;
    private final String displayName;
    private final String help;

    ConnAuthType(String value, String displayName, String help) {
        this.value       = value;
        this.displayName = displayName;
        this.help        = help;
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
