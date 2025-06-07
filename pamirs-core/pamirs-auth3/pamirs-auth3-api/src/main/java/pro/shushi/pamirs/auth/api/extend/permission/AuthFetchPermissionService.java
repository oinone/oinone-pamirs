package pro.shushi.pamirs.auth.api.extend.permission;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Set;

/**
 * 获取动作权限扩展API
 *
 * @author Adamancy Zhang at 12:37 on 2025-02-17
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthFetchPermissionService {

    default Set<String> fetchModulePermissions(Set<String> result, Set<Long> roleIds) {
        return null;
    }

    default Set<String> fetchHomepagePermissions(Set<String> result, Set<Long> roleIds) {
        return null;
    }

    default Set<String> fetchMenuPermissions(Set<String> result, Set<Long> roleIds) {
        return null;
    }

    default Set<String> fetchActionPermissions(Set<String> result, Set<Long> roleIds) {
        return null;
    }
}
