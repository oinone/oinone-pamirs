package pro.shushi.pamirs.auth.api.runtime.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Set;

/**
 * 获取角色列表
 *
 * @author Adamancy Zhang at 17:46 on 2024-01-06
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface CurrentRolesFetcher {

    /**
     * 获取角色列表
     *
     * @return 角色列表
     */
    Set<Long> fetch();
}
