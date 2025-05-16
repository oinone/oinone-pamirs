package pro.shushi.pamirs.auth.api.runtime.spi;

import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Set;

/**
 * 获取权限API
 *
 * @author Adamancy Zhang at 10:48 on 2024-01-29
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface FetchPermissionApi {

    <R> AuthResult<R> fetch(FetchPermissions<R> fetcher);

    <R> AuthResult<R> fetchByRole(FetchPermissionsByRole<R> fetcher);

    @FunctionalInterface
    interface FetchPermissions<R> {

        AuthResult<R> apply(AccessResourceInfo accessInfo, Set<Long> roleIds);
    }

    @FunctionalInterface
    interface FetchPermissionsByRole<R> {

        AuthResult<R> apply(Set<Long> roleIds);
    }
}
