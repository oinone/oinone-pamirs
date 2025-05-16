package pro.shushi.pamirs.user.api.service;

import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * @author shier
 * date  2021/3/5 5:20 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface UserLoginService {

    /**
     * 登录成功后默认跳转的位置
     * @return
     */
    ViewAction afterLoginRedirectViewAction();

}
