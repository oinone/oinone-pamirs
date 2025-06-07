package pro.shushi.pamirs.user.core.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.user.api.spi.UserPatternCheckApi;

@Order
@Component
@SPI.Service
public class DefaultUserPatternCheckApi implements UserPatternCheckApi {

    @Override
    public Boolean checkNickName(String nickname) {
        // 先不校验,保持原样
        return Boolean.TRUE;
    }

    @Override
    public Boolean checkRealName(String realname) {
        // 先不校验,保持原样
        return Boolean.TRUE;
    }
}
