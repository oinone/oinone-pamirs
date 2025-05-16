package pro.shushi.pamirs.auth.api.runtime.cache.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.enumeration.authorized.RowAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.runtime.cache.DataPermissionCacheApi;
import pro.shushi.pamirs.auth.api.runtime.cache.fast.AuthL2Cache;
import pro.shushi.pamirs.auth.api.utils.AuthAccessPermissionCacheHelper;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 默认数据权限缓存实现
 *
 * @author Adamancy Zhang at 15:48 on 2024-01-10
 */
@Order
@Component
@SPI.Service
public class DefaultDataPermissionCache implements DataPermissionCacheApi {

    @Override
    public AuthResult<Long> fetchModelPermission(Set<Long> roleIds, String model) {
        return AuthAccessPermissionCacheHelper.collectionModelPermissionResult(AuthL2Cache.getRoleModelPermissions(roleIds, model));
    }

    @Override
    public AuthResult<Map<String, Long>> fetchModelPermissionBatch(Set<Long> roleIds, List<String> models) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AuthResult<Map<String, Long>> fetchFieldPermissions(Set<Long> roleIds, String model) {
        return AuthAccessPermissionCacheHelper.collectionFieldPermissionResult(AuthL2Cache.getRoleFieldPermissions(roleIds, model));
    }

    @Override
    public AuthResult<Map<String, Map<String, Long>>> fetchFieldPermissionsBatch(Set<Long> roleIds, List<String> models) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AuthResult<Set<String>> fetchRowPermissionsForRead(Set<Long> roleIds, String model) {
        return AuthAccessPermissionCacheHelper.collectionRowPermissionResult(AuthL2Cache.getRoleRowPermissions(roleIds, model, RowAuthorizedValueEnum.READ));
    }

    @Override
    public AuthResult<Set<String>> fetchRowPermissionsForWrite(Set<Long> roleIds, String model) {
        return AuthAccessPermissionCacheHelper.collectionRowPermissionResult(AuthL2Cache.getRoleRowPermissions(roleIds, model, RowAuthorizedValueEnum.WRITE));
    }

    @Override
    public AuthResult<Set<String>> fetchRowPermissionsForDelete(Set<Long> roleIds, String model) {
        return AuthAccessPermissionCacheHelper.collectionRowPermissionResult(AuthL2Cache.getRoleRowPermissions(roleIds, model, RowAuthorizedValueEnum.DELETE));
    }
}
