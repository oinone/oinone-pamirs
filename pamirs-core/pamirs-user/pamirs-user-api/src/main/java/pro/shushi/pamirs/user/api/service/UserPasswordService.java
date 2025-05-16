package pro.shushi.pamirs.user.api.service;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.user.api.model.PamirsUser;

@Deprecated
@SPI(factory = SpringServiceLoaderFactory.class)
public interface UserPasswordService {

    PamirsUser changePassword(Long uid, String oldPasswordEncode, String newPassword, String newPasswordEncode);

}