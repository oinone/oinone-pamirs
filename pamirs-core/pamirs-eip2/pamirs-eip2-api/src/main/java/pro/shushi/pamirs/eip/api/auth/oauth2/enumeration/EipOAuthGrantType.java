package pro.shushi.pamirs.eip.api.auth.oauth2.enumeration;

/**
 * @author Adamancy Zhang at 23:35 on 2021-02-03
 */
public enum EipOAuthGrantType {

    AUTHORIZATION_CODE("authorization_code"),
    REFRESH_TOKEN("refresh_token"),
    ;

    EipOAuthGrantType(String value) {
        this.value = value;
    }

    private final String value;

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}