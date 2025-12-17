package pro.shushi.pamirs.auth.api.holder;

import pro.shushi.pamirs.auth.api.cache.service.*;
import pro.shushi.pamirs.auth.api.extend.permission.AuthFetchPermissionService;
import pro.shushi.pamirs.auth.api.runtime.cache.AccessPermissionCacheApi;
import pro.shushi.pamirs.auth.api.runtime.cache.DataPermissionCacheApi;
import pro.shushi.pamirs.auth.api.runtime.cache.ManagementPermissionCacheApi;
import pro.shushi.pamirs.auth.api.runtime.spi.*;
import pro.shushi.pamirs.ux.common.entity.HoldSupplier;

import java.util.List;

/**
 * 权限API持有者
 *
 * @author Adamancy Zhang at 12:20 on 2024-10-24
 */
public class AuthApiHolder {

    private AuthApiHolder() {
        // reject create object
    }

    private static final HoldSupplier<CurrentRolesCacheApi> currentRolesCacheApiHolder = HoldSupplier.getDefaultExtension(CurrentRolesCacheApi.class);

    public static CurrentRolesCacheApi getCurrentRolesCacheApi() {
        return currentRolesCacheApiHolder.get();
    }

    private static final HoldSupplier<CurrentRolesFetcherApi> currentRolesFetcherApiHolder = HoldSupplier.getDefaultExtension(CurrentRolesFetcherApi.class);

    public static CurrentRolesFetcherApi getCurrentRolesFetcherApi() {
        return currentRolesFetcherApiHolder.get();
    }

    private static final HoldSupplier<FetchPermissionApi> fetchPermissionApiHolder = HoldSupplier.getDefaultExtension(FetchPermissionApi.class);

    public static FetchPermissionApi getFetchPermissionApi() {
        return fetchPermissionApiHolder.get();
    }

    private static final HoldSupplier<AccessPermissionApi> accessPermissionApiHolder = HoldSupplier.getDefaultExtension(AccessPermissionApi.class);

    public static AccessPermissionApi getAccessPermissionApi() {
        return accessPermissionApiHolder.get();
    }

    private static final HoldSupplier<AccessPermissionCacheApi> accessPermissionCacheApiHolder = HoldSupplier.getDefaultExtension(AccessPermissionCacheApi.class);

    public static AccessPermissionCacheApi getAccessPermissionCacheApi() {
        return accessPermissionCacheApiHolder.get();
    }

    private static final HoldSupplier<VerificationPermissionApi> verificationPermissionApiHolder = HoldSupplier.getDefaultExtension(VerificationPermissionApi.class);

    public static VerificationPermissionApi getVerificationPermissionApi() {
        return verificationPermissionApiHolder.get();
    }

    private static final HoldSupplier<DataPermissionApi> dataPermissionApiHolder = HoldSupplier.getDefaultExtension(DataPermissionApi.class);

    public static DataPermissionApi getDataPermissionApi() {
        return dataPermissionApiHolder.get();
    }

    private static final HoldSupplier<DataPermissionCacheApi> dataPermissionCacheApiHolder = HoldSupplier.getDefaultExtension(DataPermissionCacheApi.class);

    public static DataPermissionCacheApi getDataPermissionCacheApi() {
        return dataPermissionCacheApiHolder.get();
    }

    private static final HoldSupplier<ManagementPermissionApi> managementPermissionApiHolder = HoldSupplier.getDefaultExtension(ManagementPermissionApi.class);

    public static ManagementPermissionApi getManagementPermissionApi() {
        return managementPermissionApiHolder.get();
    }

    private static final HoldSupplier<ManagementPermissionCacheApi> managementPermissionCacheApiHolder = HoldSupplier.getDefaultExtension(ManagementPermissionCacheApi.class);

    public static ManagementPermissionCacheApi getManagementPermissionCacheApi() {
        return managementPermissionCacheApiHolder.get();
    }

    private static final HoldSupplier<AuthPathMappingCacheService> authPathMappingCacheServiceHolder = HoldSupplier.getDefaultExtension(AuthPathMappingCacheService.class);

    public static AuthPathMappingCacheService getAuthPathMappingCacheService() {
        return authPathMappingCacheServiceHolder.get();
    }

    private static final HoldSupplier<AuthRoleActionByMenuCacheService> authRoleActionByMenuCacheServiceHolder = HoldSupplier.getDefaultExtension(AuthRoleActionByMenuCacheService.class);

    public static AuthRoleActionByMenuCacheService getAuthRoleActionByMenuCacheService() {
        return authRoleActionByMenuCacheServiceHolder.get();
    }

