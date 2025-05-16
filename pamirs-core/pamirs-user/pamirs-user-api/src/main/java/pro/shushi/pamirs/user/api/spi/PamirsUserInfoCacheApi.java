package pro.shushi.pamirs.user.api.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.user.api.model.PamirsUser;

/**
 * 用户信息缓存SPI
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface PamirsUserInfoCacheApi {

    /**
     * 缓存初始化
     */
    void init();

    /**
     * 根据用户ID从缓存中获取指定用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    PamirsUser queryUserById(Long userId);

    /**
     * 更新缓存用户信息
     *
     * @param user 用户信息
     */
    void putUserInfo(PamirsUser user);

    /**
     * 根据用户ID清理指定用户信息
     *
     * @param userId 用户ID
     */
    void clearUserById(Long userId);

}
