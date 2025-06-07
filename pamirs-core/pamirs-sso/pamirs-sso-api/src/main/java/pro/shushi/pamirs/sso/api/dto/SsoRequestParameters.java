package pro.shushi.pamirs.sso.api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import pro.shushi.pamirs.meta.annotation.fun.Data;

@Data
public class SsoRequestParameters {

    @JsonProperty("grant_type")
    private String grant_type;

    private String username;

    private String password;

    private String scope;

    @JsonProperty("client_id")
    private String client_id;

    @JsonProperty("client_secret")
    private String client_secret;

    private String code;

    @JsonProperty("redirect_uri")
    private String redirect_uri;

    @JsonProperty("code_verifier")
    private String code_verifier;

    @JsonProperty("refresh_token")
    private String refresh_token;

}

