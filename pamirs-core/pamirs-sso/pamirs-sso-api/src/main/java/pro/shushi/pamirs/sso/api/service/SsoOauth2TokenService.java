package pro.shushi.pamirs.sso.api.service;

import pro.shushi.pamirs.sso.api.model.SsoClient;
import pro.shushi.pamirs.sso.common.dto.OAuthTokenResponse;
import pro.shushi.pamirs.sso.common.dto.SsoRequestParameter;
import pro.shushi.pamirs.sso.common.dto.SsoUserInfo;
import pro.shushi.pamirs.sso.common.dto.SsoUserVo;

public interface SsoOauth2TokenService {

    OAuthTokenResponse authorize(SsoRequestParameter ssoRequestParameter);

    void login(SsoUserVo ssoUserVo);

    OAuthTokenResponse refresh(SsoRequestParameter ssoRequestParameter);

    SsoUserInfo getUserInfo(String clientId);

    void logout(SsoRequestParameter ssoRequestParameter);

    String getOauth2Code(SsoClient ssoClient, Long userId);
}
