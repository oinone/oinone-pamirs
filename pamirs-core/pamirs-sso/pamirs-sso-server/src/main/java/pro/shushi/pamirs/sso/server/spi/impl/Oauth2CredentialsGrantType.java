package pro.shushi.pamirs.sso.server.spi.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.sso.api.dto.SsoRequestParameter;
import pro.shushi.pamirs.sso.api.enmu.SsoGranTypeEnum;
import pro.shushi.pamirs.sso.api.dto.OAuthTokenResponse;
import pro.shushi.pamirs.sso.server.spi.IUserLoginOAuth2GrantType;

@Component
@SPI.Service("client_credentials")
@Order
public class Oauth2CredentialsGrantType implements IUserLoginOAuth2GrantType {

    @Override
    public boolean match(SsoRequestParameter ssoRequestParameter) {
        return SsoGranTypeEnum.CREDENTIALS.getType().equals(ssoRequestParameter.getGrant_type());
    }

    @Override
    public OAuthTokenResponse execute(SsoRequestParameter ssoRequestParameter) {
        // 处理 CREDENTIALS 授权类型的逻辑
        OAuthTokenResponse oAuthTokenResponse = new OAuthTokenResponse();
        return oAuthTokenResponse;
    }

}
