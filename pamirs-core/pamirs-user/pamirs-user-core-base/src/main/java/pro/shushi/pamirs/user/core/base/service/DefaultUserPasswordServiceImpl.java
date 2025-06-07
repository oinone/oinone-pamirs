package pro.shushi.pamirs.user.core.base.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.UserPasswordService;

@Deprecated
@Slf4j
@Order //默认优先级最低，业务配置需要配置成为优先级高
@Component
@SPI.Service
public class DefaultUserPasswordServiceImpl implements UserPasswordService {

    /**
     * 修改密码的自定义操作
     *
     * @return
     */
    @Override
    public PamirsUser changePassword(Long uid, String oldPasswordEncode, String newPassword, String newPasswordEncode) {
        return null;//啥也不做
    }

}
