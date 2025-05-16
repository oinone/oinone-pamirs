package pro.shushi.pamirs.sso.api.enmu;

/**
 * @author Adamancy Zhang on 2021-02-05 16:00
 */
public enum SsoOAuthAuthorizationPrefix {

    BASIC("Basic "),
    BEARER("Bearer "),
    CAK("CAK "),
    ;

    private final String value;

    SsoOAuthAuthorizationPrefix(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
