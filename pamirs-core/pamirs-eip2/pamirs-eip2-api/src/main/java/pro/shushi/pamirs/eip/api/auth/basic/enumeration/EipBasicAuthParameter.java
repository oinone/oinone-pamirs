package pro.shushi.pamirs.eip.api.auth.basic.enumeration;

import org.apache.camel.Exchange;
import pro.shushi.pamirs.eip.api.auth.api.EipAuthParameterEnum;
import pro.shushi.pamirs.eip.api.constant.EipContextConstant;

/**
 * @author Adamancy Zhang on 2021-02-05 17:20
 */
public enum EipBasicAuthParameter implements EipAuthParameterEnum {

    BASE_PATH(Exchange.HTTP_BASE_URI, EipContextConstant.INTERFACE_BASE_PATH_KEY),
    SCHEMA(Exchange.HTTP_SCHEME, "schema"),
    HOST(Exchange.HTTP_HOST, "host"),
    PORT(Exchange.HTTP_PORT, "port"),
    PATH(Exchange.HTTP_PATH, "path"),
    URI(Exchange.HTTP_URI, "uri"),
    USERNAME("username", "username"),
    PASSWORD("password", "password"),
    USER_AGENT("User-Agent", "userAgent"),
    CONTENT_TYPE("Content-Type", "contentType"),
    CONTENT_LANGUAGE("Content-Language", "contentLanguage"),
    CHARSET_NAME(Exchange.CHARSET_NAME, "charsetName"),
    HTTP_METHOD(Exchange.HTTP_METHOD, "httpMethod"),
    ;

    private final String origin;

    private final String target;

    EipBasicAuthParameter(String origin, String target) {
        this.origin = origin;
        this.target = target;
    }

    @Override
    public String getOrigin() {
        return origin;
    }

    @Override
    public String getTarget() {
        return target;
    }
}
