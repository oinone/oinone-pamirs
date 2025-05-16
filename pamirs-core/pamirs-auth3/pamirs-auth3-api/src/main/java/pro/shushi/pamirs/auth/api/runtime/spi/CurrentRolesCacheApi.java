package pro.shushi.pamirs.auth.api.runtime.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Set;

/**
 * 当前角色缓存API
 *
 * @author Adamancy Zhang at 18:28 on 2024-01-06
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface CurrentRolesCacheApi {

    /**
     * 获取角色缓存
     *
     * @return 角色列表
     */
    Set<Long> get();

    /**
     * 设置角色缓存
     *
     * @param roleIds 角色列表
     */
    void set(Set<Long> roleIds);
}
