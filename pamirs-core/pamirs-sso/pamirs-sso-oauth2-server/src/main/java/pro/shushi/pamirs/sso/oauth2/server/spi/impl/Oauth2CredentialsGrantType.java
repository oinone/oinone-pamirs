package pro.shushi.pamirs.sso.oauth2.server.spi.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.sso.api.dto.SsoRequestParameters;
import pro.shushi.pamirs.sso.api.enmu.SsoGranTypeEnum;
import pro.shushi.pamirs.sso.api.utils.OAuthTokenResponse;
import pro.shushi.pamirs.sso.oauth2.server.spi.IUserLoginOAuth2GrantType;

@Component
@SPI.Service("client_credentials")
@Order
public class Oauth2CredentialsGrantType implements IUserLoginOAuth2GrantType {

    @Override
    public boolean match(SsoRequestParameters ssoRequestParameters) {
        return SsoGranTypeEnum.CREDENTIALS.getType().equals(ssoRequestParameters.getGrant_type());
    }

    @Override
    public OAuthTokenResponse execute(SsoRequestParameters ssoRequestParameters) {
        // 处理 CREDENTIALS 授权类型的逻辑
        OAuthTokenResponse oAuthTokenResponse = new OAuthTokenResponse();
        return oAuthTokenResponse;
    }

}
