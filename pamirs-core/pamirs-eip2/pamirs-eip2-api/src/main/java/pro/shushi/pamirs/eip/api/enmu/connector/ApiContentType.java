package pro.shushi.pamirs.eip.api.enmu.connector;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * ApiContentType
 *
 * @author yakir on 2023/03/29 16:07.
 */
@Base
@Dict(dictionary = ApiContentType.dictionary, displayName = "连接器类型", summary = "连接器类型")
public enum ApiContentType implements IEnum<String> {

    // restful
    APPLICATION_JSON("application/json", "application/json", "application/json"),
    X_WWW_FORM_URLENCODED("x-www-form-urlencoded", "x-www-form-urlencoded", "x-www-form-urlencoded"),
    MULTIPART_FORM_DATA("multipart/form-data", "multipart/form-data", "multipart/form-data"),
    APPLICATION_JSON_FHIR("application/json+fhir", "application/json+fhir", "HL7-FHIR协议"),

    // webservice
    SOAP_XML("application/soap+xml", "application/soap+xml", "application/soap+xml"),  // soap 1.2 版本
    TEXT_XML("text/xml", "text/xml", "text/xml"), // soap 1.1 版本

    ;

    public static final String dictionary = "designer.ApiContentType";

    private final String value;
    private final String displayName;
    private final String help;

    ApiContentType(String value, String displayName, String help) {
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
