package pro.shushi.pamirs.sso.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = SsoAuthTypeEnum.dictionary, displayName = "SSO认证类型")
public enum SsoAuthTypeEnum implements IEnum<String> {

    OAUTH2("OAUTH2", "OAuth2", "OAUTH2"),
    CAS("CAS", "CAS", "CAS"),
    SAML("SAML", "SAML", "SAML"),
    AD("AD", "AD", "AD"),
    KERBEROS("KERBEROS", "Kerberos", "Kerberos");

    public static final String dictionary = "sso.SsoAuthTypeEnum";

    private String value;
    private String displayName;
    private String help;

    SsoAuthTypeEnum(String value, String displayName, String help) {
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
