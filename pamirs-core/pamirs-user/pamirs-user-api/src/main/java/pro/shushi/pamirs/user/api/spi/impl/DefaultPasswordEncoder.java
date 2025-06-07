package pro.shushi.pamirs.user.api.spi.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.user.api.spi.PasswordEncoder;

/**
 * 默认密码加密服务
 *
 * @author Adamancy Zhang at 18:14 on 2024-01-12
 */
@Order
@Component
@SPI.Service
public class DefaultPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return pro.shushi.pamirs.user.api.utils.PasswordEncoder.encode(rawPassword);
    }

    @Override
    public Boolean matches(CharSequence rawPassword, String encodedPassword) {
        return pro.shushi.pamirs.user.api.utils.PasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
