package pro.shushi.pamirs.sso.oauth2.server.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.sso.api.dto.SsoRequestParameter;
import pro.shushi.pamirs.sso.api.dto.OAuthTokenResponse;

@SPI(factory = SpringServiceLoaderFactory.class)
public interface IOAuth2RefreshToken {

    OAuthTokenResponse execute(SsoRequestParameter ssoRequestParameter);
}
