package pro.shushi.pamirs.sso.api.service;

import pro.shushi.pamirs.sso.api.dto.SsoRequestParameters;
import pro.shushi.pamirs.sso.api.dto.SsoUserVo;
import pro.shushi.pamirs.sso.api.tmodel.ApiCommonTransient;
import pro.shushi.pamirs.sso.api.utils.OAuthTokenResponse;
import pro.shushi.pamirs.sso.api.utils.Result;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface SsoTokenService {

    OAuthTokenResponse authorize(SsoRequestParameters ssoRequestParameters);

    void login(SsoUserVo ssoUserVo);

    Result getPrivateKey(String username) throws NoSuchAlgorithmException;


    OAuthTokenResponse refresh(SsoRequestParameters ssoRequestParameters);

    ApiCommonTransient getUserInfo(Map<String, Object> map);


    void logout(Map<String, Object> map);
}
