package pro.shushi.pamirs.sso.server.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.sso.common.dto.OAuthTokenResponse;
import pro.shushi.pamirs.sso.common.dto.SsoRequestParameter;

@SPI(factory = SpringServiceLoaderFactory.class)
public interface IOAuth2RefreshToken {

    OAuthTokenResponse execute(SsoRequestParameter ssoRequestParameter);
}
