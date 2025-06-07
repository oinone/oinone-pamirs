package pro.shushi.pamirs.auth.api.runtime.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Set;

/**
 * 自定义获取当前角色（不使用平台内置缓存服务）
 *
 * @author Adamancy Zhang at 11:03 on 2024-04-25
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface CustomCurrentRolesFetcher {

    /**
     * 获取角色列表
     *
     * @return 角色列表
     */
    Set<Long> fetch();
}
