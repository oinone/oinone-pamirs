package pro.shushi.pamirs.auth.api.extend.cache;

import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthModelAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;
import java.util.Set;

/**
 * 权限缓存扩展API
 *
 * @author Adamancy Zhang at 09:34 on 2024-02-28
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthPermissionCacheExtendApi {

    default void authorizeModulePermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> moduleAuthorizations, boolean override) {
    }

    default void authorizeHomepagePermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> homepageAuthorizations, boolean override) {
    }

    default void authorizeMenuPermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> menuAuthorizations, boolean override) {
    }

    default void authorizeActionPermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> actionAuthorizations, boolean override) {
    }

    default void authorizeModelPermissionRefreshCache(Set<Long> roleIds, List<AuthModelAuthorization> modelAuthorizations) {
    }

    default void authorizeFieldPermissionRefreshCache(Set<Long> roleIds, List<AuthFieldAuthorization> fieldAuthorizations, boolean override) {
    }

    default void authorizeRowPermissionRefreshCache(Set<Long> roleIds, List<AuthRowAuthorization> rowAuthorizations, boolean override) {
    }

    default void authorizeModulePermissionRefreshCache(List<AuthResourceAuthorization> moduleAuthorizations, boolean override) {
    }

    default void authorizeHomepagePermissionRefreshCache(List<AuthResourceAuthorization> homepageAuthorizations, boolean override) {
    }

    default void authorizeMenuPermissionRefreshCache(List<AuthResourceAuthorization> menuAuthorizations, boolean override) {
    }

    default void authorizeActionPermissionRefreshCache(List<AuthResourceAuthorization> actionAuthorizations, boolean override) {
    }

    default void authorizeModelPermissionRefreshCache(List<AuthModelAuthorization> modelAuthorizations) {
    }

    default void authorizeFieldPermissionRefreshCache(List<AuthFieldAuthorization> fieldAuthorizations, boolean override) {
    }

    default void authorizeRowPermissionRefreshCache(List<AuthRowAuthorization> rowAuthorizations, boolean override) {
    }

    default void revokeModulePermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> moduleAuthorizations, boolean isDelete) {
    }

    default void revokeHomepagePermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> homepageAuthorizations, boolean isDelete) {
    }

    default void revokeMenuPermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> menuAuthorizations, boolean isDelete) {
    }

    default void revokeActionPermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> actionAuthorizations, boolean isDelete) {
    }

    default void revokeModelPermissionRefreshCache(Set<Long> roleIds, List<AuthModelAuthorization> modelAuthorizations, boolean isDelete) {
    }

    default void revokeFieldPermissionRefreshCache(Set<Long> roleIds, List<AuthFieldAuthorization> fieldAuthorizations, boolean isDelete) {
    }

    default void revokeRowPermissionRefreshCache(Set<Long> roleIds, List<AuthRowAuthorization> rowAuthorizations, boolean isDelete) {
    }

    default void revokeModulePermissionRefreshCache(List<AuthResourceAuthorization> moduleAuthorizations, boolean isDelete) {
    }

    default void revokeHomepagePermissionRefreshCache(List<AuthResourceAuthorization> homepageAuthorizations, boolean isDelete) {
    }

    default void revokeMenuPermissionRefreshCache(List<AuthResourceAuthorization> menuAuthorizations, boolean isDelete) {
    }

    default void revokeActionPermissionRefreshCache(List<AuthResourceAuthorization> actionAuthorizations, boolean isDelete) {
    }

    default void revokeModelPermissionRefreshCache(List<AuthModelAuthorization> modelAuthorizations, boolean isDelete) {
    }

    default void revokeFieldPermissionRefreshCache(List<AuthFieldAuthorization> fieldAuthorizations, boolean isDelete) {
    }

    default void revokeRowPermissionRefreshCache(List<AuthRowAuthorization> rowAuthorizations, boolean isDelete) {
    }
}
