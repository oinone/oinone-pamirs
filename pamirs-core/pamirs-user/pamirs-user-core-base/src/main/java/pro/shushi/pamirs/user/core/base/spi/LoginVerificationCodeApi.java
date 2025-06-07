package pro.shushi.pamirs.user.core.base.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

@SPI(factory = SpringServiceLoaderFactory.class)
public interface LoginVerificationCodeApi {

    Boolean checkVerificationCode(String verificationCode);

}
