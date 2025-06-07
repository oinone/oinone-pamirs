package pro.shushi.pamirs.eip.api.auth.oauth2.enumeration;

/**
 * @author Adamancy Zhang on 2021-02-05 16:00
 */
public enum EipOAuthAuthorizationPrefix {

    BASIC("Basic "),
    BEARER("Bearer "),
    CAK("CAK "),
    ;

    private final String value;

    EipOAuthAuthorizationPrefix(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
