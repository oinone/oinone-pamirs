package pro.shushi.pamirs.user.api.spi;

import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 用户会话API
 *
 * @author Adamancy Zhang at 13:46 on 2025-10-20
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface UserSessionApi {

    /**
     * 设置当前用户
     *
     * @param function 当前请求函数
     * @param args     当前请求参数
     */
    void login(Function function, Object... args);

    /**
     * 登出
     */
    void logout();

    /**
     * 根据用户设置环境变量
     *
     * @param user 当前登录用户
     */
    void putEnv(PamirsUserDTO user);

    /**
     * 无用户时设置环境变量
     */
    void putEnv();
}