    private static final HoldSupplier<AuthRoleActionByModelCacheService> authRoleActionByModelCacheServiceHolder = HoldSupplier.getDefaultExtension(AuthRoleActionByModelCacheService.class);

    public static AuthRoleActionByModelCacheService getAuthRoleActionByModelCacheService() {
        return authRoleActionByModelCacheServiceHolder.get();
    }

    private static final HoldSupplier<AuthRoleActionByViewActionCacheService> authRoleActionByViewActionCacheServiceHolder = HoldSupplier.getDefaultExtension(AuthRoleActionByViewActionCacheService.class);

    public static AuthRoleActionByViewActionCacheService getAuthRoleActionByViewActionCacheService() {
        return authRoleActionByViewActionCacheServiceHolder.get();
    }

    private static final HoldSupplier<AuthRoleActionByViewCacheService> authRoleActionByViewCacheServiceHolder = HoldSupplier.getDefaultExtension(AuthRoleActionByViewCacheService.class);

    public static AuthRoleActionByViewCacheService getAuthRoleActionByViewCacheService() {
        return authRoleActionByViewCacheServiceHolder.get();
    }

    private static final HoldSupplier<AuthRoleFieldCacheService> authRoleFieldCacheServiceHolder = HoldSupplier.getDefaultExtension(AuthRoleFieldCacheService.class);

    public static AuthRoleFieldCacheService getAuthRoleFieldCacheService() {
        return authRoleFieldCacheServiceHolder.get();
    }

    private static final HoldSupplier<AuthRoleHomepageCacheService> authRoleHomepageCacheServiceHolder = HoldSupplier.getDefaultExtension(AuthRoleHomepageCacheService.class);

    public static AuthRoleHomepageCacheService getAuthRoleHomepageCacheService() {
        return authRoleHomepageCacheServiceHolder.get();
    }

    private static final HoldSupplier<AuthRoleMenuCacheService> authRoleMenuCacheServiceHolder = HoldSupplier.getDefaultExtension(AuthRoleMenuCacheService.class);

    public static AuthRoleMenuCacheService getAuthRoleMenuCacheService() {
        return authRoleMenuCacheServiceHolder.get();
    }

    private static final HoldSupplier<AuthRoleModelCacheService> authRoleModelCacheServiceHolder = HoldSupplier.getDefaultExtension(AuthRoleModelCacheService.class);

    public static AuthRoleModelCacheService getAuthRoleModelCacheService() {
        return authRoleModelCacheServiceHolder.get();
    }

    private static final HoldSupplier<AuthRoleModuleCacheService> authRoleModuleCacheServiceHolder = HoldSupplier.getDefaultExtension(AuthRoleModuleCacheService.class);

    public static AuthRoleModuleCacheService getAuthRoleModuleCacheService() {
        return authRoleModuleCacheServiceHolder.get();
    }

    private static final HoldSupplier<AuthRoleRowCacheService> authRoleRowCacheServiceHolder = HoldSupplier.getDefaultExtension(AuthRoleRowCacheService.class);

    public static AuthRoleRowCacheService getAuthRoleRowCacheService() {
        return authRoleRowCacheServiceHolder.get();
    }

    private static final HoldSupplier<AuthSharedCodeCacheService> authSharedCodeCacheServiceHolder = HoldSupplier.getDefaultExtension(AuthSharedCodeCacheService.class);

    public static AuthSharedCodeCacheService getAuthSharedCodeCacheService() {
        return authSharedCodeCacheServiceHolder.get();
    }

    private static final HoldSupplier<AuthSharedPageCacheService> authSharedPageCacheServiceHolder = HoldSupplier.getDefaultExtension(AuthSharedPageCacheService.class);

    public static AuthSharedPageCacheService getAuthSharedPageCacheService() {
        return authSharedPageCacheServiceHolder.get();
    }

    private static final HoldSupplier<AuthUserRoleCacheService> authUserRoleCacheServiceHolder = HoldSupplier.getDefaultExtension(AuthUserRoleCacheService.class);

    public static AuthUserRoleCacheService getAuthUserRoleCacheService() {
        return authUserRoleCacheServiceHolder.get();
    }

    private static final HoldSupplier<List<AuthFetchPermissionService>> authFetchPermissionServicesHolder = HoldSupplier.getOrderedExtensions(AuthFetchPermissionService.class);

    public static List<AuthFetchPermissionService> getAuthFetchPermissionServices() {
        return authFetchPermissionServicesHolder.get();
    }
}
