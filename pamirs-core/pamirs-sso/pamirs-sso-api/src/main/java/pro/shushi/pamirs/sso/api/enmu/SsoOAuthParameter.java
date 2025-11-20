package pro.shushi.pamirs.sso.api.enmu;


/**
 * @author Adamancy Zhang on 2021-02-04 00:31
 */
@Deprecated
public enum SsoOAuthParameter {

    CLIENT_ID("client_id", "clientId"),
    CLIENT_SECRET("client_secret", "clientSecret"),
    APPLICATION_KEY("app_key", "appKey"),
    APPLICATION_SECRET("app_secret", "appSecret"),
    RESPONSE_TYPE("response_type", "responseType"),
    REDIRECT_URI("redirect_uri", "redirectUri"),
    SCOPE("scope", "scope"),
    STATE("state", "state"),
    PROMPT("prompt", "prompt"),
    GRANT_TYPE("grant_type", "grantType"),
    CODE("code", "code"),
    AUTHORIZATION("Authorization", "authorization"),

    ACCESS_TOKEN("access_token", "accessToken"),
    EXPIRES_IN("expires_in", "expiresIn"),
    REFRESH_TOKEN("refresh_token", "refreshToken"),
    REFRESH_TOKEN_EXPIRES_IN("refresh_token_expires_in", "refreshTokenExpiresIn"),
    TOKEN_TYPE("token_type", "tokenType"),
    ;

    private final String origin;

    private final String target;

    SsoOAuthParameter(String origin, String target) {
        this.origin = origin;
        this.target = target;
    }

}
