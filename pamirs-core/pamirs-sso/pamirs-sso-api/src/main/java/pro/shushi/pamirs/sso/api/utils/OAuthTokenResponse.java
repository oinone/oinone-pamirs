package pro.shushi.pamirs.sso.api.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import pro.shushi.pamirs.meta.annotation.fun.Data;

@Data
public class OAuthTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("refresh_token_expiresIn")
    private Long refreshTokenExpiresIn;


}