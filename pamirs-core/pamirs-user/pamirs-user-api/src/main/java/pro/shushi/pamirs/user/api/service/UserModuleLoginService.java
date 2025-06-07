package pro.shushi.pamirs.user.api.service;

import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 单个module的默认的homepage
 *
 * @author shier
 * date  2021/3/5 5:20 下午
 */
@Deprecated
@SPI(factory = SpringServiceLoaderFactory.class)
public interface UserModuleLoginService {

    /**
     * 登录成功后默认跳转的位置
     *
     * @return
     */
    ViewAction afterLoginRedirectViewAction();

    /**
     * 应用的module
     *
     * @return
     */
    String module();

}
