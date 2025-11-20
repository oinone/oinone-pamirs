package pro.shushi.pamirs.sso.api.service;

import pro.shushi.pamirs.sso.api.model.SsoClient;
import pro.shushi.pamirs.sso.common.dto.OAuthTokenResponse;
import pro.shushi.pamirs.sso.common.dto.SsoRequestParameter;
import pro.shushi.pamirs.sso.common.dto.SsoUserVo;
import pro.shushi.pamirs.user.api.model.PamirsUser;

public interface SsoOauth2TokenService {

    OAuthTokenResponse authorize(SsoRequestParameter ssoRequestParameter);

    void login(SsoUserVo ssoUserVo);

    OAuthTokenResponse refresh(SsoRequestParameter ssoRequestParameter);

    PamirsUser getUserInfo(String clientId);

    void logout(SsoRequestParameter ssoRequestParameter);

    String getOauth2Code(SsoClient ssoClient, Long userId);
}
