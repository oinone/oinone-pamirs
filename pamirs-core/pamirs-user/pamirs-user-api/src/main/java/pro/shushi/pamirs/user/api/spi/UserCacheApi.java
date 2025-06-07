package pro.shushi.pamirs.user.api.spi;

import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.user.api.constants.UserConstant;

/**
 * 用户Session缓存API
 *
 * @author Adamancy Zhang at 15:49 on 2024-06-15
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface UserCacheApi {

    /**
     * 获取用户Session
     *
     * @param key SessionKey
     * @return 用户信息
     */
    PamirsUserDTO getSessionUser(String key);

    /**
     * 设置用户Session（使用默认过期时间）
     *
     * @param key  SessionKey
     * @param user 用户信息
     */
    default void setSessionUser(String key, PamirsUserDTO user) {
        setSessionUser(key, user, UserConstant.USER_EXPIRE_TIME);
    }

    /**
     * 设置用户Session并指定过期时间
     *
     * @param key    SessionKey
     * @param user   用户信息
     * @param expire 过期时间
     */
    void setSessionUser(String key, PamirsUserDTO user, Integer expire);

    /**
     * 清除指定用户Session（一般用于登出）
     *
     * @param key SessionKey
     */
    void clearSessionUser(String key);

    /**
     * 清除指定用户的全部用户Session（在多用户登录的情况下，让所有用户同时登出）
     *
     * @param userId 用户ID
     */
    void clearSessionUserByUserId(Long userId);
}
