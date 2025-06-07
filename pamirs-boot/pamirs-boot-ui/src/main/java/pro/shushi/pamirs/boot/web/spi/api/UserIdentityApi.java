package pro.shushi.pamirs.boot.web.spi.api;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 用户身份API
 *
 * @author Adamancy Zhang at 10:14 on 2024-04-10
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface UserIdentityApi {

    /**
     * <h3>是否为超级管理员</h3>
     * <p>
     * PS: 超级管理员完全无权限控制
     * </p>
     *
     * @return 是否为超级管理员
     */
    boolean isAdmin();

    /**
     * <h3>是否为匿名用户</h3>
     * <p>
     * PS: 匿名用户统一使用独立权限
     * </p>
     *
     * @return 是否为匿名用户
     */
    boolean isAnonymous();

}
