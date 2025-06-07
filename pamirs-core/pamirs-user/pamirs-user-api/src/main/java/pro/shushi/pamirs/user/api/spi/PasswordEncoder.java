package pro.shushi.pamirs.user.api.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 密码加密服务
 *
 * @author Adamancy Zhang at 17:35 on 2024-01-12
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface PasswordEncoder {

    String encode(CharSequence rawPassword);

    Boolean matches(CharSequence rawPassword, String encodedPassword);
}
