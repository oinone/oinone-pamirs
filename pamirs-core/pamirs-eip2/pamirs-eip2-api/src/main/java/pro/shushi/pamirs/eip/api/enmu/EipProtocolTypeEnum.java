package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = "pamirs.eip.EipProtocolTypeEnum", displayName = "请求响应协议")
public enum EipProtocolTypeEnum implements IEnum<String> {

    HTTP("HTTP", "http", "http"),
    SOAP("SOAP", "soap", "soap"),
    SQL("SQL", "sql", "sql"),
    ;
    private String value;

    private String displayName;

    private String help;

    EipProtocolTypeEnum(String value, String displayName, String help) {
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
