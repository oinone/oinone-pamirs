package pro.shushi.pamirs.sso.api.service;

import pro.shushi.pamirs.sso.api.dto.SsoRequestParameters;
import pro.shushi.pamirs.sso.api.dto.SsoUserVo;
import pro.shushi.pamirs.sso.api.model.SsoClient;
import pro.shushi.pamirs.sso.api.tmodel.OAuthTokenResponse;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.Map;

public interface SsoOauth2TokenService {

    OAuthTokenResponse authorize(SsoRequestParameters ssoRequestParameters);

    void login(SsoUserVo ssoUserVo);

    OAuthTokenResponse refresh(SsoRequestParameters ssoRequestParameters);

    PamirsUser getUserInfo(String clientId);

    void logout(SsoRequestParameters ssoRequestParameters);

    String getOauth2Code(SsoClient ssoClient, Long userId);
}
