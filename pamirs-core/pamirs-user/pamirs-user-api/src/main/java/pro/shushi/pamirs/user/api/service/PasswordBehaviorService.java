package pro.shushi.pamirs.user.api.service;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;

/**
 * @author shier
 * date  2022/6/7 下午1:59
 */
@SPI(PasswordBehaviorService.DEFAULT_SERVICE)
public interface PasswordBehaviorService {

    public static final String DEFAULT_SERVICE = "PasswordBehaviorService";

    void modifyPassword(PamirsUserTransient user);

    void firstResetPassword(PamirsUserTransient user);
}
